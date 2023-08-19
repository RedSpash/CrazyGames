package fr.red_spash.crazygames.map;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class GameMap {
    private Location spawnLocation;
    private final String name;
    private final File file;
    private final GameType gameType;
    private final FileConfiguration fileConfiguration;
    private World world;

    public GameMap(String name, File file,FileConfiguration fileConfiguration) {
        this.name = name;
        this.file = file;
        this.fileConfiguration = fileConfiguration;

        this.gameType = GameType.valueOf(this.fileConfiguration.getString("gametype","").toUpperCase());
    }

    private void loadMapData() {
        this.spawnLocation = new Location(
                this.world,
                this.fileConfiguration.getDouble("spawnlocation.x",0.0),
                this.fileConfiguration.getDouble("spawnlocation.y",101.5),
                this.fileConfiguration.getDouble("spawnlocation.z",0.0),
                this.fileConfiguration.getInt("spawnlocation.yaw",0),
                this.fileConfiguration.getInt("spawnlocation.pitch",0)
        );
    }

    public String getName() {
        return this.name;
    }

    public File getFile() {
        return this.file;
    }

    public World loadWorld(){
        Path path = Paths.get(this.file.getPath());
        String name = System.currentTimeMillis()+"";
        Path path2 = Paths.get(name);

        try {
            Utils.copyDirectory(path.toString(), path2.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.world = Bukkit.createWorld(new WorldCreator(name));
        if(world == null){
            world = Bukkit.getWorld(name);
        }

        this.loadMapData();
        return world;
    }

    public GameType getGameType() {
        return this.gameType;
    }

    public FileConfiguration getFileConfiguration() {
        return this.fileConfiguration;
    }

    public Class<? extends Game> classGame(){
        return this.gameType.getGameClass();
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }
}
