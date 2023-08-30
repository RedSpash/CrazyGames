package fr.red_spash.crazygames;

import fr.red_spash.crazygames.commands.EditTools;
import fr.red_spash.crazygames.commands.EditWorld;
import fr.red_spash.crazygames.commands.SaveWorld;
import fr.red_spash.crazygames.commands.StartGame;
import fr.red_spash.crazygames.game.GameListener;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.listener.SystemListener;
import fr.red_spash.crazygames.listener.edittools.EditToolsListener;
import fr.red_spash.crazygames.scoreboard.ScoreboardTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Location SPAWN;
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

        this.loadCommands();

        Bukkit.getPluginManager().registerEvents(new GameListener(this.gameManager),this);
        Bukkit.getPluginManager().registerEvents(new SystemListener(this.gameManager),this);
        Bukkit.getPluginManager().registerEvents(new EditToolsListener(this.editTools),this);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new ScoreboardTask(this.gameManager), 0, 20);
        SPAWN = new Location(Bukkit.getWorld("world"),0,100,0);

    }

    private void loadCommands() {
        this.editWorld = new EditWorld(this.gameManager);
        this.editTools = new EditTools(this.editWorld);

        getCommand("editWorld").setExecutor( this.editWorld);
        getCommand("saveWorld").setExecutor(new SaveWorld(this.editWorld));
        getCommand("edittools").setExecutor(this.editTools);
        getCommand("startGame").setExecutor(new StartGame(this.gameManager));
    }

    @Override
    public void onDisable() {
        this.gameManager.destroyWorlds();
    }

    public GameManager getGameManager() {
        return this.gameManager;
    }
}
