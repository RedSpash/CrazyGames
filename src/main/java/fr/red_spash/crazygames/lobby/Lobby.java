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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Lobby {

    public static final Location LOBBY_SPAWN = new Location(Bukkit.getWorld("world"),0,100,0,0,0);

    public static final ItemStack SHOW_GAMES_ITEM = new ItemStackBuilder(Material.SPYGLASS).setName("§a§lVoir les mini jeux").setLore("§fVous permet d'avoir la liste","§fainsi que la description des jeux disponibles").hideAttributes().toItemStack();
    public static final ItemStack PINK_WOOL_ITEM = new ItemStackBuilder(Material.PINK_WOOL,20).setName("§d§lBlock").setLore("§fVous permet de poser des","§fblocks dans le lobby").hideAttributes().addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1).toItemStack();
    private final GameManager gameManager;
    private final LobbyItemsListener lobbyItemListener;

    public Lobby(GameManager gameManager, Main main) {
        this.gameManager = gameManager;
        this.lobbyItemListener = new LobbyItemsListener(this.gameManager, this);
        Bukkit.getPluginManager().registerEvents(this.lobbyItemListener,main);
    }

    public void giveItems(Player p){
        p.getInventory().clear();

        p.getInventory().setItem(4, SHOW_GAMES_ITEM);
        p.getInventory().setItem(0, PINK_WOOL_ITEM);
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

            lore.replaceAll(s -> "§7" + s);
            lore.add("§f");

            ArrayList<GameMap> maps = new ArrayList<>(this.gameManager.getMapManager().getMaps());
            maps.removeIf(gameMap -> gameMap.getGameType() != gameType);
            lore.add("§fIl y a §l"+maps.size()+" cartes §r§fdisponible !");

            inventory.setItem(i+9,
                    new ItemStackBuilder(gameType.getMaterial())
                            .setName(ChatColor.of(gameType.getColor())+"§l"+gameType.getName())
                            .hideAttributes()
                            .setLore(lore)
                            .toItemStack()
            );
        }

        return inventory;
    }


    public void clearBlocks() {
        this.lobbyItemListener.clearBlocks();
    }
}
