package fr.red_spash.crazygames.game.manager;

import fr.red_spash.crazygames.Main;
import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.interaction.GameInteraction;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import fr.red_spash.crazygames.game.tasks.GameTimer;
import fr.red_spash.crazygames.map.GameMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.awt.Color;
import java.util.*;
import java.util.List;

public class GameManager {

    private final int maxTime = 60*4;
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
    private GameTimer gameTimer;
    private BukkitTask gameTimerTask;
    private int amountQualifiedPlayer;
    private int amountEliminatedPlayer;


    public GameManager(Main main){
        this.main = main;

        this.fillPlayerData();

        this.messageManager = new MessageManager(this);
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
            this.updateHidedPlayer(p);
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
        World oldWorld = null;
        if(this.actualGame != null){
            oldWorld = this.actualGame.getGameMap().getWorld();
        }
        if(oldWorld != null){
            World finalOldWorld = oldWorld;
            Bukkit.getScheduler().runTaskLater(this.main, ()->{
                Utils.teleportPlayersAndRemoveWorld(finalOldWorld,false);
                this.actualGame.getGameMap().deleteWorld(finalOldWorld);
            } ,10);
        }

        this.resetPlayerData();

        for(Player p : Bukkit.getOnlinePlayers()){
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
            p.setHealth(20);
            this.updateHidedPlayer(p);
            p.setFlying(false);
            p.setAllowFlight(false);
        }

        this.actualGame = gameMap.getGameType().createInstance();
        this.actualGame.loadMap();
        this.actualGame.initializePlayers();
        this.actualGame.setGameStatus(GameStatus.STARTING);
        this.playedGameType.add(gameMap.getGameType());

        GameType gameType = gameMap.getGameType();
        if(gameType.isQualificationMode()){
            amountQualifiedPlayer = this.getAlivePlayerData().size()-1;
        }else{
            amountEliminatedPlayer = this.getAlivePlayerData().size()-1;
        }

        this.startCountdown();

    }

    private void startCountdown() {
        gameTimer = new GameTimer(this, maxTime);
        if(this.taskCountDown != null){
            this.taskCountDown.cancel();
        }
        GameManager gameManager = this;
        this.taskCountDown = Bukkit.getScheduler().runTaskTimer(this.main, new Runnable() {
            int seconds = 8;
            @Override
            public void run() {

                if(seconds == 0){
                    for(Player p : Bukkit.getOnlinePlayers()){
                        p.sendTitle("§a§lC'est parti !","§a§lBonne chance !",0,20*3,10);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,2,2);
                    }
                    actualGame.setGameStatus(GameStatus.PLAYING);
                    actualGame.startGame();
                    gameTimerTask = Bukkit.getScheduler().runTaskTimer(gameManager.getMain(), gameTimer,0,20);
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
        if(playerData.isDead())return;
        if(playerData.isEliminated())return;

        if(this.actualGame != null){
            if(this.actualGame.getGameMap().hasCheckpoint() && playerData.getLastCheckPoint() != null) {
                p.teleport(playerData.getLastCheckPoint().getCheckPointLocation());
            }else if(this.actualGame.getGameStatus() == GameStatus.PLAYING){
                if(this.actualGame.getGameType().isQualificationMode()){
                    p.teleport(this.actualGame.getGameMap().getSpawnLocation());
                }else if(!playerData.isDead()){

                    if(playerData.getLife() <= 0){
                        this.killPlayer(p);
                    }else{
                        playerData.loseLife();
                        playerData.setEliminated(true);
                        this.messageManager.broadcastLifeLost(p.getName(),playerData.getVisualLife());
                        this.setEliminationAnimation(p);
                    }

                    if(this.amountEliminatedPlayer <= this.getEliminatedPlayer().size()){
                        this.stopGame();
                    }
                    this.updateHidedPlayer(p);
                }
            }
        }
    }

    public void stopGame() {
        int qualified = 0;

        if(gameTimerTask != null){
            gameTimerTask.cancel();
        }

        GameType gameType = this.actualGame.getGameType();
        for(Player p : Bukkit.getOnlinePlayers()){
            PlayerData playerData = this.getPlayerData(p.getUniqueId());
            if(!gameType.isQualificationMode() && !playerData.isDead()){
                if(playerData.isEliminated()){
                    this.qualifiedWithLifeLost(p,true);
                }else{
                    this.messageManager.sendQualificationTitle(p);
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
                }
                qualified++;
            }else if(gameType.isQualificationMode() && !playerData.isDead() && !playerData.isQualified()){
                if(playerData.getLife() > 0){
                    this.qualifiedWithLifeLost(p,false);
                    qualified++;
                }else{
                    this.messageManager.sendEliminationTitle(p);
                    this.messageManager.broadcastEliminateMessage(p.getName());
                    p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH,1,1);
                    p.setGameMode(GameMode.SPECTATOR);
                    playerData.setDead(true);
                }
            }
            if(gameType.isQualificationMode() && playerData.isQualified()){
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                qualified++;
            }
        }

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
                this.messageManager.broadcastVictoryMessage(p.getName());
            }else{
                Bukkit.broadcastMessage("§c§lFIN DE LA PARTIE");
            }

        }else{
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::rollGames,4*20L);
        }
    }

    private void qualifiedWithLifeLost(Player p, boolean hasLostHisLife) {
        PlayerData playerData = this.getPlayerData(p.getUniqueId());

        if(!hasLostHisLife){
            playerData.loseLife();
            this.messageManager.broadcastLifeLost(p.getName(),playerData.getVisualLife());
        }
        this.messageManager.sendQualifiedWithLifeLost(p);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,0);
        this.setEliminationAnimation(p);
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
                    p.playSound(p.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK,1,2);
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

        this.messageManager.sendQualificationTitle(p);
        this.messageManager.broadcastQualificationMessage(p.getName(),top);

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

        playerData.setLife(0);
        this.messageManager.broadcastEliminateMessage(p.getName());
        playerData.setDead(true);
        playerData.setEliminated(true);
        this.setEliminationAnimation(p);
        p.sendTitle("§c§lÉliminé !","§cVous avez perdu !",0,20*3,20);
    }

    private void updateHidedPlayer(Player p) {
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

    private void setEliminationAnimation(Player p) {
        p.playSound(p.getLocation(), Sound.ENTITY_WITHER_AMBIENT,1,1);
        p.getInventory().clear();
        p.setVelocity((p.getVelocity().multiply(-5)));
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,10,1,false,false,false));
        p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN,4,1);
        p.setGameMode(GameMode.SPECTATOR);
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

    public int getAmountEliminatedPlayer() {
        return amountEliminatedPlayer;
    }

    public int getAmountQualifiedPlayer() {
        return amountQualifiedPlayer;
    }

    public GameTimer getGameTimer() {
        return gameTimer;
    }

    public int getMaxTime() {
        return this.maxTime;
    }

    public GameType getActualGameType() {
        if(this.actualGame == null)return null;
        return this.actualGame.getGameType();
    }

    public GameStatus getActualGameStatus() {
        if(this.actualGame == null)return null;
        return this.actualGame.getGameStatus();
    }

    public Location getSpawnLocation() {
        if(this.actualGame == null)return null;
        return this.actualGame.getGameMap().getSpawnLocation();
    }
}
