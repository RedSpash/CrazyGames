package fr.red_spash.crazygames.commands;

import com.google.common.base.MoreObjects;
import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.manager.GameManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.UUID;

public class EditTools implements CommandExecutor {

    public final String GAME_TYPE_DISPLAY_NAME = "§a§lModification du type de jeu";
    public final ItemStack GAME_TYPE = Utils.createFastItemStack(Material.NETHER_STAR,GAME_TYPE_DISPLAY_NAME,"§7Permet de modifier le type de jeu relié à la carte.");
    public final ItemStack SPAWN_LOCATION = Utils.createFastItemStack(Material.RED_BED,"§6Point de Spawn","§7Permet de modifier l'endroit de spawn des joueurs.");
    public final ItemStack CHECKPOINT_MANAGER = Utils.createFastItemStack(Material.DIAMOND_AXE,"§aAjouter un checkpoint","§7Permet d'ajouter un nouveau checkpoint.","§7Clique gauche sur un block pour poser le 1er point","§7Clique droit sur un block pour poser le 2ème point.");
    public final ItemStack SHOW_CHECK_POINT = Utils.createFastItemStack(Material.BOOK,"§aVoir les Checkpoints","§7Permet de voir les checkpoints.");

    private final GameManager gameManager;
    private final EditWorld editWorld;
    private final ArrayList<UUID> editMode;

    public EditTools(GameManager gameManager, EditWorld editWorld) {
        this.gameManager = gameManager;
        this.editWorld = editWorld;
        this.editMode = new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player p){
            if(!this.editWorld.getEditingWorld().contains(p.getWorld())){
                p.sendMessage("§cImpossible de récuperer les outils de modification dans un monde n'étant pas en mode modification !");
                return true;
            }

            if(this.isInEditMode(p)){
                this.removeFromEditMode(p);
                p.sendMessage("§cVous n'êtes plus en mode d'édition.");
            }else{
                this.addToEditMode(p);
                p.sendMessage("§aVous êtes désormais en mode d'édition !");
            }



        }
        return false;
    }

    public void addToEditMode(Player p){
        this.editMode.add(p.getUniqueId());
        this.setPlayerInventory(p);
    }

    public boolean isInEditMode(Player p){
        return this.editMode.contains(p.getUniqueId());
    }

    private void setPlayerInventory(Player p) {
        p.getInventory().clear();

        PlayerInventory playerInventory = p.getInventory();

        playerInventory.setItem(4,GAME_TYPE);
        playerInventory.setItem(1,SHOW_CHECK_POINT);
        playerInventory.setItem(2,CHECKPOINT_MANAGER);
        playerInventory.setItem(6,SPAWN_LOCATION);
    }

    public void removeFromEditMode(Player p){
        this.editMode.remove(p.getUniqueId());
        p.getInventory().clear();
    }

}
