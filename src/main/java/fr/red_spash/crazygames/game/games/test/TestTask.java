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

    private int index = 0;

    @Override
    public void run() {
        for(Entity entity : this.test.getShulkers()){
            List<Entity> passagers = entity.getPassengers();
            for(Entity passager : passagers){
                entity.removePassenger(passager);
            }
            entity.teleport(entity.getLocation().add(0,0,-0.5));
            for(Entity passager : passagers){
                entity.addPassenger(passager);
            }

            for(Entity nearby : entity.getWorld().getNearbyEntities(entity.getLocation().add(0,0,0),0.5,0.5,0.5)){
                if(nearby instanceof Player p){
                   this.test.getGameManager().getPlayerManager().eliminatePlayer(p);
               }
            }
        }

        if (index % 10 == 0 || Utils.randomNumber(0,20) == 0){
            this.test.spawnShulker();
        }

        index = index + 1;
    }

}
