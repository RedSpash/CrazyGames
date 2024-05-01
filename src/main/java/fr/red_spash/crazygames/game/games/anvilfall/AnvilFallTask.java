package fr.red_spash.crazygames.game.games.anvilfall;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;

import java.util.ArrayList;
import java.util.List;

public class AnvilFallTask implements Runnable {
    private final ArrayList<Block> platform;
    private final AnvilFall anvilFall;
    private int round;
    private int tick;
    private final ArrayList<FallingBlock> fallingBlocks;

    public AnvilFallTask(AnvilFall anvilFall, ArrayList<Block> platform) {
        this.anvilFall = anvilFall;
        this.platform = platform;
        this.tick = 0;
        this.round = 0;
        this.fallingBlocks = new ArrayList<>();
    }

    @Override
    public void run() {
        if(tick == 0){
            int moreAnvilStart = 0;
            if(round <= 10){
                moreAnvilStart = 7;
            }
            int randomNumber = Utils.randomNumber(10,20)+moreAnvilStart;
            this.round++;
            for(int i =0; i<= randomNumber; i++){
                if(!this.platform.isEmpty()){
                    Block block = this.platform.remove(Utils.randomNumber(0,this.platform.size()-1));
                    FallingBlock fallingBlock = block.getLocation().getWorld().spawnFallingBlock(block.getLocation().add(0.5,30,0.5), Material.ANVIL.createBlockData());
                    fallingBlock.setDamagePerBlock(500);
                    fallingBlock.setHurtEntities(true);
                    this.fallingBlocks.add(fallingBlock);
                    Bukkit.getScheduler().runTaskLater(this.anvilFall.getGameManager().getMain(), ()->{
                        block.setType(Material.AIR);
                        this.anvilFall.removeOneBlock();
                    },20*3L);
                }
            }
        }

        for(FallingBlock fallingBlock : (List<FallingBlock>) this.fallingBlocks.clone()){
            if(fallingBlock.isDead() || !fallingBlock.isValid() || fallingBlock.getLocation().getY() <= 20){
                fallingBlock.remove();
                this.fallingBlocks.remove(fallingBlock);
            }else{
                Location location = fallingBlock.getLocation();
                location.getWorld().spawnParticle(Particle.CLOUD,location,1,0,0,0,0);
            }
        }

        tick++;

        if(tick >= 40){
            tick = 0;
        }

    }

}
