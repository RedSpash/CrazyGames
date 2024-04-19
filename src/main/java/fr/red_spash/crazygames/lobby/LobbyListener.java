package fr.red_spash.crazygames.lobby;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.GameStatus;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class LobbyListener implements Listener {

    private final GameManager gameManager;

    public LobbyListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent e){
        if(e.getPlayer().isOp() && e.getPlayer().getGameMode() == GameMode.CREATIVE)return;
        if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY)return;
        e.setCancelled(true);
    }

    @EventHandler
    public void paintingBreakEvent(HangingBreakByEntityEvent e){
        if(!(e.getRemover() instanceof Player p))return;
        if(p.isOp() && p.getGameMode() == GameMode.CREATIVE)return;
        if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY)return;
        e.setCancelled(true);
    }


    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent e){
        if(e.getPlayer().isOp() && e.getPlayer().getGameMode() == GameMode.CREATIVE)return;
        if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY)return;
        e.setCancelled(true);
    }

    @EventHandler
    public void foodLevelChangeEvent(FoodLevelChangeEvent e){
        if(!(e.getEntity() instanceof Player p))return;
        if(p.isOp() && p.getGameMode() == GameMode.CREATIVE)return;
        if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY)return;
        e.setCancelled(true);
        e.setFoodLevel(20);
    }

    @EventHandler
    public void playerInteractAtEntityEvent(PlayerInteractAtEntityEvent e){
        Player p = e.getPlayer();
        if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY)return;
        if(!(e.getRightClicked() instanceof Player clicked))return;
        if(clicked.isOp() && clicked.getGameMode() == GameMode.CREATIVE)return;
        if(!clicked.getPassengers().isEmpty())return;

        clicked.addPassenger(p);
    }

    @EventHandler
    public void playerToggleSneakEvent(PlayerToggleSneakEvent e){
        Player p = e.getPlayer();
        if(p.isOp() && p.getGameMode() == GameMode.CREATIVE)return;
        if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY)return;
        if(!(e.isSneaking()))return;
        if(p.getPassengers().isEmpty())return;

        for(Entity passager : p.getPassengers()){
            p.removePassenger(passager);
        }
    }

    @EventHandler
    public void entityDamageByEntityEvent(EntityDamageByEntityEvent e){
        if(!(e.getEntity() instanceof Player p))return;
        if(p.isOp() && p.getGameMode() == GameMode.CREATIVE)return;
        if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY)return;
        e.setCancelled(true);
    }
}
