package fr.red_spash.crazygames.game.victory;

import fr.red_spash.crazygames.Utils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VictoryTask implements Runnable{
    private final Victory victory;
    private final List<Color> colors = List.of(Color.RED,Color.GREEN,Color.BLUE,Color.LIME,Color.PURPLE,Color.ORANGE,Color.WHITE,Color.OLIVE,Color.AQUA);

    private final ArrayList<Item> items;
    private int maxTime = 25;
    private int timer;

    public VictoryTask(Victory victory) {
        this.items = new ArrayList<>();
        this.victory = victory;
        this.timer = 0;
    }


    @Override
    public void run() {
        timer ++;
        this.items.add(this.spawnRandomItem(this.victory.getLooserLocation(), List.of(Material.DIRT,Material.ROOTED_DIRT,Material.DEAD_BUSH)));
        this.items.add(this.spawnRandomItem(this.victory.getWinnerLocation(), List.of(Material.DIAMOND,Material.EMERALD,Material.GOLD_INGOT,Material.IRON_INGOT,Material.NETHERITE_INGOT)));

        for(Item item : (List<Item>) items.clone()){
            if(item.getLocation().getY() <= 0){
                this.items.remove(item);
                item.remove();
            }else{
                item.setVelocity(item.getVelocity().divide(new Vector(1.5,1.5,1.5)));
            }
        }
        if(timer % 10 == 0){
            this.maxTime = this.maxTime - 1;
            if(this.maxTime <= 0){
                this.victory.unRegisterListeners();
                World oldWorld = this.victory.getGameMap().getWorld();

                if(oldWorld != null){
                    Utils.teleportPlayersAndRemoveWorld(oldWorld,false);
                    this.victory.getGameMap().deleteWorld(oldWorld);
                }
                this.victory.getGameManager().setActualGame(null);
                for(Player p : Bukkit.getOnlinePlayers()){
                    p.getInventory().clear();
                }
            }
        }
        if(timer % 5 == 0 && this.maxTime > 3){
            Location location = this.victory.getWinnerLocation().add(Utils.randomNumber(-15,15),Utils.randomNumber(-15,15),Utils.randomNumber(-15,15));

            Firework firework = (Firework) location.getWorld().spawnEntity(location,EntityType.FIREWORK);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            fireworkMeta.setPower(3);
            FireworkEffect fireworkEffect = FireworkEffect.builder()
                    .with(FireworkEffect.Type.values()[Utils.randomNumber(0,FireworkEffect.Type.values().length-1)])
                    .withColor(this.colors.get(Utils.randomNumber(0,this.colors.size()-1)))
                    .flicker(this.randomTrueOrFalse())
                    .trail(this.randomTrueOrFalse())
                    .withFade(this.colors.get(Utils.randomNumber(0,this.colors.size()-1)))
                    .build();
            fireworkMeta.addEffect(fireworkEffect);
            firework.setFireworkMeta(fireworkMeta);
            firework.detonate();
        }
    }

    private boolean randomTrueOrFalse() {
        return Utils.randomNumber(0,1) == 0;
    }

    private Item spawnRandomItem(Location looserLocation, List<Material> dirt) {
        looserLocation.add(Utils.randomNumber(-7,7),20,Utils.randomNumber(-7,7));
        return looserLocation.getWorld().dropItemNaturally(looserLocation,new ItemStack(dirt.get(Utils.randomNumber(0,dirt.size()-1))));
    }
}
