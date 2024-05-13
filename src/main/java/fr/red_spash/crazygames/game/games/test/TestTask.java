package fr.red_spash.crazygames.game.games.test;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.manager.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.util.Vector;

import java.util.*;

public class TestTask implements Runnable {


    private final Test test;

    public TestTask(Test test) {
        this.test = test;
    }

    @Override
    public void run() {
        for(Entity entity : this.test.getShulkers()){
            List<Entity> passagers = entity.getPassengers();
            for(Entity passager : passagers){
                entity.removePassenger(passager);
            }
            entity.teleport(entity.getLocation().add(0.1,0,0.1));
            for(Entity passager : passagers){
                entity.addPassenger(passager);
            }

            for(Entity nearby : entity.getWorld().getNearbyEntities(entity.getLocation().add(0,1,0),0.30,1,0.30)){
                if(nearby instanceof Player p){
                    Location loc = p.getLocation().clone();

                    // Here we're keeping the same yaw and pitch
                    p.teleport(new Location(loc.getWorld(), p.getLocation().getX()+0.1, p.getLocation().getY(), p.getLocation().getZ()+0.1, loc.getYaw(), loc.getPitch()));
                }
            }
        }
    }

}
