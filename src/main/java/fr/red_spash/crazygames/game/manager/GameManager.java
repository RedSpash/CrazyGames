package fr.red_spash.crazygames.game.manager;

import fr.red_spash.crazygames.Main;
import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.error.ConstructorError;
import fr.red_spash.crazygames.game.interaction.GameInteraction;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import fr.red_spash.crazygames.game.tasks.GameTimer;
import fr.red_spash.crazygames.game.victory.Victory;
import fr.red_spash.crazygames.map.GameMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.awt.Color;
import java.util.*;
import java.util.List;

public class GameManager {
    public static final int TIME_BEFORE_START = 15;
    private boolean autoStart = false;
    private static final int MAX_TIME = 60*4;
    private final List<String> countDown = new ArrayList<>(Arrays.asList("❶","❷","❸","❹","❺","❻","❼","❽","❾","❿"));
    private final ArrayList<GameType> playedGameType = new ArrayList<>();
    private final Main main;
    private final MapManager mapManager;
    private final GameInteraction gameInteraction;
    private final MessageManager messageManager;
    private final PlayerManager playerManager;
    private Game actualGame;
    private BukkitTask taskCountDown;
    private BukkitTask rollGameTask;
    private GameTimer gameTimer;
    private BukkitTask gameTimerTask;



