package fr.red_spash.crazygames.listener;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SystemListener implements Listener {

    private final GameManager gameManager;

    public SystemListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if(this.gameManager.getPlayerData(p.getUniqueId()) == null){
            this.gameManager.addPlayerData(p.getUniqueId(),new PlayerData(p.getUniqueId()));
        }
        p.setScoreboard(this.gameManager.getPlayerData(p.getUniqueId()).getScoreboard().getBoard());
    }

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent e){
        Player p = e.getPlayer();
        PlayerData playerData = this.gameManager.getPlayerData(p.getUniqueId());

        if(!playerData.isDead() && this.gameManager.getActualGame() != null){
            this.gameManager.killPlayer(p);
        }
    }

}
