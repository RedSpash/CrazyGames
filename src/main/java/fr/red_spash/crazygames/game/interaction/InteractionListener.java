package fr.red_spash.crazygames.game.interaction;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
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
    public void item(ItemSpawnEvent e){
        if(!this.gameManager.isInWorld(e.getEntity().getWorld()))return;

        if(!gameInteraction.isBlockLootItem()){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void explode(EntityExplodeEvent e){
        if(!this.gameManager.isInWorld(e.getEntity().getWorld()))return;

        if(!gameInteraction.isExplosion()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void explode(InventoryClickEvent e){
        if(!this.gameManager.isInWorld(e.getWhoClicked().getWorld()))return;

        if(!gameInteraction.isMoveItemInventory()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void explode(PlayerSwapHandItemsEvent e){
        if(!this.gameManager.isInWorld(e.getPlayer().getWorld()))return;

        if(!gameInteraction.isMoveItemInventory()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void explode(PlayerDropItemEvent e){
        if(!this.gameManager.isInWorld(e.getPlayer().getWorld()))return;

        if(!gameInteraction.isMoveItemInventory()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e){
        if(e.getTo().getBlock() == e.getFrom().getBlock())return;
        if(!this.gameManager.isInWorld(e.getPlayer().getWorld()))return;

        Player p = e.getPlayer();
        PlayerData playerData = this.gameManager.getPlayerData(p.getUniqueId());

        if(this.gameInteraction.getBlockWin() != null && !playerData.isDead() && !playerData.isQualified()){
            for(int i =0; i>=-5; i--){
                if(p.getLocation().add(0,i,0).getBlock().getType() == this.gameInteraction.getBlockWin()){
                    this.gameManager.getPlayerManager().qualifiedPlayer(p);
                }
            }
        }

        if(this.gameInteraction.getDeathUnderSpawn() > 0){
            if(this.gameManager.getActualGame() != null && !playerData.isDead()){
                if(this.gameManager.getActualGame().getGameMap() != null){
                    if(this.gameManager.getActualGame().getGameMap().getSpawnLocation().getY() - this.gameInteraction.getDeathUnderSpawn() >= p.getLocation().getY()){
                        this.gameManager.getPlayerManager().eliminatePlayer(p);
                    }
                }
            }
        }

        if(this.gameInteraction.getTeleportUnderBlock() != -1){
            if(this.gameManager.getSpawnLocation() != null && !playerData.isDead()){
                if(this.gameManager.getSpawnLocation().getY()-this.gameInteraction.getTeleportUnderBlock() >= p.getLocation().getY()){
                    p.teleport(this.gameManager.getSpawnLocation());
                }
            }
        }
    }

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent e){
        if(!this.gameManager.isInWorld(e.getPlayer().getWorld()))return;

        if(!gameInteraction.getAllowedToBeBreak().contains(e.getBlock().getType())){
            e.setCancelled(true);
        }
        if(!gameInteraction.isBlockLoot()){
            e.setDropItems(false);
        }
    }

    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent e){
        if(!this.gameManager.isInWorld(e.getPlayer().getWorld()))return;
        if(this.gameInteraction.getMaxBuildHeight() != -1){
            if(this.gameManager.getActualGame() != null ){
                if(this.gameManager.getActualGame().getGameMap() != null){
                    if(this.gameManager.getActualGame().getGameMap().getSpawnLocation().getY()+this.gameInteraction.getMaxBuildHeight() < e.getBlock().getLocation().getY()){
                        e.setCancelled(true);
                        e.getPlayer().sendMessage("§cVous êtes à la limite de hauteur !");
                        return;
                    }
                }
            }
        }
        if(!gameInteraction.getAllowedToBePlaced().contains(e.getBlock().getType())){
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerInteractEvent(PlayerInteractEvent e){
        if(!this.gameManager.isInWorld(e.getPlayer().getWorld()))return;


        if(e.getClickedBlock() != null){
            if(!this.gameInteraction.isAllowInteraction()){
                if(e.getClickedBlock().getType().isInteractable() || e.getClickedBlock().getType() == Material.FARMLAND){
                    e.setCancelled(true);
                }
            }

        }
    }

    @EventHandler
    public void vehicleBreak(VehicleDestroyEvent e){
        if(!this.gameManager.isInWorld(e.getVehicle().getWorld()))return;

        if(!gameInteraction.isVehicleBreak()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void mount(EntityMountEvent e){
        if(!this.gameManager.isInWorld(e.getEntity().getWorld()))return;

        if(!gameInteraction.isEntityMount()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerBucketFillEvent(PlayerBucketFillEvent e){
        if(!this.gameManager.isInWorld(e.getPlayer().getWorld()))return;

        if(!gameInteraction.isBucketInteract()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerBucketEmptyEvent(PlayerBucketEmptyEvent e){
        if(!this.gameManager.isInWorld(e.getPlayer().getWorld()))return;

        if(!gameInteraction.isBucketInteract()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerBucketEntityEvent(PlayerBucketEntityEvent e){
        if(!this.gameManager.isInWorld(e.getPlayer().getWorld()))return;

        if(!gameInteraction.isBucketInteract()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void projectileHit(ProjectileLaunchEvent e){
        if(!this.gameManager.isInWorld(e.getEntity().getWorld()))return;

        if(!gameInteraction.isShootProjectile()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void foodLevelChangeEvent(FoodLevelChangeEvent e){
        if(!this.gameManager.isInWorld(e.getEntity().getWorld()))return;

        if(!gameInteraction.isFoodLevel()){
            e.getEntity().setFoodLevel(20);
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void playerRegenEvent(EntityRegainHealthEvent e){
        if(!this.gameManager.isInWorld(e.getEntity().getWorld()))return;

        if(e.getEntity() instanceof Player){
            if(!gameInteraction.isPlayerRegen()){
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void entityHit(EntityDamageByEntityEvent e){
        if(!this.gameManager.isInWorld(e.getEntity().getWorld()))return;

        if(!gameInteraction.isPvp()){
            if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
                e.setCancelled(true);
                return;
            }
        }

        if(!gameInteraction.isPve()){
            if(!(e.getDamager() instanceof Player && e.getEntity() instanceof Player)){
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void entityDamage(EntityDamageEvent e){
        if(!this.gameManager.isInWorld(e.getEntity().getWorld()))return;

        if(e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK){
            if(!gameInteraction.isPve()){
                e.setCancelled(true);
            }
        }
    }


}
