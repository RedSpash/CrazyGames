package fr.red_spash.crazygames.game;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.GameStatus;
import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.game.models.GameType;
import fr.red_spash.crazygames.map.CheckPoint;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

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
                && p.getLocation().getY() <= this.gameManager.getSpawnLocation().getY() - 10
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
                    if(this.gameManager.getActualGame().getGameMap().getCheckPoints().indexOf(checkPoint) == this.gameManager.getActualGame().getGameMap().getCheckPoints().size()-1){
                        this.gameManager.getPlayerManager().qualifiedPlayer(p);
                    }else{
                        playerData.unlockCheckPoint(checkPoint);
                        p.playSound(p.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK,2,1);
                        p.sendMessage("§a§lCHECKPOINT!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void backToRed_Survie(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        PlayerData playerData = this.gameManager.getPlayerData(p.getUniqueId());
        if(playerData.isEliminated()){
            ItemStack itemStack = e.getCurrentItem();
            if(itemStack != null){
                if(p.getGameMode() == GameMode.SPECTATOR){
                    if(itemStack.getType() == Material.RED_DYE){
                        Utils.sendPlayerToSurvieServer(p);
                    }
                }
            }
        }
    }


    @EventHandler
    public void entityChangeBlockEvent(EntityChangeBlockEvent e){
        if(this.gameManager.getActualGame() == null)return;
        if(this.gameManager.getActualGame().getGameType() == GameType.ANVIL_FALL)return;
        e.setCancelled(true);

    }
}
