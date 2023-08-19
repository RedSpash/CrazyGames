package fr.red_spash.crazygames;

import fr.red_spash.crazygames.game.manager.GameManager;
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
        this.gameManager = new GameManager();
    }

    @Override
    public void onDisable() {

    }
}
