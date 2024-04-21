package fr.red_spash.crazygames;

import fr.red_spash.crazygames.commands.*;
import fr.red_spash.crazygames.game.GameListener;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.listener.SystemListener;
import fr.red_spash.crazygames.listener.edittools.EditToolsListener;
import fr.red_spash.crazygames.lobby.Lobby;
import fr.red_spash.crazygames.lobby.LobbyItemsListener;
import fr.red_spash.crazygames.lobby.LobbyListener;
import fr.red_spash.crazygames.scoreboard.ScoreboardTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Location spawn;
    private GameManager gameManager;
    private static Main instance;
    private EditTools editTools;

    @Override
    public void onEnable() {
        instance = this;

        this.gameManager = new GameManager(this);

        this.loadCommands();
        getServer().getMessenger().registerOutgoingPluginChannel( this, "BungeeCord");

        Bukkit.getPluginManager().registerEvents(new GameListener(this.gameManager),this);
        Bukkit.getPluginManager().registerEvents(new SystemListener(this.gameManager),this);
        Bukkit.getPluginManager().registerEvents(new EditToolsListener(this.editTools,this.gameManager),this);
        Bukkit.getPluginManager().registerEvents(new LobbyListener(this.gameManager),this);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new ScoreboardTask(this.gameManager), 0, 20);

        this.setSpawn(new Location(Bukkit.getWorld("world"),0,100,0));
    }

    private void setSpawn(Location world) {
        spawn = world;
    }

    private void loadCommands() {
        EditWorld editWorld = new EditWorld(this.gameManager);
        this.editTools = new EditTools(editWorld);

        getCommand("leave").setExecutor(new Leave());
        getCommand("editWorld").setExecutor(editWorld);
        getCommand("saveWorld").setExecutor(new SaveWorld(editWorld));
        getCommand("edittools").setExecutor(this.editTools);
        getCommand("startGame").setExecutor(new StartGame(this.gameManager));
    }

    @Override
    public void onDisable() {
        this.gameManager.destroyWorlds();
        for(Player p : Bukkit.getOnlinePlayers()){
            for(Player pl : Bukkit.getOnlinePlayers()){
                if(!p.canSee(pl)){
                    p.showPlayer(this,pl);
                }
            }
        }
    }


    public GameManager getGameManager() {
        return this.gameManager;
    }

    public static Main getInstance() {
        return instance;
    }

}