    public GameManager(Main main){
        this.main = main;

        this.messageManager = new MessageManager(this);
        this.playerManager = new PlayerManager(this.messageManager,this);
        this.mapManager = new MapManager(this);
        this.gameInteraction = new GameInteraction(this.main,this);

        this.actualGame = null;
        this.playerManager.fillPlayerData();
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void destroyWorlds() {
        if(this.actualGame != null){
            this.actualGame.getGameMap().deleteWorld();
        }
    }

    public void startRandomGame(){
        ArrayList<GameType> availableGameType = new ArrayList<>();
        for(GameType gameType : GameType.values()){
            if(!this.playedGameType.contains(gameType)){
                availableGameType.add(gameType);
            }
        }

        if(availableGameType.isEmpty()){
            availableGameType.addAll(Arrays.asList(GameType.values()));
            this.playedGameType.clear();
        }

        this.startAGameType(availableGameType.get(Utils.randomNumber(0,availableGameType.size()-1)));
    }

    public void startAGameType(GameType gameType) {
        if(this.actualGame != null){
            GameStatus gameStatus = this.actualGame.getGameStatus();
            if(gameStatus != GameStatus.WAITING && gameStatus != GameStatus.ENDING)return;
        }

        GameMap randomGameMap = this.mapManager.getRandomMap(gameType);
        this.startAGameMap(randomGameMap);
    }

    public void startAGameMap(GameMap gameMap) {
        if(this.actualGame != null){
            GameStatus gameStatus = this.actualGame.getGameStatus();
            if(gameStatus != GameStatus.WAITING && gameStatus != GameStatus.ENDING)return;
        }
        Bukkit.getServer().setSpawnRadius(0);
        if(Bukkit.getServer().getSpawnRadius() > 0){
            Bukkit.broadcastMessage("§cImpossible de lancer une partie lorsque le rayon de protection du spawn est au dessus de 0 !");
            return;
        }

        this.mapManager.setPlayed(gameMap);

        if(this.actualGame != null){
            World oldWorld = this.actualGame.getGameMap().getWorld();
            if(oldWorld != null){
                Bukkit.getScheduler().runTaskLater(this.main, ()->{
                    Utils.teleportPlayersAndRemoveWorld(oldWorld,false);
                    this.actualGame.getGameMap().deleteWorld(oldWorld);
                } ,10);
            }
        }

        this.playerManager.resetPlayerData();

        try {
            this.actualGame = gameMap.getGameType().createInstance();
        } catch (ConstructorError e) {throw new RuntimeException(e);}
        this.actualGame.loadMap();
        this.actualGame.initializePlayers();
        this.actualGame.setGameStatus(GameStatus.STARTING);
        this.playedGameType.add(gameMap.getGameType());
        this.actualGame.preStart();

        GameType gameType = gameMap.getGameType();
        this.playerManager.calculatePlayerQualifiedOrEliminated(gameType);

        this.startCountdown();
    }

    private void startCountdown() {
       this.gameTimer = new GameTimer(this, GameManager.MAX_TIME);
        if(this.taskCountDown != null){
            this.taskCountDown.cancel();
        }
        this.taskCountDown = Bukkit.getScheduler().runTaskTimer(this.main, new Runnable() {
            int seconds = TIME_BEFORE_START;
            @Override
            public void run() {
                if(seconds == 0){
                    for(Player p : Bukkit.getOnlinePlayers()){
                        p.sendTitle("§a§lC'est parti !","§a§lBonne chance !",0,20*3,10);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,2,2);
                    }
                    actualGame.setGameStatus(GameStatus.PLAYING);
                    actualGame.startGame();
                    gameTimerTask = Bukkit.getScheduler().runTaskTimer(main, gameTimer,0,20);
                    taskCountDown.cancel();
                    return;
                }

                if(seconds <= 5){
                    double ratio = (seconds-1)/9.0;
                    ChatColor chatColor = ChatColor.of(new Color((int) ((1-ratio)*255), (int) (ratio*255),0));
                    for(Player p : Bukkit.getOnlinePlayers()){
                        p.sendTitle(chatColor+countDown.get(seconds-1),"§eLancement de la partie...",0,20*3,10);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,2,0);
                    }
                }
                seconds--;
            }
        },20,20);
    }

    public void stopGame() {
        int qualified = 0;

        if(gameTimerTask != null){
            gameTimerTask.cancel();
        }

        GameType gameType = this.actualGame.getGameType();
        for(Player p : Bukkit.getOnlinePlayers()){
            PlayerData playerData = this.getPlayerData(p.getUniqueId());
            if(playerData.isDead())continue;

            if(gameType.isQualificationMode()){
                if(playerData.isQualified()){
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
                    p.sendTitle(ChatColor.of(new Color(0, 255, 159))+"§lFin du jeu!","§aVous êtes qualifié!",0,20*3,20);
                    qualified++;
                }else{
                    qualified += this.playerManager.removeLife(p);
                }
            }else{
                if(playerData.isEliminated()){
                    this.playerManager.qualifiedWithLifeLost(p,true);
                }else{
                    this.messageManager.sendQualificationTitle(p);
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
                }
                qualified++;
            }
        }

        this.actualGame.setGameStatus(GameStatus.ENDING);
        this.actualGame.unRegisterListeners();
        this.gameInteraction.resetInteractions();

        if(qualified <= 1){
            this.endGame();
        }else{
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::rollGames,4*20L);
        }
    }

    private void endGame() {
        UUID uuid = null;
        for(Player p : Bukkit.getOnlinePlayers()){
            PlayerData playerData = this.getPlayerData(p.getUniqueId());
            if(!playerData.isDead()){
                uuid = playerData.getUuid();
            }
        }

        if(this.actualGame != null){
            World oldWorld = this.actualGame.getGameMap().getWorld();
            if(oldWorld != null){
                Bukkit.getScheduler().runTaskLater(this.main, ()->{
                    Utils.teleportPlayersAndRemoveWorld(oldWorld,false);
                    this.actualGame.getGameMap().deleteWorld(oldWorld);
                } ,10);
            }
        }

        this.gameInteraction.resetInteractions();
        this.actualGame = new Victory(uuid);
        this.actualGame.loadMap(this.mapManager.getVictoryMap());
        this.actualGame.setGameStatus(GameStatus.PLAYING);
        this.actualGame.initializePlayers();
        this.actualGame.startGame();
    }

    public void rollGames() {
        if(this.rollGameTask != null){
            this.rollGameTask.cancel();
        }
        this.rollGameTask = Bukkit.getScheduler().runTaskTimer(this.main, new Runnable() {
            int rollNumber = 0;
            @Override
            public void run() {
                if(rollNumber >= 50){
                    startRandomGame();
                    rollGameTask.cancel();
                    return;
                }
                GameType gameType = GameType.values()[Utils.randomNumber(0,GameType.values().length-1)];
                for(Player p : Bukkit.getOnlinePlayers()){
                    p.sendTitle(ChatColor.of(gameType.getColor())+gameType.getName(),"§2"+gameType.getShortDescription(),0,20,0);
                    p.playSound(p.getLocation(), Sound.BLOCK_CHERRY_WOOD_PRESSURE_PLATE_CLICK_ON,3,0);
                }
                rollNumber ++;
            }
        },1,2);

    }

    public boolean isInWorld(World world) {
        if(this.getActualGame() == null)return false;
        if(this.getActualGame().getGameMap().getWorld() == null)return false;

        return this.getActualGame().getGameMap().getWorld() == world;
    }

    public void updateHidedPlayer(Player p) {
        PlayerData playerData = this.getPlayerData(p.getUniqueId());

        for(Player otherPlayer : Bukkit.getOnlinePlayers()){
            if(!otherPlayer.getUniqueId().equals(p.getUniqueId())){
                if(playerData.isDead() || playerData.isEliminated()){
                    if(otherPlayer.canSee(p)){
                        otherPlayer.hidePlayer(this.main,p);
                    }
                }else{
                    if(!otherPlayer.canSee(p)){
                        otherPlayer.showPlayer(this.main,p);
                    }
                }
            }
        }
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public Plugin getMain() {
        return this.main;
    }

    public GameTimer getGameTimer() {
        return gameTimer;
    }

    public int getMaxTime() {
        return GameManager.MAX_TIME;
    }

    public GameStatus getActualGameStatus() {
        if(this.actualGame == null)return null;
        return this.actualGame.getGameStatus();
    }

    public Location getSpawnLocation() {
        if(this.actualGame == null)return null;
        return this.actualGame.getGameMap().getSpawnLocation();
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public PlayerData getPlayerData(UUID uniqueId) {
        return this.playerManager.getPlayerData(uniqueId);
    }

    public GameInteraction getGameInteractions() {
        return this.gameInteraction;
    }

    public Game getActualGame() {
        return actualGame;
    }

    public void setActualGame(Game game) {
        this.actualGame = game;
    }
}
