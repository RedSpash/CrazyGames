package fr.red_spash.crazygames.game.bonus;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.games.blastvillage.BlastVillage;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BonusTask implements Runnable{

    private final ArrayList<Block> blocks;
    private final int additionalHeight;
    private final int maxBonus;
    private final int seconds;
    private final ArrayList<Item> bonusSpawned;
    private final ArrayList<ItemStack> itemStacks;
    private double timer;

    public BonusTask(List<Block> blocks, List<ItemStack> itemStacks, int additionalHeight, int maxBonus, int seconds){
        this.blocks = new ArrayList<>(blocks);
        this.additionalHeight = additionalHeight;
        this.itemStacks = new ArrayList<>(itemStacks);
        this.maxBonus = maxBonus;
        this.seconds = seconds;
        this.bonusSpawned = new ArrayList<>();
        this.timer = 0;
    }

    @Override
    public void run() {

        for(Item item : (ArrayList<Item>) bonusSpawned.clone()){
            if (item.isDead() || item.getLocation().getY() <= 20){
                item.remove();
                bonusSpawned.remove(item);
            }else{
                item.getWorld().spawnParticle(Particle.FIREWORKS_SPARK,item.getLocation(),20,0,0,0,0.1);
            }
        }

        this.timer = this.timer + 0.5;

        if(this.timer >= this.seconds){
            this.timer = 0;
            Block block = this.blocks.get(Utils.randomNumber(0,this.blocks.size()-1));
            Location location = block.getLocation().add(0.5,this.additionalHeight,0.5);
            Item item = location.getWorld().dropItem(location,itemStacks.get(Utils.randomNumber(0,itemStacks.size()-1)));
            item.setGlowing(true);
            this.bonusSpawned.add(item);
        }
    }
}
