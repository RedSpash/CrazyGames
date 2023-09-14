package fr.red_spash.crazygames.game.manager;

import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
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
            this.gameManager.stopGame();
        }
    }

    public void eliminatePlayer(Player p) {
        PlayerData playerData = this.getPlayerData(p.getUniqueId());
        if(playerData.isDead())return;
        if(playerData.isEliminated())return;

        Game actualGame = this.gameManager.getActualGame();

        if(actualGame != null){
            if(actualGame.getGameMap().hasCheckpoint()) {
                if( playerData.getLastCheckPoint() != null){
                    p.teleport(playerData.getLastCheckPoint().getMiddle());
                }else{
                    p.teleport(actualGame.getGameMap().getSpawnLocation());
                }

            }else if(actualGame.getGameStatus() == GameStatus.PLAYING){
                if(actualGame.getGameType().isQualificationMode()){
                    p.teleport(actualGame.getGameMap().getSpawnLocation());
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
            this.amountQualifiedPlayer = this.getAlivePlayerData().size()-1;
        }else{
            this.amountEliminatedPlayer = this.getAlivePlayerData().size()-1;
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