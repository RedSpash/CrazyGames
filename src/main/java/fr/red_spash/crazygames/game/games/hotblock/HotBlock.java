package fr.red_spash.crazygames.game.games.hotblock;

import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;

public class HotBlock extends Game {

    ArrayList<Block> blocks = new ArrayList<>();

    public HotBlock() {
        super(GameType.HOT_BLOCK);
    }

    @Override
    public void initializePlayers() {
        Location location = super.getGameMap().getSpawnLocation();
        int count = 0;
        while(location.getBlock().getType() != Material.WHITE_TERRACOTTA && count<=50){
            location.add(0,-1,0);
            count++;
        }

        for(int x=-75; x<=75; x++){
            for(int z=-75; z<=75; z++){
                Location blockLocation = location.clone().add(x,0,z);
                if(blockLocation.getBlock().getType() == Material.WHITE_TERRACOTTA){
                    this.blocks.add(blockLocation.getBlock());
                }
            }
        }

        super.gameManager.getGameInteractions().setDeathUnderSpawn(5);

        super.initializePlayers();
    }

    @Override
    public void startGame() {
        super.initializeTask(new HotBlockTask(this.blocks),0,1);
    }
}
