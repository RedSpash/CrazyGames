package fr.red_spash.crazygames.game.games.pickablock;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.game.manager.PointManager;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.awt.*;
import java.util.List;
import java.util.*;

public class PickABlock extends Game {
    private static final int RADIUS = 50;
    private final ArrayList<UUID> endedPlayers = new ArrayList<>();
    private final HashMap<Material, ArrayList<Block>> materialBlockList;
    private final PointManager pointManager;
    private Material choosedMaterial = null;
    private final PickABlockListener pickABlockListener;
    private final PickABlockTask pickABlockTask;

    public PickABlock() {
        super(GameType.PICK_A_BLOCK);
        this.materialBlockList = new HashMap<>();
        this.pickABlockListener = new PickABlockListener(this);
        this.pickABlockTask = new PickABlockTask(this);
        this.pointManager = this.getGameManager().getPointManager();
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
                this.pointManager.addPoint(p.getUniqueId(),0);
                p.setAllowFlight(true);
                p.setFlying(true);
            }
        }
    }

    @Override
    public void startGame() {
        this.gameManager.getGameInteractions()
                .setMoveItemInventory(false);
        for(PlayerData playerData : this.gameManager.getPlayerManager().getAlivePlayerData()){
            Player p = Bukkit.getPlayer(playerData.getUuid());
            if(p != null && p.isOnline()){
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,PotionEffect.INFINITE_DURATION,5,false,false));
            }
        }

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
        String materialName = this.choosedMaterial.toString().replace("_"," ");
        for(Player p : Bukkit.getOnlinePlayers()){
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT,1,0);
            p.sendTitle(
                    "",
                    ChatColor.of(new Color(77, 255, 0, 255))+"§l"+materialName,
                    0,
                    20*5,
                    0
            );
            p.getInventory().clear();
            p.getInventory().setHeldItemSlot(4);
            p.getInventory().setItem(4,new ItemStack(this.choosedMaterial));
        }

        Bukkit.broadcastMessage("§aVous devez trouver "+ChatColor.of(Color.GREEN)+"§l"+materialName+" §a!");
    }

    public Material getChoosedMaterial() {
        return choosedMaterial;
    }

    public void chosenRightBlock(Player p) {
        int playerPoints = this.gameManager.getPlayerManager().getAlivePlayerData().size()-this.endedPlayers.size();
        Bukkit.broadcastMessage(ChatColor.of(Color.GREEN)+"§l"+p.getName()+" §avient de trouver le block ! §e§l+"+(playerPoints)+" points !");
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT,1,1);
        p.getInventory().clear();
        for(Player pl : Bukkit.getOnlinePlayers()){
            if(!pl.getUniqueId().equals(p.getUniqueId())){
                pl.playSound(pl.getLocation(), Sound.ENCHANT_THORNS_HIT,1,2);
            }
        }
        p.sendTitle("§a","§a§l+ "+playerPoints+" points !",0,20,20);
        this.endedPlayers.add(p.getUniqueId());
        this.pointManager.addPoint(p.getUniqueId(),playerPoints);


        if(this.endedPlayers.size() == this.gameManager.getPlayerManager().getAlivePlayerData().size()){
            for(PlayerData playerData : this.gameManager.getPlayerManager().getAlivePlayerData()){
                Player pl = Bukkit.getPlayer(playerData.getUuid());
                if(pl != null && pl.isOnline()){
                    for(PlayerData playerData2 : this.gameManager.getPlayerManager().getAlivePlayerData()){
                        Player pl2 = Bukkit.getPlayer(playerData2.getUuid());
                        if(pl2 != null && pl2.isOnline() && !pl2.getUniqueId().equals(pl.getUniqueId())){
                            pl.showPlayer(this.gameManager.getMain(),pl2);
                        }
                    }
                }
            }
            this.replaceBlocks();
            this.chooseAnotherBlock();
        }else{
            for(PlayerData playerData : this.gameManager.getPlayerManager().getAlivePlayerData()){
                Player pl = Bukkit.getPlayer(playerData.getUuid());
                if(pl != null && pl.isOnline() && !pl.getUniqueId().equals(p.getUniqueId())){
                    pl.hidePlayer(this.gameManager.getMain(),p);
                }
            }
        }
    }

    public void replaceBlocks() {
        for(Block block : this.materialBlockList.get(this.choosedMaterial)){
            ArmorStand armorStand = (ArmorStand) block.getWorld().spawnEntity(block.getLocation().add(0.5,-1.5,0.5), EntityType.ARMOR_STAND);

            armorStand.setInvisible(true);
            armorStand.setMarker(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.getEquipment().setHelmet(new ItemStack(block.getType()));

            block.setType(Material.BARRIER);
        }
    }

    public void wrongBlock(Player p) {
        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE,1,1);
        p.sendHurtAnimation(10);
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,20*3,10,false,false,false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS,20*3,10,false,false,false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,20*3,10,false,false,false));
    }


    public List<UUID> getEndedPlayers() {
        return endedPlayers;
    }

    public String isQualified(Player p) {
        return this.pointManager.getQualifiedUUID().contains(p.getUniqueId()) ?
                "§a§lQUALIFICATION" : "§4§lÉLIMINATION";
    }

    public String getTop(Player p) {
        int point = this.pointManager.getPoint(p.getUniqueId());

        ArrayList<Integer> scores = this.pointManager.getOrderedPoints();
        String top = "?";
        for(int i = 0; i < scores.size(); i++){
            if(scores.get(i) == point){
                top = (i+1)+"";
            }
        }
        return top;
    }

    @Override
    public int getMaxTime() {
        return 60*3;
    }
}
