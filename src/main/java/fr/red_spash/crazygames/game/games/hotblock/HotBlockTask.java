package fr.red_spash.crazygames.game.games.hotblock;

import fr.red_spash.crazygames.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Arrays;

public class HotBlockTask implements Runnable {

    private final ArrayList<Block> blocks;
    private final HotBlock hotBlock;

    public HotBlockTask(HotBlock hotBlock) {
        this.hotBlock = hotBlock;
        this.blocks = hotBlock.getBlocks();
    }

    @Override
    public void run() {
        for(int i =0; i<=1; i++){
            if(!this.blocks.isEmpty()){
                Block block = blocks.get(Utils.randomNumber(0,blocks.size()-1));
                hotBlock.changeBlock(block);
            }
        }
    }

    public int getBlocksNumber() {
        return blocks.size();
    }
}
