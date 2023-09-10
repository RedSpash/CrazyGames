package fr.red_spash.crazygames.game.games.colorshuffle;

import fr.red_spash.crazygames.game.games.hotblock.HotBlockTask;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;

public class ColorShuffle extends Game {

    private final ArrayList<Block> blocks;


    public ColorShuffle() {
        super(GameType.COLOR_SHUFFLE);
        this.blocks = new ArrayList<>();
    }

    @Override
    public void initializePlayers() {
        this.blocks.addAll(super.getBlockPlatform(Material.WHITE_TERRACOTTA,Material.WHITE_CONCRETE));

        super.gameManager.getGameInteractions().setDeathUnderSpawn(2);
        super.initializePlayers();
    }

    @Override
    public void startGame() {
        super.initializeTask(new ColorShuffleTask(this.blocks,super.gameManager),0,2);
    }
}
