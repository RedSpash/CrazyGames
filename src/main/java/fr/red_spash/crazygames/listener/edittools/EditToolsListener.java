package fr.red_spash.crazygames.listener.edittools;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.commands.EditTools;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.game.models.GameType;
import fr.red_spash.crazygames.map.CheckPoint;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class EditToolsListener implements Listener {
    private final EditTools editTools;
    private final GameManager gameManager;

    public EditToolsListener(EditTools editTools, GameManager gameManager) {
        this.editTools = editTools;
        this.gameManager = gameManager;
    }

    @EventHandler
    public void playerDropItemEvent(PlayerDropItemEvent e){
        Player p = e.getPlayer();
        if(this.editTools.isInEditMode(p)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        if(!this.editTools.isInEditMode(p))return;
        e.setCancelled(true);

        if(e.getClickedInventory() == null) return;
        if(!e.getView().getTitle().equalsIgnoreCase(EditTools.GAME_TYPE_DISPLAY_NAME))return;

        ItemStack itemStack = e.getCurrentItem();
        if(itemStack == null)return;
        if(!itemStack.hasItemMeta())return;
        if(!itemStack.getItemMeta().hasDisplayName())return;
        String name = itemStack.getItemMeta().getDisplayName().replace("§e§l","");
        GameType gameType = null;

        for(GameType search : GameType.values()){
            if(name.contains(search.getName())){
                gameType = search;
            }
        }
        if(gameType != null){
            FileConfiguration fileConfiguration = this.getFileConfigurationOfWorld(p.getWorld());
            fileConfiguration.set("gametype",gameType.toString().toUpperCase());
            p.sendMessage("§aLe mode de la carte vient d'être définie sur '"+gameType+"' avec succès !");
            this.saveFileConfiguration(p.getWorld(),fileConfiguration);
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
        }else{
            p.sendMessage("§cImpossible de trouver le mode de jeu !");
        }

    }





    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(this.editTools.isInEditMode(p)){
            e.setCancelled(true);

            ItemStack itemStack = e.getItem();

            if(EditTools.GAME_TYPE.isSimilar(itemStack)){
                Inventory inventory = Bukkit.createInventory(null,9*6, EditTools.GAME_TYPE_DISPLAY_NAME);
                int index = 0;
                for(GameType gameType : GameType.values()){
                    inventory.setItem(index, Utils.createFastItemStack(Material.PAPER,"§a§l"+gameType.getName(),new ArrayList<>(Utils.splitSentance(gameType.getLongDescription()))));
                    index++;
                }
                p.openInventory(inventory);
            } else if (EditTools.SPAWN_LOCATION.isSimilar(itemStack)) {
                FileConfiguration fileConfiguration = this.getFileConfigurationOfWorld(p.getWorld());
                fileConfiguration.set("spawnlocation.x",p.getLocation().getX());
                fileConfiguration.set("spawnlocation.y",p.getLocation().getY());
                fileConfiguration.set("spawnlocation.z",p.getLocation().getZ());
                fileConfiguration.set("spawnlocation.yaw",p.getLocation().getYaw());
                fileConfiguration.set("spawnlocation.pitch",p.getLocation().getPitch());
                p.sendMessage("§aEndroit de spawn définie avec succès !");
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                this.saveFileConfiguration(p.getWorld(),fileConfiguration);
            }else if(EditTools.CHECKPOINT_MANAGER.isSimilar(itemStack) && e.getClickedBlock() != null){
                PlayerData playerData = this.gameManager.getPlayerData(p.getUniqueId());
                switch (e.getAction()){
                    case LEFT_CLICK_BLOCK -> {
                        playerData.setLeftCheckPointCreation(e.getClickedBlock().getLocation());
                        p.sendMessage("§aPremier point positionné.");
                    }
                    case RIGHT_CLICK_BLOCK -> {
                        playerData.setRightCheckPointCreation(e.getClickedBlock().getLocation());
                        p.sendMessage("§aDeuxième point positionné.");
                    }
                }
                if(playerData.getRightCheckPointCreation() != null && playerData.getLeftCheckPointCreation() != null){
                    try{
                        FileConfiguration fileConfiguration = this.getFileConfigurationOfWorld(p.getWorld());
                        int lastId;

                        if(fileConfiguration.isSet("checkpoints")){
                            lastId = fileConfiguration.getConfigurationSection("checkpoints").getKeys(false).size();
                        }else{
                            lastId = 0;
                        }
                        CheckPoint checkPoint = new CheckPoint(lastId,playerData.getRightCheckPointCreation(),playerData.getLeftCheckPointCreation());
                        checkPoint.save(fileConfiguration);
                        this.saveFileConfiguration(p.getWorld(),fileConfiguration);
                        p.sendMessage("§aCréation d'un checkpoint avec l'id "+lastId+" !");
                        playerData.setRightCheckPointCreation(null);
                        playerData.setLeftCheckPointCreation(null);
                    }catch (Exception ee){
                        p.sendMessage("§cUne erreur est survenue ! ("+ee.getCause()+")");
                    }
                }
            } else if (EditTools.SHOW_CHECK_POINT.isSimilar(itemStack)) {
                Inventory inventory = Bukkit.createInventory(null,9*6,"§a§lCheckpoints de la carte");
                FileConfiguration fileConfiguration = this.getFileConfigurationOfWorld(p.getWorld());
                if(fileConfiguration.isSet("checkpoints")){
                    fileConfiguration.getConfigurationSection("checkpoints").getKeys(false).forEach(id ->{
                        CheckPoint checkPoint = new CheckPoint(fileConfiguration,"checkpoints",id);
                        Material material = Material.GOLD_NUGGET;

                        inventory.setItem(Integer.parseInt(id),Utils.createFastItemStack(material,"Checkpoint n°"+id));
                    });
                }
                p.openInventory(inventory);
            } else if (EditTools.ADD_SPAWN.isSimilar(itemStack)) {
                FileConfiguration fileConfiguration = this.getFileConfigurationOfWorld(p.getWorld());
                int lastId;

                if(fileConfiguration.isSet("spawns")){
                    lastId = fileConfiguration.getConfigurationSection("spawns").getKeys(false).size();
                }else{
                    lastId = 0;
                }

                fileConfiguration.set("spawns."+lastId+".x",p.getLocation().getX());
                fileConfiguration.set("spawns."+lastId+".y",p.getLocation().getY());
                fileConfiguration.set("spawns."+lastId+"..z",p.getLocation().getZ());
                fileConfiguration.set("spawns."+lastId+".yaw",p.getLocation().getYaw());
                fileConfiguration.set("spawns."+lastId+".pitch",p.getLocation().getPitch());
                p.sendMessage("§aAjout du spawn n°"+lastId);
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                this.saveFileConfiguration(p.getWorld(),fileConfiguration);
            }
        }
    }

    @EventHandler
    public void switchItem(PlayerItemHeldEvent e){
        Player p = e.getPlayer();
        ItemStack itemStack = p.getInventory().getItem(e.getPreviousSlot());
        if(itemStack != null){
            if(EditTools.CHECKPOINT_MANAGER.isSimilar(itemStack)){
                PlayerData playerData = this.gameManager.getPlayerData(p.getUniqueId());
                if(playerData.getLeftCheckPointCreation() != null || playerData.getRightCheckPointCreation() != null){
                    playerData.setLeftCheckPointCreation(null);
                    playerData.setRightCheckPointCreation(null);
                    p.sendMessage("§cLa création du checkpoint en cours vient d'être annulée.");
                }
            }
        }
    }

    private void saveFileConfiguration(World world, FileConfiguration fileConfiguration) {
        File file = new File(world.getWorldFolder(),"config.yml");
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler()
    public void blockBreakEvent(BlockBreakEvent e){
        Player p = e.getPlayer();
        if(this.editTools.isInEditMode(p)){
            e.setCancelled(true);
        }
    }

    @EventHandler()
    public void blockPlaceEvent(BlockPlaceEvent e){
        Player p = e.getPlayer();
        if(this.editTools.isInEditMode(p)){
            e.setCancelled(true);
        }
    }


    public FileConfiguration getFileConfigurationOfWorld(World world){
        File file = new File(world.getWorldFolder(),"config.yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().warning("§cImpossible de créer le fichier de configuration !");
                throw new RuntimeException(e);
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

}
