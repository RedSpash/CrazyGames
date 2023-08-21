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

import java.util.Arrays;

public class SpleefListener implements Listener {
    private final Spleef spleef;
    private final GameManager gameManager;

    public SpleefListener(Spleef spleef) {
        this.spleef = spleef;
        this.gameManager = spleef.getGameManager();
    }

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent e){
        if(e.getBlock().getType() == Material.SNOW_BLOCK || e.getBlock().getType() == Material.CLAY){
            if(Utils.randomNumber(0,1) == 0){
                ItemStack itemStack = new ItemStack(Material.SNOWBALL);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName("§f§lSnowBreaker");
                itemMeta.setLore(Arrays.asList("§7Permet de casser le","§7block visé par la boule de neige."));
                itemStack.setItemMeta(itemMeta);
                e.getPlayer().getInventory().addItem(itemStack);
            }
        }
    }

    @EventHandler
    public void projectilHitEvent(ProjectileHitEvent e){
        if(e.getEntity() instanceof Snowball){
            if(e.getHitBlock() != null){
                if(e.getHitBlock().getType() == Material.CLAY || e.getHitBlock().getType() == Material.CLAY ){
                    e.getHitBlock().breakNaturally();
                }
            }else{
                e.setCancelled(true);
            }
        }
    }



}
