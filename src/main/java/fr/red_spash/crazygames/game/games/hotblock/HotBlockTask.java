package fr.red_spash.crazygames.game.games.hotblock;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;

public class HotBlockTask implements Runnable {

    private final ArrayList<Material> materialState = new ArrayList<>(Arrays.asList(Material.WHITE_TERRACOTTA,Material.YELLOW_CONCRETE,Material.ORANGE_CONCRETE,Material.RED_CONCRETE,Material.AIR));
    private final ArrayList<Block> blocks;

    public HotBlockTask(ArrayList<Block> blocks) {
        this.blocks = blocks;
        for(Block block : this.blocks){
            block.setType(materialState.get(0));
        }
    }

    @Override
    public void run() {
        for(int i =0; i<=1; i++){
            if(!this.blocks.isEmpty()){
                Block block = blocks.get(Utils.randomNumber(0,blocks.size()-1));
                block.setType(materialState.get(materialState.indexOf(block.getType())+1));
                if(block.getType() == materialState.get(materialState.size()-1)){
                    this.blocks.remove(block);
                }
            }
        }
    }

    public int getBlocksNumber() {
        return blocks.size();
    }
}