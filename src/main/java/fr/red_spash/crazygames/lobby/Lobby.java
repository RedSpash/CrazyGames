package fr.red_spash.crazygames.lobby;

import fr.red_spash.crazygames.ItemStackBuilder;
import fr.red_spash.crazygames.Main;
import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.models.GameType;
import fr.red_spash.crazygames.map.GameMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Lobby {

    public static final Location LOBBY_SPAWN = new Location(Bukkit.getWorld("world"),0,100,0,0,0);

    public static final ItemStack SHOW_GAMES_ITEM = new ItemStackBuilder(Material.SPYGLASS).setName("§a§lVoir les mini jeux").setLore("§fVous permet d'avoir la liste","§fainsi que la description des jeux disponibles").hideAttributes().toItemStack();
    private final GameManager gameManager;

    public Lobby(GameManager gameManager, Main main) {
        this.gameManager = gameManager;

        Bukkit.getPluginManager().registerEvents(new LobbyItemsListener(this.gameManager, this),main);
    }

    public void giveItems(Player p){
        p.getInventory().clear();

        p.getInventory().setItem(4, SHOW_GAMES_ITEM);
    }

    public Inventory getGamesInventory() {
        int numberOfLine = GameType.values().length/9;
        if(GameType.values().length%9 != 0){
            numberOfLine = numberOfLine + 1;
        }
        Inventory inventory = Bukkit.createInventory(null, 9*(numberOfLine+2), "§6§lMini-Jeux");

        ItemStack border = new ItemStackBuilder(Material.PINK_STAINED_GLASS_PANE)
                .setName("§f")
                .hideAttributes()
                .toItemStack();
        for(int i = 0; i <= 8; i++){
            inventory.setItem(i,border);
            inventory.setItem(inventory.getSize()-1-i,border);
        }

        for(int i =0; i < GameType.values().length; i++){
            GameType gameType = GameType.values()[i];

            ArrayList<String> lore = Utils.maxWordOnOneLine(7,gameType.getLongDescription());

            lore.replaceAll(s -> "§f" + s);
            lore.add("§f");

            ArrayList<GameMap> maps = new ArrayList<>(this.gameManager.getMapManager().getMaps());
            maps.removeIf(gameMap -> gameMap.getGameType() != gameType);
            lore.add("§6Il y a §l"+maps.size()+" cartes §r§6disponible !");

            inventory.setItem(i+9,
                    new ItemStackBuilder(gameType.getMaterial())
                            .setName(ChatColor.of(gameType.getColor())+gameType.getName())
                            .hideAttributes()
                            .setLore(lore)
                            .toItemStack()
            );
        }

        return inventory;
    }
}
