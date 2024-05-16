package fr.red_spash.crazygames.game.games.test;

import fr.red_spash.crazygames.Utils;
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

        for(int x = -10; x <= 10; x ++){
            for(int z = -5; z <= 5; z ++){
                Location location = this.gameMap.getSpawnLocation().add(x,-1,z);
                location.getBlock().setType(Material.STONE);
            }
        }

        super.registerListener(new TestListener());
    }

    @Override
    public void startGame() {
        this.registerTask(new TestTask(this),1,1);
        super.gameManager.getGameInteractions()
                .setPvp(true)
                .setHitCooldown(5)
                .setDeathUnderSpawn(1);

    }

    public List<ArmorStand> getShulkers() {
        return shulkers;
    }

    public void spawnShulker() {
        Location loca = this.gameMap.getSpawnLocation().add(Utils.randomNumber(-11,11),0,Utils.randomNumber(-11,11)+50);
        this.spawnShulker(loca);
        this.spawnShulker(loca.add(0,1,0));
    }

    private void spawnShulker(Location loca) {
        Shulker shulker = (Shulker) this.gameMap.getSpawnLocation().getWorld().spawnEntity(loca, EntityType.SHULKER);

        ArmorStand armorStand = (ArmorStand) this.gameMap.getSpawnLocation().getWorld().spawnEntity(loca, EntityType.ARMOR_STAND);
        armorStand.setInvisible(true);
        armorStand.setMarker(true);
        armorStand.setGravity(false);

        shulker.setInvulnerable(true);
        shulker.setAI(false);
        armorStand.addPassenger(shulker);

        this.shulkers.add(armorStand);
    }
}
