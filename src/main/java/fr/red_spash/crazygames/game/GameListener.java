package fr.red_spash.crazygames.game;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.GameStatus;
import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.map.CheckPoint;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class GameListener implements Listener {
    private final GameManager gameManager;

    public GameListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e) {
        if (e.getTo().getBlock() == e.getFrom().getBlock()) return;
        if(!this.gameManager.isInWorld(e.getPlayer().getWorld()))return;

        Player p = e.getPlayer();
        PlayerData playerData = this.gameManager.getPlayerData(p.getUniqueId());

        if (this.gameManager.getActualGameStatus() != GameStatus.PLAYING
                && this.gameManager.getSpawnLocation() != null
                && p.getLocation().getY() <= this.gameManager.getSpawnLocation().getY() - 20
                && !playerData.isDead()
                && !playerData.isEliminated()) {
            p.teleport(this.gameManager.getSpawnLocation());
            if(this.gameManager.getActualGameStatus() == GameStatus.ENDING){
                boolean hasBlock = false;
                for(int i =0; i<= 50; i++){
                    if(p.getLocation().add(0,-i,0).getBlock().getType().isBlock()){
                       hasBlock = true;
                    }
                }
                if(!hasBlock){
                    p.setAllowFlight(true);
                    p.setFlying(true);
                }
            }
            return;
        }



        if(playerData.isDead() || p.getGameMode() == GameMode.SPECTATOR){
            if(this.gameManager.getSpawnLocation() != null){
                if(this.gameManager.getSpawnLocation().distance(p.getLocation()) >= 500){
                    p.teleport(this.gameManager.getSpawnLocation());
                }
            }
            return;
        }

        for(CheckPoint checkPoint : this.gameManager.getActualGame().getGameMap().getCheckPoints()){
            if(checkPoint.isInside(p.getLocation())){
                if(playerData.canUnlockCheckPoint(checkPoint)){
                    playerData.unlockCheckPoint(checkPoint);
                    p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE,1,2);
                    p.sendMessage("§a§lCHECKPOINT débloqué !");
                }
            }
        }
    }

    @EventHandler
    public void playerDeath(EntityDamageEvent e){
        if(e.getEntity() instanceof Player p){
            if(p.getHealth()-e.getFinalDamage() <= 0.0){
                e.setCancelled(true);
                this.gameManager.getPlayerManager().eliminatePlayer(p);
            }
        }
    }
}
