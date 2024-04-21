package fr.red_spash.crazygames.lobby;

import fr.red_spash.crazygames.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Lobby {

    public static final Location LOBBY_SPAWN = new Location(Bukkit.getWorld("world"),0,100,0,0,0);

    public static final ItemStack SHOW_GAMES = new ItemStackBuilder(Material.SPYGLASS).setName("§a§lVoir les mini jeux").setLore("§fVous permet d'avoir la liste","§fainsi que la description des jeux disponibles").hideAttributes().toItemStack();
    public static final ItemStack SHOW_GAMES = new ItemStackBuilder(Material.SPYGLASS).setName("§a§lVoir les mini jeux").setLore("§fVous permet d'avoir la liste","§fainsi que la description des jeux disponibles").hideAttributes().toItemStack();

    public void giveItems(Player p){
        p.getInventory().clear();
    }

}
