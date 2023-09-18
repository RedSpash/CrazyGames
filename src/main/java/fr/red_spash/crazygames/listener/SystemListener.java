package fr.red_spash.crazygames.listener;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.PlayerData;
import org.bukkit.GameMode;
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
        p.setCustomName("§d"+p.getName());
        PlayerData playerData = this.gameManager.getPlayerData(p.getUniqueId());
        if(playerData == null){
            playerData = new PlayerData(p.getUniqueId());
            this.gameManager.getPlayerManager().addPlayerData(p.getUniqueId(),playerData);
        }
        p.setScoreboard(playerData.getScoreboard().getBoard());

        if(playerData.isDead()){
            e.setJoinMessage("");
            p.setGameMode(GameMode.SPECTATOR);
        }else{
            if(this.gameManager.getActualGameStatus() != null){
                playerData.setDead(true);
                p.setGameMode(GameMode.SPECTATOR);
            }else{
                p.setGameMode(GameMode.SURVIVAL);
                e.setJoinMessage("§a§l"+p.getName()+"§a vient de rejoindre la partie !");
            }

        }
        this.gameManager.updateHidedPlayer(p);
    }

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent e){
        Player p = e.getPlayer();
        PlayerData playerData = this.gameManager.getPlayerData(p.getUniqueId());

        if(!playerData.isDead()){
            if(this.gameManager.getActualGame() != null){
                this.gameManager.getPlayerManager().killPlayer(p,"déconnexion");
            }
            e.setQuitMessage("§c§l"+e.getPlayer().getName()+" §cvient de quitter la partie !");
        }else{
            e.setQuitMessage("");
        }
    }

}
