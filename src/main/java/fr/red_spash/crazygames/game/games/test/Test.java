package fr.red_spash.crazygames.game.games.test;

import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Test extends Game {

    private final ArrayList<ArmorStand> shulkers =  new ArrayList<>();

    public Test() {
        super(GameType.TEST);
    }

    @Override
    public void preStart() {
        super.gameManager.getGameInteractions().setEntityMount(true);

        this.startGame();
        super.registerListener(new TestListener());
    }

    @Override
    public void startGame() {
        for(int x = -1; x <= 1; x++){
            for(int z = -1; z <= 1; z++){
                Shulker shulker = (Shulker) this.gameMap.getSpawnLocation().getWorld().spawnEntity(this.gameMap.getSpawnLocation().add(x,1,z), EntityType.SHULKER);

                ArmorStand armorStand = (ArmorStand) this.gameMap.getSpawnLocation().getWorld().spawnEntity(this.gameMap.getSpawnLocation().add(x,1,z), EntityType.ARMOR_STAND);
                armorStand.setInvisible(true);
                armorStand.setMarker(true);
                armorStand.setGravity(false);

                shulker.setInvulnerable(true);
                shulker.setAI(false);
                armorStand.addPassenger(shulker);

                this.shulkers.add(armorStand);
            }
        }
        this.registerTask(new TestTask(this),1,1);
    }

    public List<ArmorStand> getShulkers() {
        return shulkers;
    }
}
