package fr.red_spash.crazygames.game.games.hotblock;

import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;

public class HotBlock extends Game {

    private final ArrayList<Block> blocks;

    public HotBlock() {
        super(GameType.HOT_BLOCK);
        this.blocks = new ArrayList<>();
    }

    @Override
    public void initializePlayers() {
        this.blocks.addAll(super.getBlockPlatform(Material.WHITE_TERRACOTTA));

        super.gameManager.getGameInteractions().setDeathUnderSpawn(2);
        super.initializePlayers();
    }

    @Override
    public void startGame() {
        super.initializeTask(new HotBlockTask(this.blocks),0,1);
    }
}
