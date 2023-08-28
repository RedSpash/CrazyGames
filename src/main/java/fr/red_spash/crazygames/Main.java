package fr.red_spash.crazygames;

import fr.red_spash.crazygames.commands.EditTools;
import fr.red_spash.crazygames.commands.EditWorld;
import fr.red_spash.crazygames.commands.SaveWorld;
import fr.red_spash.crazygames.game.GameListener;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.listener.SystemListener;
import fr.red_spash.crazygames.listener.edittools.EditToolsListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static final String PREFIX = "§d§lCrazyGames";
    public static final String SEPARATOR = "§6§l>>>";
    public static final Location SPAWN = new Location(Bukkit.getWorld("world"),0,100,0);
    private GameManager gameManager;
    private static Main instance;
    private EditTools editTools;
    private EditWorld editWorld;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        this.gameManager = new GameManager(this);

        for(Player p : Bukkit.getOnlinePlayers()){
            this.gameManager.addPlayerData(p.getUniqueId(),new PlayerData(p.getUniqueId()));
        }

        this.loadCommands();

        Bukkit.getPluginManager().registerEvents(new GameListener(this.gameManager),this);
        Bukkit.getPluginManager().registerEvents(new SystemListener(this.gameManager),this);
        Bukkit.getPluginManager().registerEvents(new EditToolsListener(this.editTools,this.editWorld),this);

        Bukkit.getScheduler().runTaskLater(Main.getInstance(),
                () -> this.gameManager.startRandomGame()
                ,20*3L
        );
    }

    private void loadCommands() {
        this.editWorld = new EditWorld(this.gameManager);
        this.editTools = new EditTools(this.gameManager,this.editWorld);

        getCommand("editWorld").setExecutor( this.editWorld);
        getCommand("saveWorld").setExecutor(new SaveWorld(this.editWorld));
        getCommand("edittools").setExecutor(this.editTools);

    }

    @Override
    public void onDisable() {
        this.gameManager.destroyWorlds();
    }

    public GameManager getGameManager() {
        return this.gameManager;
    }
}
