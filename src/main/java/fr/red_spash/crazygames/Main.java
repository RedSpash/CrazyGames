package fr.red_spash.crazygames;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.models.GameType;
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

        Bukkit.getScheduler().runTaskLater(Main.getInstance(),
                () -> this.gameManager.startGame(GameType.SPLEEF)
                ,20*3L
        );
    }

    @Override
    public void onDisable() {
        this.gameManager.destroyWorlds();
    }

    public GameManager getGameManager() {
        return this.gameManager;
    }
}
