package fr.red_spash.crazygames.game.manager;

import fr.red_spash.crazygames.Main;
import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.interaction.GameInteraction;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import fr.red_spash.crazygames.map.GameMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameManager {

    private final HashMap<UUID,PlayerData> playerDataHashMap = new HashMap<>();
    private final List<String> countDown = new ArrayList<>(Arrays.asList("❶","❷","❸","❹","❺","❻","❼","❽","❾","❿"));
    private final ArrayList<GameType> playedGameType = new ArrayList<>();
    private final Main main;
    private final MapManager mapManager;
    private final GameInteraction gameInteraction;
    private final MessageManager messageManager;
    private Game actualGame;
    private BukkitTask taskCountDown;
    private BukkitTask rollGameTask;


    public GameManager(Main main){
        this.fillPlayerData();

        this.main = main;
        this.messageManager = new MessageManager();
        this.mapManager = new MapManager(this);
        this.gameInteraction = new GameInteraction(this.main,this);

    }

    public void fillPlayerData() {
        this.playerDataHashMap.clear();
        for(Player p : Bukkit.getOnlinePlayers()){
            if(!this.playerDataHashMap.containsKey(p.getUniqueId())){
                this.playerDataHashMap.put(p.getUniqueId(),new PlayerData(p.getUniqueId()));
            }else{
                this.playerDataHashMap.get(p.getUniqueId()).reset();
            }
            p.setScoreboard(this.getPlayerData(p.getUniqueId()).getScoreboard().getBoard());

        }
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
        this.mapManager.setPlayed(randomGameMap);

    }

    public void startAGameMap(GameMap gameMap) {
        if(this.actualGame != null){
            GameStatus gameStatus = this.actualGame.getGameStatus();
            if(gameStatus != GameStatus.WAITING && gameStatus != GameStatus.ENDING)return;
        }
        GameMap oldGameMap = null;
        if(this.actualGame != null){
            oldGameMap = this.actualGame.getGameMap();
        }
        if(oldGameMap != null){
            GameMap finalOldGameMap = oldGameMap;
            Bukkit.getScheduler().runTaskLater(this.main, ()->{
                Utils.teleportPlayersAndRemoveWorld(finalOldGameMap.getWorld(),false);
                finalOldGameMap.deleteWorld();
            } ,10);
        }
        this.actualGame = gameMap.getGameType().createInstance();
        this.actualGame.loadMap();
        this.actualGame.initializePlayers();
        this.actualGame.setGameStatus(GameStatus.STARTING);
        this.playedGameType.add(gameMap.getGameType());

        this.startCountdown();




    }

    private void startCountdown() {
        if(this.taskCountDown != null){
            this.taskCountDown.cancel();
        }
        this.taskCountDown = Bukkit.getScheduler().runTaskTimer(this.main, new Runnable() {
            int seconds = 15;
            @Override
            public void run() {

                if(seconds == 0){
                    for(Player p : Bukkit.getOnlinePlayers()){
                        p.sendTitle("§a§lC'est parti !","§a§lBonne chance !",0,20*3,10);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,2,2);
                    }
                    actualGame.setGameStatus(GameStatus.PLAYING);
                    actualGame.startGame();
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


    public List<GameType> getPlayedGameType() {
        return playedGameType;
    }

    public PlayerData getPlayerData(UUID uniqueId) {
        return playerDataHashMap.get(uniqueId);
    }


    public GameInteraction getGameInteractions() {
        return this.gameInteraction;
    }

    public Game getActualGame() {
        return actualGame;
    }


    public void eliminatePlayer(Player p) {
        PlayerData playerData = this.getPlayerData(p.getUniqueId());

        if(this.actualGame != null && this.actualGame.getGameMap().hasCheckpoint() && playerData.getLastCheckPoint() != null){
            p.teleport(playerData.getLastCheckPoint().getCheckPointLocation());
            return;
        }

        if(!playerData.isDead() && !playerData.isQualified()){
            this.killPlayer(p);

            this.stopGame();
        }

    }

    private void stopGame() {
        int qualified = 0;
        GameType gameType = this.actualGame.getGameType();
        for(Player p : Bukkit.getOnlinePlayers()){
            PlayerData playerData = this.getPlayerData(p.getUniqueId());
            if(!gameType.isQualificationMode() && !playerData.isDead()){
                p.sendTitle("§a§lVous êtes qualifié !","§aBien joué !",0,20*3,20);
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
                qualified++;
            }else if(gameType.isQualificationMode() && !playerData.isDead() && !playerData.isQualified()){
                p.sendTitle("§c§lÉliminé !","§cVous avez perdu !",0,20*3,20);
                this.messageManager.sendEliminateMessage(p.getName());
                p.setGameMode(GameMode.SPECTATOR);
                playerData.setDead(true);
            }
            if(gameType.isQualificationMode() && playerData.isQualified()){
                qualified++;
            }
        }

        this.resetPlayerData();
        this.actualGame.setGameStatus(GameStatus.ENDING);
        this.actualGame.unRegisterListeners();
        this.gameInteraction.resetInteractions();

        if(qualified <= 1){
            UUID uuid = null;
            for(PlayerData playerData : this.playerDataHashMap.values()){
                if(!playerData.isDead()){
                    uuid = playerData.getUuid();
                }
            }
            if(uuid != null){
                Player p = Bukkit.getPlayer(uuid);
                this.messageManager.sendVictoryMessage(p.getName());
            }else{
                Bukkit.broadcastMessage("§c§lFIN DE LA PARTIE");
            }

        }else{
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::rollGames,5*20L);
        }
    }

    public void resetPlayerData() {
        for(PlayerData playerData : this.playerDataHashMap.values()){
            playerData.resetGameData();
        }
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
                    p.sendTitle("§a"+gameType.getName(),"§2"+gameType.getShortDescription(),0,20,0);
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK,1,1);
                }
                rollNumber ++;
            }
        },1,2);

    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public void addPlayerData(UUID uniqueId, PlayerData playerData) {
        this.playerDataHashMap.put(uniqueId,playerData);
    }

    public boolean isInWorld(World world) {
        if(this.getActualGame() == null)return false;
        if(this.getActualGame().getGameMap().getWorld() == null)return false;

        return this.getActualGame().getGameMap().getWorld() == world;
    }

    public void qualifiedPlayer(Player p) {
        PlayerData playerData = this.getPlayerData(p.getUniqueId());

        playerData.setQualified(true);
        p.setGameMode(GameMode.SPECTATOR);
        p.sendTitle(ChatColor.of(new Color(0,255,0))+"§lQUALIFIÉ !","§aVous êtes qualifié pour la prochaine épreuve!",0,20*3,20);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);

        int top = 0;
        int alivePlayer = 0;
        for(PlayerData data : this.playerDataHashMap.values()){
            if(data.isQualified()){
                top = top + 1;
            } else if (!data.isDead()) {
                alivePlayer += 1;
            }
        }

        this.messageManager.sendQualificationMessage(p.getName(),top);

        Bukkit.broadcastMessage("alive: "+alivePlayer);
        if(alivePlayer <= 1){
            stopGame();
        }
    }

    public List<PlayerData> getAlivePlayerData(){
        ArrayList<PlayerData> alive = new ArrayList<>(this.playerDataHashMap.values());
        alive.removeIf(PlayerData::isDead);
        return alive;
    }

    public Plugin getMain() {
        return this.main;
    }

    public MessageManager messageManager() {
        return this.messageManager;
    }

    public void killPlayer(Player p) {
        PlayerData playerData = this.getPlayerData(p.getUniqueId());

        p.setGameMode(GameMode.SPECTATOR);
        this.messageManager.sendEliminateMessage(p.getName());
        playerData.setDead(true);
        playerData.setEliminated(true);
        p.sendTitle("§c§lÉliminé !","§cVous avez perdu !",0,20*3,20);
        p.playSound(p.getLocation(), Sound.ENTITY_WITHER_AMBIENT,1,1);
        p.getInventory().clear();
        p.setVelocity((p.getVelocity().multiply(-5)));
        p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN,4,1);

    }

    public List<PlayerData> getQualifiedPlayers() {
        ArrayList<PlayerData> playerData = new ArrayList<>(this.playerDataHashMap.values());
        playerData.removeIf(data -> !data.isQualified());
        return playerData;
    }

    public List<PlayerData> getEliminatedPlayer() {
        ArrayList<PlayerData> playerData = new ArrayList<>(this.playerDataHashMap.values());
        playerData.removeIf(data -> !data.isEliminated());
        return playerData;
    }
}
