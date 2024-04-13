package fr.red_spash.crazygames.game.manager;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

    private final HashMap<UUID,PlayerData> playerDataHashMap = new HashMap<>();
    private final MessageManager messageManager;
    private final GameManager gameManager;
    private int amountQualifiedPlayer;
    private int amountEliminatedPlayer;

    public PlayerManager(MessageManager messageManager, GameManager gameManager) {
        this.messageManager = messageManager;
        this.gameManager = gameManager;
    }

    public HashMap<UUID, PlayerData> getPlayerDataHashMap() {
        return playerDataHashMap;
    }

    public void fillPlayerData() {
        this.playerDataHashMap.clear();
        for(Player p : Bukkit.getOnlinePlayers()){
            if(!this.playerDataHashMap.containsKey(p.getUniqueId())){
                this.playerDataHashMap.put(p.getUniqueId(),new PlayerData(p.getUniqueId()));
            }else{
                this.playerDataHashMap.get(p.getUniqueId()).reset();
            }
            p.setScoreboard(this.playerDataHashMap.get(p.getUniqueId()).getScoreboard().getBoard());

        }
    }

    public PlayerData getPlayerData(UUID uniqueId) {
        return this.playerDataHashMap.get(uniqueId);
    }

    public void resetPlayerData() {
        for(PlayerData playerData : this.playerDataHashMap.values()){
            playerData.resetGameData();
        }
    }

    public void qualifiedPlayer(Player p){
        this.qualifiedPlayer(p,null);
    }
    public void qualifiedPlayer(Player p, String comment) {
        PlayerData playerData = this.getPlayerData(p.getUniqueId());

        playerData.setQualified(true);
        p.getInventory().clear();
        p.setGameMode(GameMode.SPECTATOR);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);

        int top = 0;
        for(PlayerData data : this.playerDataHashMap.values()){
            if(data.isQualified()){
                top = top + 1;
            }
        }

        this.messageManager.sendQualificationTitle(p);
        this.messageManager.broadcastQualificationMessage(p.getName(),top,comment);

        if(this.getQualifiedPlayers().size() >= this.amountQualifiedPlayer){
            this.gameManager.stopGame();
        }
    }

    public void eliminatePlayer(Player p) {
        PlayerData playerData = this.getPlayerData(p.getUniqueId());
        if(playerData.isDead())return;
        if(playerData.isEliminated())return;

        Game actualGame = this.gameManager.getActualGame();

        if(actualGame != null){
            if(actualGame.getGameStatus() != GameStatus.PLAYING){
                p.teleport(actualGame.getGameMap().getSpawnLocation());
                return;
            }
            if(actualGame.getGameType().isQualificationMode()){
                if(actualGame.getGameMap().hasCheckpoint()
                        && playerData.getLastCheckPoint() != null) {
                    Location location = playerData.getLastCheckPoint().getMiddle();
                    location.setDirection(p.getLocation().getDirection());
                    p.teleport(location);
                }else {
                    p.teleport(actualGame.getGameMap().getSpawnLocation());
                }
            }else if(!playerData.isDead()){
                if(playerData.getLife() <= 0){
                    this.killPlayer(p);
                }else{
                    playerData.loseLife();
                    playerData.setEliminated(true);
                    this.messageManager.broadcastLifeLost(p.getName(),playerData.getVisualLife());
                    this.playEliminationAnimation(p);
                }
                if(this.amountEliminatedPlayer <= this.getEliminatedPlayer().size()){
                    this.gameManager.stopGame();
                }
                this.gameManager.updateHidedPlayer(p);
            }
        }
    }

    public void qualifiedWithLifeLost(Player p, boolean hasLostHisLife) {
        PlayerData playerData = this.getPlayerData(p.getUniqueId());

        if(!hasLostHisLife){
            playerData.loseLife();
            this.messageManager.broadcastLifeLost(p.getName(),playerData.getVisualLife());
        }
        this.messageManager.sendQualifiedWithLifeLost(p);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,0);
        this.playEliminationAnimation(p);
    }

    public void killPlayer(Player p){
        this.killPlayer(p,"");
    }
    public void killPlayer(Player p, String reason) {
        PlayerData playerData = this.getPlayerData(p.getUniqueId());

        playerData.setLife(0);
        this.messageManager.broadcastEliminateMessage(p.getName(),reason);
        playerData.setDead(true);
        playerData.setEliminated(true);
        this.playEliminationAnimation(p);
        p.sendTitle("§c§lÉliminé !","§cVous avez perdu !",0,20*3,20);

        if(this.gameManager.isAutoStart()){
            p.getInventory().clear();
            p.sendMessage("§aCliquez sur l'item dans votre inventaire pour retourner sur la Red_Survie 3 !");
            p.getInventory().setItem(4, Utils.createFastItemStack(Material.RED_DYE,"§a§lRetour à la survie","§fVous téléporte sur la Red_Survie 3"));
        }
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

    public List<PlayerData> getAlivePlayerData(){
        ArrayList<PlayerData> alive = new ArrayList<>(this.playerDataHashMap.values());
        alive.removeIf(PlayerData::isDead);
        return alive;
    }

    public void calculatePlayerQualifiedOrEliminated(GameType gameType) {
        if(gameType.isQualificationMode()){
            this.amountQualifiedPlayer = this.getAlivePlayerData().size()-2;
        }else{
            this.amountEliminatedPlayer = 3;
            while(this.getAlivePlayerData().size() <= this.amountEliminatedPlayer){
                this.amountEliminatedPlayer--;
            }
        }

        if(this.amountQualifiedPlayer <= 0 ){
            this.amountQualifiedPlayer = 1;
        }
        if(this.amountEliminatedPlayer <= 0 ){
            this.amountEliminatedPlayer = 1;
        }
    }

    public void addPlayerData(UUID uniqueId, PlayerData playerData) {
        this.playerDataHashMap.put(uniqueId,playerData);
    }

    private void playEliminationAnimation(Player p) {
        p.playSound(p.getLocation(), Sound.ENTITY_WITHER_AMBIENT,1,1);
        p.getInventory().clear();
        p.setVelocity((p.getVelocity().multiply(-5)));
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,10,1,false,false,false));
        p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN,4,1);
        p.setGameMode(GameMode.SPECTATOR);
        p.sendHurtAnimation(10F);
    }

    public int removeLife(Player p) {
        PlayerData playerData = this.getPlayerData(p.getUniqueId());
        if(playerData.getLife() > 0){
            this.qualifiedWithLifeLost(p,false);
            return 1;
        }else{
            this.messageManager.sendEliminationTitle(p);
            this.messageManager.broadcastEliminateMessage(p.getName());
            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH,1,1);
            p.setGameMode(GameMode.SPECTATOR);
            playerData.setDead(true);
        }
        return 0;
    }
}
