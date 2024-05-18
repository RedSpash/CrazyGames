package fr.red_spash.crazygames.game.games.test.moving_blocks;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;

import java.util.List;

public class MovingBlock {

    private final Material material;
    private final double speed;
    private final DyeColor shulkerColor;
    private Shulker shulker;
    private ArmorStand armorStand;
    private FallingBlock fallingBlock;

    public MovingBlock(Material material, double speed, DyeColor shulkerColor){
        this.material = material;
        this.speed = speed;
        this.shulkerColor = shulkerColor;
    }

    public void spawn(Location location){
        this.shulker = (Shulker) location.getWorld().spawnEntity(location, EntityType.SHULKER);
        this.shulker.setInvulnerable(true);
        this.shulker.setAI(false);
        this.shulker.setColor(this.shulkerColor);
        this.shulker.teleport(this.shulker.getLocation().add(0,0.1,0));
        this.shulker.setInvisible(true);
        this.shulker.setRotation(0,0);

        this.armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        this.armorStand.setInvisible(true);
        this.armorStand.setMarker(true);
        this.armorStand.setGravity(false);

        this.fallingBlock = location.getWorld().spawnFallingBlock(location,this.material.createBlockData());
        this.fallingBlock.setHurtEntities(false);
        this.fallingBlock.setDropItem(false);
        this.fallingBlock.setInvulnerable(true);
        this.fallingBlock.setGravity(false);
        this.fallingBlock.setFallDistance(20F*100000);

        this.armorStand.addPassenger(this.shulker);
        this.armorStand.addPassenger(this.fallingBlock);
    }

    public void move(){
        List<Entity> passagers = this.armorStand.getPassengers();
        for(Entity passager : passagers){
            this.armorStand.removePassenger(passager);
        }
        this.armorStand.teleport(this.armorStand.getLocation().add(0,0,-this.speed));
        for(Entity passager : passagers){
            this.armorStand.addPassenger(passager);
        }
    }


    public ArmorStand getArmorStand() {
        return this.armorStand;
    }

    public void destroy() {
        this.shulker.remove();
        this.fallingBlock.remove();
        this.armorStand.remove();
    }
}
