package fr.red_spash.crazygames.listener;

import fr.red_spash.crazygames.Main;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.GameStatus;
import fr.red_spash.crazygames.game.manager.PlayerData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;

public class SystemListener implements Listener {

    private final GameManager gameManager;

    private boolean requested = false;

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
            if(this.gameManager.getActualGame() != null && this.gameManager.getActualGame().getGameMap() != null){
                p.teleport(this.gameManager.getActualGame().getGameMap().getSpawnLocation());
            }
        }else{
            if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY){
                playerData.setDead(true);
                p.setGameMode(GameMode.SPECTATOR);
                e.setJoinMessage("");
            }else{
                this.gameManager.getLobby().giveItems(p);
                p.setGameMode(GameMode.SURVIVAL);
                e.setJoinMessage("§a§l"+p.getName()+"§a vient de rejoindre la partie !");
                this.gameManager.getLobby().teleportToSpawn(p);
            }

        }
        if(this.gameManager.isAutoStart() ){
            p.sendMessage(ChatColor.of(Color.GREEN)+"/leave pour retourner sur la Red_Survie 3 !");
            if(!requested){
                Bukkit.broadcastMessage("§aDémarrage dans 5 secondes !");
                requested = true;
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        if(Bukkit.getOnlinePlayers().size() > 1){
                            gameManager.startRandomGame();
                            requested = false;
                        }
                    }
                },20*5);
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
