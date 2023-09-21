package fr.red_spash.crazygames.game.manager;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.models.GameType;
import fr.red_spash.crazygames.map.GameMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;

public class MapManager {

    private final GameManager gameManager;
    private ArrayList<File> invalidMaps = new ArrayList<>();
    private final ArrayList<GameMap> playedGameMap = new ArrayList<>();
    private final ArrayList<GameMap> maps = new ArrayList<>();
    private GameMap victoryMap;

    public MapManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.loadMaps();
    }

    private void loadMaps() {
        this.maps.clear();

        File mapsFolder = new File(this.gameManager.getMain().getDataFolder(), "maps");

        if (mapsFolder.exists() && mapsFolder.isDirectory()) {
            File[] mapFolders = mapsFolder.listFiles(File::isDirectory);

            if (mapFolders != null) {
                for (File mapFolder : mapFolders) {
                    ArrayList<File> directories = new ArrayList<>(Collections.singletonList(mapFolder));

                    for (File directory : directories) {
                        String[] name = directory.toString().split("\\\\");

                        if (name.length == 1) {
                            name = directory.toString().split("/");
                        }

                        File[] files = directory.listFiles((dir, fileName) -> fileName.equalsIgnoreCase("config.yml"));

                        if (files == null || files.length != 1) {
                            this.invalidateMap(directory);
                        } else {
                            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(files[0]);

                            if(!fileConfiguration.isSet("spawnlocation")){
                                this.invalidateMap(directory);
                                return;
                            }

                            GameMap gameMap = new GameMap(name[name.length - 1], directory, fileConfiguration);
                            if(gameMap.isVictoryMap()){
                                getLogger().info("[MAP LOADER]: Victory map '"+ gameMap.getName() + "' loaded !");
                                this.victoryMap = gameMap;
                            }else{
                                this.maps.add(gameMap);
                                getLogger().info("[MAP LOADER]: map " + gameMap.getName() + " loaded !");
                            }

                        }
                    }
                }
            }
        }
    }

    public GameMap getVictoryMap() {
        return victoryMap.clone();
    }

    private void invalidateMap(File directory) {
        getLogger().warning("La map suivante est invalide : " + directory.toString());
        this.invalidMaps.add(directory);
    }

    public List<GameMap> getMaps() {
        return maps;
    }

    public List<GameMap> getAvailableMaps(GameType gameType) {
        ArrayList<GameMap> mapsAvailable = new ArrayList<>();
        for(GameMap gameMap : this.maps){
            if(!this.playedGameMap.contains(gameMap) && gameMap.getGameType() == gameType){
                mapsAvailable.add(gameMap.clone());
            }
        }

        if(mapsAvailable.isEmpty() && !this.maps.isEmpty()){
            this.playedGameMap.clear();
            return getAvailableMaps(gameType);
        }

        return mapsAvailable;
    }

    public GameMap getRandomMap(GameType gameType) {
        ArrayList<GameMap> mapsAvailable = new ArrayList<>(this.getAvailableMaps(gameType));
        return mapsAvailable.get(Utils.randomNumber(0,mapsAvailable.size()-1)).clone();
    }

    public void setPlayed(GameMap gameMap) {
        this.playedGameMap.add(gameMap);
    }

    public List<File> getInvalidMaps() {
        return this.invalidMaps;
    }

    public void reset() {
        this.playedGameMap.clear();
    }
}
