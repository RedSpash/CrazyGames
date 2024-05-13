package fr.red_spash.crazygames.game.games.test;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class TestListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Location location = event.getPlayer().getLocation();

        // Summon the armor stand
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setGravity(false); // Make armor stand not fall

        // Summon the falling block
        FallingBlock fallingBlock = (FallingBlock) location.getWorld().spawnFallingBlock(location, Material.STONE.createBlockData());
        fallingBlock.setDropItem(false);
        fallingBlock.setVelocity(new Vector(0, 0, 0)); // Set velocity to zero to make it static
        armorStand.addPassenger(fallingBlock);

        // Summon the shulker
        Shulker shulker = (Shulker) location.getWorld().spawnEntity(location, EntityType.SHULKER);
        shulker.setAI(false); // Disable AI
        shulker.addPassenger(armorStand);
    }

}
