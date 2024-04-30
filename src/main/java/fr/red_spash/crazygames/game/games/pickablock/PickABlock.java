package fr.red_spash.crazygames.game.games.pickablock;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.awt.*;
import java.util.*;

public class PickABlock extends Game {

    private final HashMap<UUID, Integer> points = new HashMap<>();
    private final ArrayList<UUID> endedPlayers = new ArrayList<>();
    private final static int RADIUS = 50;
    private final HashMap<Material, ArrayList<Block>> materialBlockList;
    private Material choosedMaterial = null;
    private final PickABlockListener pickABlockListener;
    private final PickABlockTask pickABlockTask;

    public PickABlock() {
        super(GameType.PICK_A_BLOCK);
        this.materialBlockList = new HashMap<>();
        this.pickABlockListener = new PickABlockListener(this);
        this.pickABlockTask = new PickABlockTask(this);
    }

    @Override
    public void initializePlayers(){
        super.initializePlayers();

        this.findAllBlocks();
    }

    private void findAllBlocks() {
        for(int x = -RADIUS; x <= RADIUS; x++ ){
            for(int y = -RADIUS; y <= RADIUS; y++ ){
                for(int z = -RADIUS; z <= RADIUS; z++ ){
                    Block block = super.gameMap.getSpawnLocation().add(x,y,z).getBlock();
                    Material material = block.getType();
                    if(material.isItem()){
                        ArrayList<Block> list = this.materialBlockList.getOrDefault(material,new ArrayList<>());
                        list.add(block);
                        this.materialBlockList.put(material,list);
                    }
                }
            }
        }

        for(Map.Entry<Material, ArrayList<Block>> entry : ( (HashMap<Material, ArrayList<Block>>) this.materialBlockList.clone()).entrySet()){
            Material material = entry.getKey();
            ArrayList<Block> list = entry.getValue();

            if(list.size() <= 2 || list.size() >= 3500){
                this.materialBlockList.remove(material);
            }
        }
    }

    @Override
    public void preStart() {
        this.gameManager.getGameInteractions().setTeleportUnderBlock(5);
        for(PlayerData playerData : this.gameManager.getPlayerManager().getAlivePlayerData()){
            Player p = Bukkit.getPlayer(playerData.getUuid());
            if(p != null && p.isOnline()){
                p.setAllowFlight(true);
                p.setFlying(true);

                for(Player pl : Bukkit.getOnlinePlayers()){
                    if(!pl.getName().equals(p.getName())){
                        p.hidePlayer(this.gameManager.getMain(),pl);
                    }
                }
            }
        }
    }

    @Override
    public void startGame() {
        this.registerListener(this.pickABlockListener);
        this.registerTask(this.pickABlockTask,0,10);
        this.chooseAnotherBlock();
    }

    public void chooseAnotherBlock() {
        if(this.choosedMaterial != null){
            this.materialBlockList.remove(this.choosedMaterial);
        }

        this.endedPlayers.clear();
        this.pickABlockListener.resetCooldowns();
        this.pickABlockTask.resetTimer();

        ArrayList<Material> availableMaterials = new ArrayList<>(this.materialBlockList.keySet());
        this.choosedMaterial = availableMaterials.get(Utils.randomNumber(0,availableMaterials.size()-1));

        for(Player p : Bukkit.getOnlinePlayers()){
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
            p.sendTitle(
                    "",
                    ChatColor.of(new Color(77, 255, 0, 255))+"§l"+this.choosedMaterial.toString().replace("_"," "),
                    0,
                    20*5,
                    0
            );
            p.getInventory().clear();
            p.getInventory().setHeldItemSlot(4);
            p.getInventory().setItem(4,new ItemStack(this.choosedMaterial));
        }
    }

    public Material getChoosedMaterial() {
        return choosedMaterial;
    }

    public void chosenRightBlock(Player p) {
        int points = this.gameManager.getPlayerManager().getAlivePlayerData().size()-this.endedPlayers.size()+1;
        Bukkit.broadcastMessage(ChatColor.of(Color.GREEN)+"§l"+p.getName()+" §avient de trouver le block ! §e§l+"+(points)+" points !");

        this.endedPlayers.add(p.getUniqueId());
        this.points.put(p.getUniqueId(),this.points.getOrDefault(p.getUniqueId(),0)+points);

        this.replaceBlocks();
        this.chooseAnotherBlock();
    }

    private void replaceBlocks() {
        for(Block block : this.materialBlockList.get(this.choosedMaterial)){
            FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation().add(0.5,0.5,0.5),block.getBlockData());
            fallingBlock.setVelocity(new Vector(
                    Utils.randomNumber(-100,100)/100,
                    Utils.randomNumber(-100,100)/100,
                    Utils.randomNumber(-100,100)/100)
            );
            fallingBlock.setTicksLived(20*10);
            fallingBlock.setFallDistance(20*10f);
            fallingBlock.setDropItem(false);
            fallingBlock.setHurtEntities(true);
            block.setType(Material.RED_STAINED_GLASS);
        }
    }

    public void wrongBlock(Player p) {
        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE,1,1);
        p.sendHurtAnimation(10);
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,20*3,10,false,false,false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS,20*3,10,false,false,false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,20*3,10,false,false,false));
    }

    public HashMap<UUID, Integer> getPoints() {
        return points;
    }

    public ArrayList<UUID> getEndedPlayers() {
        return endedPlayers;
    }
}
