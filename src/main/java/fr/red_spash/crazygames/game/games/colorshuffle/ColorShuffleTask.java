package fr.red_spash.crazygames.game.games.colorshuffle;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.PlayerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ColorShuffleTask implements Runnable {
    private static final double NUMBER_OF_SONG = 30.0;
    private final ArrayList<Material> materials;
    private final GameManager gameManager;
    private final ArrayList<Material> availableMaterials;
    private final ArrayList<Block> blocks;
    private final HashMap<Material, ColorTranslation> translations;
    private ColorShuffleStatus colorShuffleStatus;
    private Material chosenMaterial;
    private double time;
    private double maxTime;
    private int roundNumber;
    private double nextSong;

    public ColorShuffleTask(List<Block> blocks, Map<Material,ColorTranslation> allowedMaterialsTranslation, GameManager gameManager) {
        this.nextSong = -1;
        this.roundNumber = 0;
        this.blocks = new ArrayList<>(blocks);
        this.availableMaterials = new ArrayList<>();
        this.materials = new ArrayList<>(allowedMaterialsTranslation.keySet());
        this.translations = new HashMap<>(allowedMaterialsTranslation);
        this.gameManager = gameManager;
        this.setTime(0);
        this.colorShuffleStatus = ColorShuffleStatus.EMPTY_PLATFORM;
    }

    @Override
    public void run() {
        time += 0.05;

        if(time >= this.nextSong && this.nextSong >= 0){
            this.playSound((1.5*((this.time*100/this.maxTime)/100))+0.5);
            this.nextSong += Utils.round(this.maxTime/ColorShuffleTask.NUMBER_OF_SONG,100.0);
        }

        if(time >= this.maxTime){
            switch (colorShuffleStatus) {
                case ALL_COLOR -> {
                    this.removeOtherBlocks();
                    this.nextSong = -1;
                    this.setTime(2);
                }
                case EMPTY_PLATFORM, ONLY_ONE_COLOR -> {
                    this.roundNumber++;
                    this.resetGame();
                    this.setTime(2);
                }
                case WAITING_COLOR -> {
                    this.chooseRandomColor();
                    this.nextSong = Utils.round((this.maxTime / 2) / ColorShuffleTask.NUMBER_OF_SONG, 100.0) + this.maxTime / 2+0.15;
                    this.setTime(this.getMaxTime());
                }
            }
        }
        if(this.chosenMaterial != null){
            StringBuilder message = new StringBuilder();
            int nbr = (int) ((this.time+0.05) * 50 / this.maxTime);
            for(int i = 1; i<=50; i++){
                if(i<=nbr){
                    message.append("§c§l|");
                }else{
                    message.append("§f§l|");
                }
            }


            for(Player p : Bukkit.getOnlinePlayers()){
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(this.getChosenMaterialName()+" "+message+" §7("+Utils.onXString(3,((int)((this.time+0.05)*100/this.maxTime))+"%)")));
            }
        }
    }

    private void chooseRandomColor() {
        chosenMaterial = this.availableMaterials.get(Utils.randomNumber(0, this.availableMaterials.size() - 1));
        for(PlayerData playerData : this.gameManager.getPlayerManager().getAlivePlayerData()){
            Player p = Bukkit.getPlayer(playerData.getUuid());
            if(p != null && p.isOnline()){
                p.getInventory().clear();
                p.getInventory().setItem(4,new ItemStack(this.chosenMaterial));
                ColorTranslation colorTranslation = this.translations.get(this.chosenMaterial);
                p.sendTitle("",ChatColor.of(colorTranslation.color())+"§l"+colorTranslation.name(),0,50,0);
            }
        }
        this.colorShuffleStatus = ColorShuffleStatus.ALL_COLOR;
    }

    private void playSound(double power) {
        if(power <= 0){
            power = 0;
        }
        for(Player p : Bukkit.getOnlinePlayers()){
            p.playSound(p.getLocation().add(0,2,0), Sound.BLOCK_NOTE_BLOCK_BIT, 1, (float) power);
        }
    }

    private void removeOtherBlocks() {
        for (Block block : this.blocks) {
            if (block.getType() != chosenMaterial) {
                block.setType(Material.AIR);
                block.getWorld().spawnParticle(Particle.CLOUD,block.getLocation().add(0.5,0.5,0.5),1,0.25,0.25,0.25,0);
            }
        }
        colorShuffleStatus = ColorShuffleStatus.ONLY_ONE_COLOR;
        for(Player p : Bukkit.getOnlinePlayers()){
            p.getInventory().clear();
        }
        chosenMaterial = null;
    }

    private void resetGame() {
        regeneratePlatform();
        colorShuffleStatus = ColorShuffleStatus.WAITING_COLOR;
    }

    public double getMaxTime() {
        double estimatedTime = 6 - (this.roundNumber*0.15);
        if(estimatedTime < 2.0){
            estimatedTime = 2.0;
        }
        return estimatedTime;
    }

    private void setTime(double i) {
        this.maxTime = i;
        this.time = 0;
    }

    public void regeneratePlatform(){
        if(blocks.isEmpty()){
            return;
        }
        Collections.shuffle(this.materials);

        int randomNumber = Utils.randomNumber(0,12);
        int max = this.materials.size();
        Location randomPoint = new Location(this.blocks.get(0).getWorld(),Utils.randomNumber(-50,50),this.blocks.get(0).getY(),Utils.randomNumber(-50,50));
        Location location;
        this.availableMaterials.clear();
        for(Block block : this.blocks){
            location = block.getLocation();
            block.setType(this.materials.get(Math.abs(this.getResult(randomNumber,location,randomPoint))%max));
            if(!this.availableMaterials.contains(block.getType())){
                this.availableMaterials.add(block.getType());
            }
        }
    }

    private int getResult(int randomNumber, Location location, Location randomPoint) {
        Location middle = new Location(location.getWorld(),0,location.getY(),0);
        switch (randomNumber){
            case 0: return ((location.getBlockX() + location.getBlockZ())/2);
            case 1: return (location.getBlockX()+50)/2;
            case 2: return (location.getBlockZ()+50)/2;
            case 3: return (location.getBlockX() * location.getBlockZ());
            case 4: return (int) randomPoint.distance(location)/2;
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
            case 7: return (location.getBlockX()+50-location.getBlockZ())/6;
            case 8: return ((location.getBlockX() + location.getBlockZ())/2)*(location.getBlockX()+50)/2;
            case 9: return (((location.getBlockX()+5000)/3)*((location.getBlockZ()+5000)/3))/2;
            case 10: return Math.max(location.getBlockX(),location.getBlockZ())+100;
            case 11: return Math.min(location.getBlockX(),location.getBlockZ())+100;
            case 12:return Math.max(location.getBlockX(),location.getBlockZ())+100 + Math.min(location.getBlockX(),location.getBlockZ())+100;
            default:
                return 1;
        }
    }

    public double getRemainingTime(){
        return this.maxTime-this.time;
    }

    public ColorShuffleStatus getColorShuffleStatus() {
        return colorShuffleStatus;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public String getChosenMaterialName() {
        ColorTranslation colorTranslation = this.translations.get(this.chosenMaterial);

        String materialChosen = "§c§lAUCUN";
        if(colorTranslation != null){
            materialChosen = "§"+colorTranslation.colorCode()+"§l"+colorTranslation.name();
        }
        return materialChosen;
    }
}
