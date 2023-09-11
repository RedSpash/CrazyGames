package fr.red_spash.crazygames.game.games.colorshuffle;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.PlayerData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ColorShuffleTask implements Runnable {
    private final ArrayList<Material> materials;
    private final GameManager gameManager;
    private ArrayList<Material> availableMaterials;
    private final ArrayList<Block> blocks;
    private ColorShuffleStatus colorShuffleStatus;
    private Material choosenMaterial;
    private double time;
    private int maxTime;

    public ColorShuffleTask(ArrayList<Block> blocks, GameManager gameManager) {
        this.blocks = blocks;
        this.availableMaterials = new ArrayList<>();
        this.materials = new ArrayList<>(Arrays.asList(
                Material.WHITE_CONCRETE,
                Material.BLACK_CONCRETE,
                Material.BLUE_CONCRETE,
                Material.BROWN_CONCRETE,
                Material.CYAN_CONCRETE,
                Material.GRAY_CONCRETE,
                Material.GREEN_CONCRETE,
                Material.LIGHT_BLUE_CONCRETE,
                Material.LIGHT_GRAY_CONCRETE,
                Material.LIME_CONCRETE,
                Material.MAGENTA_CONCRETE,
                Material.ORANGE_CONCRETE,
                Material.PURPLE_CONCRETE,
                Material.PINK_CONCRETE,
                Material.RED_CONCRETE,
                Material.YELLOW_CONCRETE
        ));
        this.gameManager = gameManager;
        this.setTime(0);
        this.colorShuffleStatus = ColorShuffleStatus.EMPTY_PLATFORM;
    }

    @Override
    public void run() {
        time += 0.1;
        if(time >= this.maxTime){
            switch (colorShuffleStatus) {
                case ALL_COLOR -> {
                    for (Block block : this.blocks) {
                        if (block.getType() != choosenMaterial) {
                            block.setType(Material.AIR);
                        }
                    }
                    colorShuffleStatus = ColorShuffleStatus.ONLY_ONE_COLOR;
                    this.setTime(3);
                    for(Player p : Bukkit.getOnlinePlayers()){
                        p.getInventory().clear();
                    }
                }
                case EMPTY_PLATFORM, ONLY_ONE_COLOR -> {
                    regeneratePlatform();
                    colorShuffleStatus = ColorShuffleStatus.ALL_COLOR;
                    choosenMaterial = this.availableMaterials.get(Utils.randomNumber(0, this.availableMaterials.size() - 1));
                    for(PlayerData playerData : this.gameManager.getAlivePlayerData()){
                        Player p = Bukkit.getPlayer(playerData.getUuid());
                        if(p != null && p.isOnline()){
                            p.getInventory().clear();
                            p.getInventory().setItem(4,new ItemStack(this.choosenMaterial));
                        }
                    }
                    this.setTime(5);
                }
            }
        }

        String message = "";
        int nbr = (int) (this.time * 50 / this.maxTime);
        for(int i = 1; i<=50; i++){
            if(i<=nbr){
                message+="§c|";
            }else{
                message+="§7|";
            }
        }

        for(Player p : Bukkit.getOnlinePlayers()){
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(message+" §7("+("???")+"%)"));
        }


    }

    private void setTime(int i) {
        this.maxTime = 10;
        time = this.maxTime-i;
    }

    public void regeneratePlatform(){
        if(blocks.isEmpty()){
            return;
        }
        Collections.shuffle(this.materials);

        int random_number = Utils.randomNumber(0,6);
        int max = this.materials.size();
        Location random_point = new Location(this.blocks.get(0).getWorld(),Utils.randomNumber(-50,50),this.blocks.get(0).getY(),Utils.randomNumber(-50,50));
        Location location;
        int x;
        this.availableMaterials.clear();
        for(Block block : this.blocks){
            location = block.getLocation();
            x = Math.abs(this.getResult(random_number,location,random_point))%max;
            block.setType(this.materials.get(x));
            if(!this.availableMaterials.contains(block.getType())){
                this.availableMaterials.add(block.getType());
            }
        }
    }

    private int getResult(int randomNumber, Location location, Location random_point) {
        Location middle = new Location(location.getWorld(),0,location.getY(),0);
        switch (randomNumber){
            case 0: return ((location.getBlockX() + location.getBlockZ())/2);
            case 1: return (location.getBlockX()+50)/2;
            case 2: return (location.getBlockZ()+50)/2;
            case 3: return (location.getBlockX() * location.getBlockZ());
            case 4: return (int) random_point.distance(location)/2;
            case 5: return (int) middle.distance(location)/2;
            case 6: {
                int amount = 1;
                while(amount <= 5){
                    Material material = location.add(Utils.randomNumber(-1,1),0,Utils.randomNumber(-1,1)).getBlock().getType();
                    if(this.materials.contains(material)){
                        return this.materials.indexOf(material);
                    }
                    amount++;
                }

                return Utils.randomNumber(0,this.materials.size()-1);
            }
        }
        return 0;
    }
}
