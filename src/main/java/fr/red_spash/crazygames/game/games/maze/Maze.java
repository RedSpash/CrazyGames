package fr.red_spash.crazygames.game.games.maze;

import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class Maze extends Game {

    public Maze() {
        super(GameType.MAZE);
    }

    @Override
    public void startGame() {
        for(int x= -20; x<=20; x++){
            for(int y= -3; y<=3; y++){
                for(int z= -20; z<=20; z++){
                    Block block = super.getGameMap().getSpawnLocation().add(x,y,z).getBlock();
                    if(block.getType().equals(Material.RED_STAINED_GLASS_PANE)){
                        block.breakNaturally(new ItemStack(Material.STONE_SWORD));
                    }
                }
            }
        }
        this.gameManager.getGameInteractions().setBlockWin(Material.LIME_CONCRETE);
    }
}
