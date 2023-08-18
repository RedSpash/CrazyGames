package fr.red_spash.crazygames.game.manager;

import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.map.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class GameManager {
    
    private Game actualGame;
    private final ArrayList<GameMap> maps = new ArrayList<>();
    private static final Path MAP_PATH = Paths.get("MAPS");
    private World world;

    public GameManager(){
        this.loadMaps();
    }

    private void loadMaps() {
        File[] file = new File(MAP_PATH.toUri()).listFiles(File::isDirectory);
        if(file != null){
            ArrayList<File> directories = new ArrayList<File>(
                    Arrays.asList(
                            file
                    )
            );
            this.maps.clear();

            for(File directory : directories){
                String[] name = directory.toString().split("\\\\");

                if(name.length == 1){
                    name = directory.toString().split("/");
                }

                File [] files = directory.listFiles((dir, fileName) -> fileName.equalsIgnoreCase("config.yml"));

                if(files.length != 1){
                    Bukkit.getLogger().warning("La map suivante est invalide : "+directory.toString());
                }else{
                    this.maps.add(new GameMap(name[name.length-1],directory, YamlConfiguration.loadConfiguration(file[0])));
                    Bukkit.getLogger().info("[MAP LOADER]: map "+name+" loaded !");
                }

            }
        }else{
            Bukkit.getLogger().warning("No map found.");
        }

    }

    public ArrayList<GameMap> getMaps() {
        return maps;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
