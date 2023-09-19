package fr.red_spash.crazygames.game.interaction;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
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

import java.util.HashMap;
import java.util.UUID;

public class InteractionListener implements Listener {

    private final GameInteraction gameInteraction;
    private final GameManager gameManager;
    private final HashMap<UUID,Long> hitCooldown;

    public InteractionListener(GameInteraction gameInteraction, GameManager gameManager){
        this.gameInteraction = gameInteraction;
        this.gameManager = gameManager;
        this.hitCooldown = new HashMap<>();
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

        if(!this.gameInteraction.getMaterialPotionEffectHashMap().isEmpty()){
            for(Material material : this.gameInteraction.getMaterialPotionEffectHashMap().keySet()){
                for(int i =0; i>=-2; i--){
                    if(p.getLocation().add(0,i,0).getBlock().getType() == material){
                        p.addPotionEffect(this.gameInteraction.getMaterialPotionEffectHashMap().get(material));
                    }
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
        if(this.gameManager.getActualGame() != null){
            if(this.gameManager.getActualGame().getGameType() == GameType.RACE){
                if(p.getLocation().getY() <= 50){
                    this.gameManager.getPlayerManager().eliminatePlayer(p);
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

        for(Material material : this.gameInteraction.getKillPlayerMaterials()){
            if(material.isSolid()){
                if(p.getLocation().add(0,-1,0).getBlock().getType().equals(material)){
                    this.gameManager.getPlayerManager().eliminatePlayer(p);
                }
            }else{
                if(p.getLocation().getBlock().getType().equals(material)){
                    this.gameManager.getPlayerManager().eliminatePlayer(p);
                }
            }
        }
    }

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent e){
        if(!this.gameManager.isInWorld(e.getPlayer().getWorld()))return;

        if(!this.gameInteraction.getAllowedToBeBreak().contains(e.getBlock().getType())){
            e.setCancelled(true);
        }
        if(!this.gameInteraction.isBlockLoot()){
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
        if(!this.gameInteraction.getAllowedToBePlaced().contains(e.getBlock().getType())){
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

        if(!this.gameInteraction.isVehicleBreak()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void mount(EntityMountEvent e){
        if(!this.gameManager.isInWorld(e.getEntity().getWorld()))return;

        if(!this.gameInteraction.isEntityMount()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerBucketFillEvent(PlayerBucketFillEvent e){
        if(!this.gameManager.isInWorld(e.getPlayer().getWorld()))return;

        if(!this.gameInteraction.isBucketInteract()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerBucketEmptyEvent(PlayerBucketEmptyEvent e){
        if(!this.gameManager.isInWorld(e.getPlayer().getWorld()))return;

        if(!this.gameInteraction.isBucketInteract()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerBucketEntityEvent(PlayerBucketEntityEvent e){
        if(!this.gameManager.isInWorld(e.getPlayer().getWorld()))return;

        if(!this.gameInteraction.isBucketInteract()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void projectileHit(ProjectileLaunchEvent e){
        if(!this.gameManager.isInWorld(e.getEntity().getWorld()))return;

        if(!this.gameInteraction.isShootProjectile()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void foodLevelChangeEvent(FoodLevelChangeEvent e){
        if(!this.gameManager.isInWorld(e.getEntity().getWorld()))return;

        if(!this.gameInteraction.isFoodLevel()){
            e.getEntity().setFoodLevel(20);
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void playerRegenEvent(EntityRegainHealthEvent e){
        if(!this.gameManager.isInWorld(e.getEntity().getWorld()))return;

        if(e.getEntity() instanceof Player){
            if(!this.gameInteraction.isPlayerRegen()){
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void entityHit(EntityDamageByEntityEvent e){
        if(!this.gameManager.isInWorld(e.getEntity().getWorld()))return;
        if(e.getDamager() instanceof Player p && e.getEntity() instanceof Player){
            if(this.gameInteraction.getHitCooldown() > 0){
                if(this.hitCooldown.containsKey(p.getUniqueId())){
                    if(this.hitCooldown.get(p.getUniqueId()) > System.currentTimeMillis()){
                        e.setCancelled(true);
                        return;
                    }
                }
                this.hitCooldown.put(p.getUniqueId(), (long) (System.currentTimeMillis()+(1000*this.gameInteraction.getHitCooldown())));
            }else if(!this.gameInteraction.isPvp()){
                e.setCancelled(true);
            }
            return;
        }


        if(this.gameInteraction.isPve()){
            if (e.getEntity() instanceof Player pl && !(e.getDamager() instanceof Player)) {
                if(pl.getHealth() <= e.getFinalDamage()){
                    e.setCancelled(true);
                    this.gameManager.getPlayerManager().eliminatePlayer(pl);
                }
            }
        }else{
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void playerDeath(EntityDamageEvent e){
        if(!this.gameManager.isInWorld(e.getEntity().getWorld()))return;
        if(gameInteraction.isPve()){
            if(e.getEntity() instanceof Player p){
                if(p.getHealth() <= e.getFinalDamage()){
                    e.setCancelled(true);
                    this.gameManager.getPlayerManager().eliminatePlayer(p);
                }
            }
        }else{
            e.setCancelled(true);
        }
    }


}
