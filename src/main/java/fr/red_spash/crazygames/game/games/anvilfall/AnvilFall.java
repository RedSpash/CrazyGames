package fr.red_spash.crazygames.game.games.anvilfall;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AnvilFall extends Game {

    private final ArrayList<Block> platform;
    private int blockAmount;
    private AnvilFallTask anvilFallTask;

    public AnvilFall() {
        super(GameType.ANVIL_FALL);
        this.platform = new ArrayList<>();
        this.blockAmount = 0;
    }

    @Override
    public void initializePlayers() {
        this.platform.addAll(super.getBlockPlatform(List.of(Material.RED_WOOL,Material.RED_CONCRETE,Material.LIME_WOOL,Material.LIME_CONCRETE,Material.ORANGE_WOOL,Material.ORANGE_CONCRETE,Material.YELLOW_WOOL,Material.YELLOW_CONCRETE,Material.GREEN_WOOL,Material.GREEN_CONCRETE)));
        super.initializePlayers();
        super.gameManager.getGameInteractions().setDeathUnderSpawn(1).setPve(true);
        this.blockAmount = this.platform.size();
        this.anvilFallTask = new AnvilFallTask(this,this.platform);
    }

    @Override
    public void startGame() {
        super.registerTask(anvilFallTask,2,2);
    }

    @Override
    public List<String> updateScoreboard(Player p) {
        ArrayList<String> scoreBoardMeValue = new ArrayList<>(List.of("Blocks: " + this.blockAmount));
        return scoreBoardMeValue;
    }

    public void removeOneBlock() {
        blockAmount = blockAmount - 1;
    }
}
