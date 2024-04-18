package fr.red_spash.crazygames.game.games.hotblock;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;

public class HotBlockListener implements Listener {
    private final ArrayList<Block> blocks;
    private final HotBlock hotBlock;

    public HotBlockListener(HotBlock hotBlock) {
        this.blocks = hotBlock.getBlocks();
        this.hotBlock = hotBlock;
    }

    @EventHandler
    public void potionSplash(ProjectileHitEvent e){
        if(e.getEntity() instanceof ThrownPotion){
            Location location = null;
            if(e.getHitBlock() != null){
                location = e.getHitBlock().getLocation();
            } else if (e.getHitEntity() != null) {
                location = e.getHitEntity().getLocation();
            }

            if(location != null){
                for(Block block : (ArrayList<Block>) this.blocks.clone()){
                    if (block.getLocation().distance(location) <= 4){
                        this.hotBlock.changeBlock(block);
                        this.hotBlock.changeBlock(block);
                    }
                }
            }
        }
    }
}
