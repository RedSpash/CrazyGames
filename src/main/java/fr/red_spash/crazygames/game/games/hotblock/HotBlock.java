package fr.red_spash.crazygames.game.games.hotblock;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HotBlock extends Game {

    ArrayList<Block> blocks = new ArrayList<>();
    private HotBlockTask hotBlockTask;
    private int blockBefore;

    public HotBlock() {
        super(GameType.HOT_BLOCK);
    }

    @Override
    public void initializePlayers() {
        this.blocks.addAll(super.getBlockPlatform(Material.WHITE_TERRACOTTA));

        super.gameManager.getGameInteractions().setDeathUnderSpawn(2);
        super.initializePlayers();
    }

    @Override
    public void startGame() {
        this.hotBlockTask = new HotBlockTask(this.blocks);
        super.registerTask(this.hotBlockTask,0,1);
    }

    @Override
    public List<String> updateScoreboard(Player p) {
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
