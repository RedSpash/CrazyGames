package fr.red_spash.crazygames.game.games.bridgeit;

import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BridgeIt extends Game {

    private final ArrayList<Material> woolColor = new ArrayList<>(List.of(Material.WHITE_WOOL,Material.LIGHT_GRAY_WOOL,Material.GRAY_WOOL,Material.BLACK_WOOL,Material.BROWN_WOOL,Material.RED_WOOL,Material.ORANGE_WOOL,Material.YELLOW_WOOL,Material.LIME_WOOL,Material.GREEN_WOOL,Material.CYAN_WOOL,Material.LIGHT_BLUE_WOOL, Material.BLUE_WOOL,Material.PURPLE_WOOL, Material.MAGENTA_WOOL,Material.PINK_WOOL));

    public BridgeIt() {
        super(GameType.BRIDGE_IT);
    }

    @Override
    public void startGame() {
        Collections.shuffle(woolColor);
        int index = 0;
        for(PlayerData playerData : super.gameManager.getAlivePlayerData()){
            Player p = Bukkit.getPlayer(playerData.getUuid());
            p.getInventory().addItem(new ItemStack(woolColor.get(index%(woolColor.size()-1)),64*9));
            index ++;

        }
        this.gameManager.getGameInteractions()
                .setTeleportUnderBlock(1).
                setBlockWin(Material.LIME_CONCRETE)
                .addAllowedToBePlaced(this.woolColor)
                .addAllowedToBeBreak(this.woolColor)
                .setMaxBuildHeight(10);
    }
}
