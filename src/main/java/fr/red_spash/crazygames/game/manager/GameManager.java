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

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class GameManager {

    private final HashMap<UUID,PlayerData> playerDataHashMap = new HashMap<>();

    private final List<String> countDown = new ArrayList<>(Arrays.asList("❶","❷","❸","❹","❺","❻","❼","❽","❾","❿"));
    private final ArrayList<GameType> playedGameType = new ArrayList<>();
    private final Main main;
    private final MapManager mapManager;
    private GameInteraction gameInteraction;

    private Game actualGame;

    private BukkitTask taskCountDown;
    private BukkitTask rollGameTask;

    public GameManager(Main main){
        this.main = main;
        this.mapManager = new MapManager(this);
        this.gameInteraction = new GameInteraction(this.main,this);
    }



    public void destroyWorlds() {
        this.actualGame.getGameMap().deleteWorld();
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
        this.actualGame = gameMap.getGameType().createInstance();
        this.actualGame.loadMap();
        this.actualGame.initializePlayers();
        this.actualGame.setGameStatus(GameStatus.STARTING);

        this.startCountdown();

        if(oldGameMap != null){
            Utils.teleportPlayersAndRemoveWorld(oldGameMap.getWorld(),false);
            Bukkit.getScheduler().runTaskLater(this.main, oldGameMap::deleteWorld,10);
        }


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
            p.setGameMode(GameMode.SPECTATOR);
            Bukkit.broadcastMessage("§d§l"+Main.PREFIX+" §6§l"+Main.SEPARATOR+" §c"+p.getName()+ChatColor.of(new Color(255,0,0))+" vient d'être éliminé !");
            playerData.setDead(true);
            p.getInventory().clear();
            p.setVelocity((p.getVelocity().multiply(-5)));
            p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN,4,1);

            this.stopGame();
        }

    }

    private void stopGame() {
        int qualified = 0;
        GameType gameType = this.actualGame.getGameType();
        for(Player p : Bukkit.getOnlinePlayers()){
            PlayerData playerData = this.getPlayerData(p.getUniqueId());
            if((gameType.isQualificationMode() && playerData.isQualified()) || (!gameType.isQualificationMode() && !playerData.isDead())){
                p.sendTitle("§a§lVous êtes qualifié !","§aBien joué !",0,20*3,20);
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
                qualified++;
            }else{
                if(!playerData.isDead()){
                    p.sendTitle("§c§lÉliminé !","§cVous avez perdu !",0,20*3,20);
                }
                playerData.setDead(true);
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
                Bukkit.broadcastMessage("§d§l"+Main.PREFIX+" §6"+Main.SEPARATOR+" "+ChatColor.of(new Color(0,255,0))+p.getName()+" vient de gagner la partie !");
            }else{
                Bukkit.broadcastMessage("§c§lFIN DE LA PARTIE");
            }

        }else{
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::rollGames,5*20);
        }
    }

    private void resetPlayerData() {
        for(PlayerData playerData : this.playerDataHashMap.values()){
            playerData.resetGameData();
        }
    }

    private void rollGames() {
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
        p.sendMessage("§aQualifié !");
        p.setGameMode(GameMode.SPECTATOR);

        stopGame();
    }

    public List<PlayerData> getAlivePlayerData(){
        ArrayList<PlayerData> alive = new ArrayList<>(this.playerDataHashMap.values());
        alive.removeIf(PlayerData::isDead);
        return alive;
    }

    public Plugin getMain() {
        return this.main;
    }
}
