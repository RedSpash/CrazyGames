package fr.red_spash.crazygames.game.models;

import fr.red_spash.crazygames.Main;
import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.error.IncompatibleGameType;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.GameStatus;
import fr.red_spash.crazygames.game.manager.MessageManager;
import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.map.GameMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class Game implements Cloneable{

    protected final GameManager gameManager;
    private final GameType gameType;
    private GameMap gameMap;
    private GameStatus gameStatus;
    private final ArrayList<Listener> activeListeners;
    private ArrayList<Integer> activeTasks;

    protected Game(GameType gameType) {
        this.activeListeners = new ArrayList<>();
        this.activeTasks = new ArrayList<>();
        this.gameManager = Main.getInstance().getGameManager();
        this.gameType = gameType;
        this.gameStatus = GameStatus.WAITING;
    }

    public void loadMap(){
        this.loadMap(null);
    }

    public void loadMap(@Nullable GameMap forceGameMap){
        if(forceGameMap == null){
            List<GameMap> gameMaps = new ArrayList<>(this.getGameManager().getMapManager().getMaps());
            gameMaps.removeIf(map -> map.getGameType() != this.gameType);

            this.gameMap = gameMaps.get(Utils.randomNumber(0,gameMaps.size()-1));
        }else{
            if(forceGameMap.getGameType() == this.gameType){
                this.gameMap = forceGameMap;
            }else{
                new IncompatibleGameType("La carte '"+forceGameMap.getName()+"' n'est pas compatible avec le mode "+this.gameType+" !").printStackTrace();
                return;
            }

        }

        boolean isSpawned = this.gameMap.loadWorld();
        if(!isSpawned){
            Bukkit.broadcastMessage("§d§l"+ MessageManager.PREFIX+" "+ MessageManager.SEPARATOR+" §cChangement de programme !");
            this.gameManager.startRandomGame();
        }
    }
    public void initializePlayers(){
        for(Player p : Bukkit.getOnlinePlayers()){
            PlayerData playerData = this.gameManager.getPlayerData(p.getUniqueId());
            playerData.resetGameData();
            for(PotionEffect potionEffect : p.getActivePotionEffects()){
                p.removePotionEffect(potionEffect.getType());
            }
            p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            p.setFoodLevel(20);
            p.setLevel(0);
            p.setExp(0);
            p.teleport(this.gameMap.getSpawnLocation());
            p.sendTitle(ChatColor.of(gameType.getColor()) +"§l"+this.gameType.getName(),"§9"+this.gameType.getShortDescription(),20,20*3,20);
            p.sendMessage(ChatColor.of(gameType.getColor())+"§l"+this.gameType.getName()+"\n§f "+this.gameType.getLongDescription());
            p.getInventory().clear();
            if(playerData.isDead()){
                p.setGameMode(GameMode.SPECTATOR);
            }else{
                p.setGameMode(GameMode.SURVIVAL);
            }
        }
    }
    public abstract void startGame();

    public GameManager getGameManager() {
        return gameManager;
    }

    public GameType getGameType() {
        return gameType;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void unRegisterListeners(){
        for(Listener listener : this.activeListeners){
            HandlerList.unregisterAll(listener);
        }

        for(int runnable : this.activeTasks){
            Bukkit.getScheduler().cancelTask(runnable);
        }
        this.activeTasks.clear();
        this.activeListeners.clear();
    }

    protected void initializeListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, Main.getInstance());
        this.activeListeners.add(listener);
    }

    protected void initializeTask(Runnable runnable, int i, int i1) {
        int id = Bukkit.getServer().getScheduler().runTaskTimer(this.gameManager.getMain(),runnable, i, i1).getTaskId();
        this.activeTasks.add(id);
    }

    protected List<Block> getBlockPlatform(Material material, Material toReplace) {
        return this.getBlockPlatform(List.of(material),toReplace);
    }


    protected List<Block> getBlockPlatform(List<Material> materials, Material toReplace) {
        ArrayList<Block> blocks = new ArrayList<>();
        Location location = this.gameManager.getSpawnLocation();
        int count = 0;
        while(!materials.contains(location.getBlock().getType()) && count<=50){
            location.add(0,-1,0);
            count++;
        }

        for(int x=-75; x<=75; x++){
            for(int z=-75; z<=75; z++){
                Location blockLocation = location.clone().add(x,0,z);
                if(materials.contains(blockLocation.getBlock().getType())){
                    blocks.add(blockLocation.getBlock());
                    if(toReplace != null){
                        blockLocation.getBlock().setType(toReplace);
                    }
                }
            }
        }
        return blocks;
    }

    public List<Block> getBlockPlatform(Material material) {
        return this.getBlockPlatform(material,null);
    }

    public List<String> updateScoreboard() {
        return new ArrayList<>();
    }
}
