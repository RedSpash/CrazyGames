package fr.red_spash.crazygames;

import fr.red_spash.crazygames.game.games.Spleef;
import fr.red_spash.crazygames.game.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private GameManager gameManager;
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.gameManager = new GameManager(this);

        Spleef spleef = new Spleef(this.gameManager);
        Long time = System.currentTimeMillis();
        spleef.loadMap();
        Long after = System.currentTimeMillis();

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                spleef.initializePlayers();
            }
        },20*3);
    }

    @Override
    public void onDisable() {
        this.gameManager.destroyWorlds();
    }
}
