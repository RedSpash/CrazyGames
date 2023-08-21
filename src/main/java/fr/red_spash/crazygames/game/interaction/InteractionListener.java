package fr.red_spash.crazygames.game.interaction;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public class InteractionListener implements Listener {

    private final GameInteraction gameInteraction;
    private final GameManager gameManager;

    public InteractionListener(GameInteraction gameInteraction, GameManager gameManager){
        this.gameInteraction = gameInteraction;
        this.gameManager = gameManager;
    }

    @EventHandler
    public void explode(EntityExplodeEvent e){
        if(!gameInteraction.isExplosion()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e){
        if(e.getTo().getBlock() != e.getFrom().getBlock()){
            Player p = e.getPlayer();
            if(this.gameInteraction.getDeathY() == -1){
                return;
            }
            PlayerData playerData = this.gameManager.getPlayerData(p.getUniqueId());
            if(this.gameManager.getActualGame() != null && !playerData.isDead()){
                if(this.gameManager.getActualGame().getGameMap() != null){
                    if(this.gameInteraction.getDeathY() >= p.getLocation().getY()){
                        this.gameManager.eliminatePlayer(p);
                    }
                }
            }
        }
    }

    @EventHandler()
    public void blockBreakEvent(BlockBreakEvent e){
        if(!gameInteraction.getAllowedToBeBreak().contains(e.getBlock().getType())){
            e.setCancelled(true);
        }
        if(!gameInteraction.isBlockLoot()){
            e.setDropItems(false);
        }
    }

    @EventHandler()
    public void blockPlaceEvent(BlockPlaceEvent e){
        if(!gameInteraction.getAllowedToBePlaced().contains(e.getBlock().getType())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void vehicleBreak(VehicleDestroyEvent e){
        if(!gameInteraction.isVehicleBreak()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void mount(EntityMountEvent e){
        if(!gameInteraction.isEntityMount()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerBucketFillEvent(PlayerBucketFillEvent e){
        if(!gameInteraction.isBucketInteract()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerBucketEmptyEvent(PlayerBucketEmptyEvent e){
        if(!gameInteraction.isBucketInteract()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerBucketEntityEvent(PlayerBucketEntityEvent e){
        if(!gameInteraction.isBucketInteract()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void projectileHit(ProjectileLaunchEvent e){
        if(!gameInteraction.isShootProjectile()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void foodLevelChangeEvent(FoodLevelChangeEvent e){
        if(!gameInteraction.isFoodLevel()){
            e.getEntity().setFoodLevel(20);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void entityHit(EntityDamageByEntityEvent e){
        if(!gameInteraction.isPvp()){
            if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
                e.setCancelled(true);
                return;
            }
        }

        if(!gameInteraction.isPve()){
            if(!(e.getDamager() instanceof Player && e.getEntity() instanceof Player)){
                e.setCancelled(true);
                return;
            }
        }

    }

    @EventHandler
    public void entityDamage(EntityDamageEvent e){
        if(e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK){
            if(!gameInteraction.isPve()){
                e.setCancelled(true);
            }
        }
    }


}
