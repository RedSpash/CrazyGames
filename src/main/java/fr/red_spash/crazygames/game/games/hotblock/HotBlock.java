package fr.red_spash.crazygames.game.games.hotblock;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import fr.red_spash.crazygames.scoreboard.RedScoreBoard;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HotBlock extends Game {

    ArrayList<Block> blocks = new ArrayList<>();
    private HotBlockTask hotBlockTask;
    private int blockBefore = 0;

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
        this.hotBlockTask = new HotBlockTask(this.blocks);
        super.initializeTask(this.hotBlockTask,0,1);
    }

    @Override
    public List<String> updateScoreboard() {
        int removedBlock = 0;
        int blockNumber = 0;
        if(hotBlockTask != null){
            blockNumber = this.hotBlockTask.getBlocksNumber();
            int amount = this.blockBefore-blockNumber;
            if(amount > 0){
                removedBlock  = amount;
            }
            this.blockBefore = blockNumber;
        }
        return Collections.singletonList("Blocks: §a"+blockNumber+" §7(§c-"+ Utils.onXString(2,removedBlock+"")+"§7)");
    }
}
