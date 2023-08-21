package fr.red_spash.crazygames;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.game.models.GameType;
import fr.red_spash.crazygames.listener.SystemListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static final String PREFIX = "CrazyGames";
    public static final String SEPARATOR = ">>>";
    private GameManager gameManager;
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        this.gameManager = new GameManager(this);
        Bukkit.getPluginManager().registerEvents(new SystemListener(this.gameManager),this);

        for(Player p : Bukkit.getOnlinePlayers()){
            this.gameManager.addPlayerData(p.getUniqueId(),new PlayerData(p.getUniqueId()));
        }

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
