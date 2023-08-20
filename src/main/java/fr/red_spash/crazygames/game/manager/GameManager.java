package fr.red_spash.crazygames.game.manager;

import fr.red_spash.crazygames.Main;
import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.interaction.GameInteraction;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import fr.red_spash.crazygames.map.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;

import static org.bukkit.Bukkit.getLogger;

public class GameManager {

    private final ArrayList<GameType> playedGameType = new ArrayList<>();
    private final ArrayList<GameMap> playedGameMap = new ArrayList<>();
    private GameStatus gameStatus = GameStatus.WAITING;
    private final GameInteraction gameInteraction;
    private Game actualGame;
    private final ArrayList<GameMap> maps = new ArrayList<>();
    private World world;

    public GameManager(Main main){
        this.gameInteraction = new GameInteraction(main);
        this.loadMaps();
    }

    private void loadMaps() {
        this.maps.clear();

        File mapsFolder = new File(Main.getInstance().getDataFolder(), "maps");

        if (mapsFolder.exists() && mapsFolder.isDirectory()) {
            File[] mapFolders = mapsFolder.listFiles(File::isDirectory);

            if (mapFolders != null) {
                for (File mapFolder : mapFolders) {
                    ArrayList<File> directories = new ArrayList<File>(Collections.singletonList(mapFolder));

                    for (File directory : directories) {
                        String[] name = directory.toString().split("\\\\");

                        if (name.length == 1) {
                            name = directory.toString().split("/");
                        }

                        File[] files = directory.listFiles((dir, fileName) -> fileName.equalsIgnoreCase("config.yml"));

                        if (files == null || files.length != 1) {
                            getLogger().warning("La map suivante est invalide : " + directory.toString());
                        } else {
                            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(files[0]);
                            GameMap gameMap = new GameMap(name[name.length - 1], directory, fileConfiguration);
                            this.maps.add(gameMap);
                            getLogger().info("[MAP LOADER]: map " + gameMap.getName() + " loaded !");
                        }
                    }

                }
            }
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

    public void destroyWorlds() {
        if(this.world != null){
            File file = this.world.getWorldFolder();
            Bukkit.unloadWorld(this.world,false);
            Utils.deleteWorldFiles(file);
        }
    }

    public void startGame(GameType gameType) {
        if(gameStatus == GameStatus.WAITING || gameStatus == GameStatus.ENDING){
            for(GameMap gameMap : this.maps){

            }
        }
    }

    public void startGame(GameMap gameMap) {
        if(gameStatus == GameStatus.WAITING || gameStatus == GameStatus.ENDING){

        }
    }


    public ArrayList<GameMap> getPlayedGameMap() {
        return playedGameMap;
    }

    public ArrayList<GameType> getPlayedGameType() {
        return playedGameType;
    }
}
