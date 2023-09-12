package fr.red_spash.crazygames.listener.edittools;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.commands.EditTools;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class EditToolsListener implements Listener {
    private final EditTools editTools;

    public EditToolsListener(EditTools editTools) {
        this.editTools = editTools;
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
