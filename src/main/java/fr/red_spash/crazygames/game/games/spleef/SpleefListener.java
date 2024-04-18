package fr.red_spash.crazygames.game.games.spleef;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class SpleefListener implements Listener {
    private final GameManager gameManager;
    private final Spleef spleef;

    public SpleefListener(Spleef spleef) {
        this.gameManager = spleef.getGameManager();
        this.spleef = spleef;
    }

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent e){
        if(!this.gameManager.isInWorld(e.getPlayer().getWorld()))return;
        if(e.isCancelled())return;
        if(e.getBlock().getType() == Material.SNOW_BLOCK || e.getBlock().getType() == Material.CLAY){
            this.spleef.getAvailableBlocks().remove(e.getBlock());
            if(Utils.randomNumber(0,1) == 0){
                Player p = e.getPlayer();
                if(!p.getInventory().containsAtLeast(new ItemStack(Material.SNOWBALL),16)){
                    ItemStack itemStack = new ItemStack(Material.SNOWBALL);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName("§c§lSnowBreaker");
                    itemMeta.setLore(Arrays.asList("§7Permet de casser le","§7block visé par la boule de neige."));
                    itemStack.setItemMeta(itemMeta);
                    e.getPlayer().getInventory().addItem(itemStack);
                }
            }
        }
    }

    @EventHandler
    public void projectilHitEvent(ProjectileHitEvent e){
        if(!this.gameManager.isInWorld(e.getEntity().getWorld()))return;
        if(e.getEntity() instanceof Snowball &&
                (e.getHitBlock() != null &&
                        (e.getHitBlock().getType() == Material.CLAY || e.getHitBlock().getType() == Material.SNOW_BLOCK ))){
            e.getHitBlock().setType(Material.AIR);
        } else if (e.getHitEntity() instanceof Player p) {
            p.sendHurtAnimation(1);
            p.setVelocity(e.getEntity().getLocation().subtract(p.getLocation()).toVector().normalize().add(new Vector(0,0.25,0)).multiply(-1));
        }
    }

}
