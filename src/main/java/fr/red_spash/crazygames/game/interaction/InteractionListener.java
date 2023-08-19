package fr.red_spash.crazygames.game.interaction;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public class InteractionListener implements Listener {

    private final GameInteraction gameInteraction;

    public InteractionListener(GameInteraction gameInteraction){
        this.gameInteraction = gameInteraction;
    }

    @EventHandler
    public void explode(EntityExplodeEvent e){
        if(!gameInteraction.isExplosion()){
            e.setCancelled(true);
        }
    }

    @EventHandler()
    public void blockBreakEvent(BlockBreakEvent e){
        if(!gameInteraction.getAllowedToBeBreak().contains(e.getBlock().getType())){
            e.setCancelled(true);
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


}
