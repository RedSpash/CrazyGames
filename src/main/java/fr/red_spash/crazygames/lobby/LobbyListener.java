package fr.red_spash.crazygames.lobby;

import com.google.common.base.Predicates;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.GameStatus;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

import java.util.List;

public class LobbyListener implements Listener {

    private final GameManager gameManager;
    private final Lobby lobby;
    private final List<Material> lockedBlocks = List.of(
            Material.ENDER_CHEST,
            Material.HOPPER,
            Material.CHEST,
            Material.OAK_TRAPDOOR);

    public LobbyListener(GameManager gameManager) {
        this.gameManager = gameManager;
        this.lobby = this.gameManager.getLobby();
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
        if(e.getItemInHand().isSimilar(Lobby.PINK_WOOL_ITEM))return;
        e.setCancelled(true);
    }

    @EventHandler
    public void blockPlaceEvent(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player p))return;
        if(p.isOp() && p.getGameMode() == GameMode.CREATIVE)return;
        if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY)return;
        e.setCancelled(true);
    }

    @EventHandler
    public void explode(PlayerDropItemEvent e){
        if(e.getPlayer().isOp() && e.getPlayer().getGameMode() == GameMode.CREATIVE)return;
        if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY)return;
        e.setCancelled(true);
    }

    @EventHandler
    public void explode(EntityPickupItemEvent e){
        if(!(e.getEntity() instanceof Player p))return;
        if(p.isOp() && p.getGameMode() == GameMode.CREATIVE)return;
        if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY)return;
        e.setCancelled(true);
    }

    @EventHandler
    public void playerBucketFillEvent(PlayerBucketFillEvent e){
        if(e.getPlayer().isOp() && e.getPlayer().getGameMode() == GameMode.CREATIVE)return;
        if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY)return;
        e.setCancelled(true);
    }

    @EventHandler
    public void playerBucketEmptyEvent(PlayerBucketEmptyEvent e){
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
    public void playerMoveEvent(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(p.isOp() && p.getGameMode() == GameMode.CREATIVE)return;
        if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY)return;
        if(e.getTo().getBlock() == e.getFrom().getBlock())return;
        if(!p.getWorld().getName().equals(this.lobby.getSpawn().getWorld().getName())) return;
        if(p.getLocation().getY() > this.lobby.getTeleportHeight())return;

        p.teleport(this.lobby.getSpawn());
    }

    @EventHandler
    public void entityDamageEvent(EntityDamageEvent e){
        if(!(e.getEntity() instanceof Player p))return;
        if(p.isOp() && p.getGameMode() == GameMode.CREATIVE)return;
        if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY)return;
        e.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void playerInteractEvent(PlayerInteractEvent e){
        if(e.getPlayer().isOp() && e.getPlayer().getGameMode() == GameMode.CREATIVE)return;
        if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY)return;
        if(e.getItem() != null && e.getItem().isSimilar(Lobby.PINK_WOOL_ITEM) && (e.getClickedBlock() != null && !this.lockedBlocks.contains(e.getClickedBlock().getType())))return;
        e.setCancelled(true);
    }
}
