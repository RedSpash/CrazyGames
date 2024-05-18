package fr.red_spash.crazygames.game.games.test;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.games.test.moving_blocks.MovingBlock;
import fr.red_spash.crazygames.game.manager.PlayerData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.awt.*;
import java.util.*;

public class TestTask implements Runnable {


    private final Test test;

    public TestTask(Test test) {
        this.test = test;
    }

    private int index = 0;

    @Override
    public void run() {
        for(MovingBlock movingBlock : this.test.getMovingBlocks()){
            movingBlock.move();

            for(Entity nearby : movingBlock.getArmorStand().getWorld().getNearbyEntities(movingBlock.getArmorStand().getLocation().add(0,0,0),0.5,0.5,0.5)){
                if(nearby instanceof Player p){
                    int life = this.test.getGameManager().getPointManager().getPoint(p.getUniqueId());
                    if(life <= 0){
                        this.test.getGameManager().getPlayerManager().eliminatePlayer(p);
                    }else{
                        this.test.getGameManager().getPointManager().addPoint(p.getUniqueId(),-1);
                        p.sendTitle(ChatColor.of(Color.RED)+"§l-1 vie !","§cIl vous reste §l"+(life-1)+" vie"+(life-1 > 1 ? "s":"")+" !",0,20*2,20);
                        p.teleport(p.getLocation().add(0,20,0));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,5*20,10,false,false,false));
                    }
                }
            }

            if(movingBlock.getArmorStand().getLocation().getZ() <= -5){
                movingBlock.destroy();
                this.test.removeMovingBlock(movingBlock);
            }
        }

        if (index % 10 == 0 || Utils.randomNumber(0,20) == 0){
            this.test.spawnShulker();
        }

        index = index + 1;
    }

}
