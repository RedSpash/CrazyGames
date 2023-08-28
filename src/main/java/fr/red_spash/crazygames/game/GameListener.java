package fr.red_spash.crazygames.game;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.GameStatus;
import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.map.CheckPoint;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        if (this.gameManager.getActualGame().getGameStatus() != GameStatus.PLAYING
                && this.gameManager.getActualGame().getGameMap().getSpawnLocation() != null
                && p.getLocation().getY() <= this.gameManager.getActualGame().getGameMap().getSpawnLocation().getY() - 5) {
            p.teleport(this.gameManager.getActualGame().getGameMap().getSpawnLocation());
            return;
        }


        PlayerData playerData = this.gameManager.getPlayerData(p.getUniqueId());
        if(playerData.isDead())return;

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
}
