package fr.red_spash.crazygames.game.manager;

import fr.red_spash.crazygames.Main;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import fr.red_spash.crazygames.map.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GameManager {
    
    private Game actualGame;
    private final ArrayList<GameMap> maps = new ArrayList<>();
    private static final String MAP_PATH = "MAPS";
    private World world;

    public GameManager(){
        this.loadMaps();
    }

    private void loadMaps() {
        ClassLoader classLoader = Main.class.getClassLoader();

        ArrayList<String> fileName = new ArrayList<>();
        try {
            String mapsDirectory = "MAPS"; // Le nom du dossier que vous voulez explorer
            Enumeration<URL> resources = classLoader.getResources(mapsDirectory);

            while (resources.hasMoreElements()) {
                URL resourceUrl = resources.nextElement();

                if (resourceUrl.getProtocol().equals("jar")) {
                    JarURLConnection connection = (JarURLConnection) resourceUrl.openConnection();
                    JarFile jarFile = connection.getJarFile();

                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        if (entry.getName().startsWith(mapsDirectory + "/") && entry.isDirectory()) {
                            Bukkit.getConsoleSender().sendMessage("§a"+entry.getName());
                            String directoryName = entry.getName().substring(mapsDirectory.length() + 1);
                            System.out.println("Directory: " + directoryName);
                            if(fileName.add(directoryName.replace("\\",""));)
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File[] file = new File("path").listFiles();
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
                    FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file[0]);
                    GameMap gameMap = new GameMap(name[name.length-1],directory, fileConfiguration);
                    this.maps.add(gameMap);
                    Bukkit.getLogger().info("[MAP LOADER]: map "+gameMap.getName()+" loaded !");
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
