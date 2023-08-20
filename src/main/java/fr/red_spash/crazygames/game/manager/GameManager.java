package fr.red_spash.crazygames.game.manager;

import fr.red_spash.crazygames.Main;
import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.interaction.GameInteraction;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import fr.red_spash.crazygames.map.GameMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;

public class GameManager {

    private final HashMap<UUID,PlayerData> playerData = new HashMap<>();

    private final List<String> countDown = new ArrayList<>(Arrays.asList("❶","❷","❸","❹","❺","❻","❼","❽","❾","❿"));
    private final ArrayList<GameType> playedGameType = new ArrayList<>();
    private final ArrayList<GameMap> playedGameMap = new ArrayList<>();
    private final Main main;
    private GameInteraction gameInteraction;
    private Game actualGame;
    private final ArrayList<GameMap> maps = new ArrayList<>();
    private World world;
    private BukkitTask taskCountDown;

    public GameManager(Main main){
        this.main = main;
        this.gameInteraction = new GameInteraction(this.main);
        this.loadMaps();
    }

    private void loadMaps() {
        this.maps.clear();

        File mapsFolder = new File(this.main.getDataFolder(), "maps");

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

    public List<GameMap> getMaps() {
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
            Bukkit.getScheduler().runTaskLater(Main.getInstance(),()->Utils.deleteWorldFiles(file),2);
        }
    }

    public void startGame(GameType gameType) {
        GameStatus gameStatus = this.actualGame.getGameStatus();
        if(gameStatus == null)return;
        if(gameStatus != GameStatus.WAITING && gameStatus != GameStatus.ENDING)return;

        ArrayList<GameMap> mapsAvailable = new ArrayList<>();
        for(GameMap gameMap : this.maps){
            if(!this.playedGameMap.contains(gameMap) && gameMap.getGameType() == gameType){
                mapsAvailable.add(gameMap);
            }
        }
        if(mapsAvailable.isEmpty()) {
            this.playedGameMap.clear();
            Bukkit.broadcastMessage("Toutes les maps ont été jouées !");

            mapsAvailable = new ArrayList<>(this.maps);
            mapsAvailable.removeIf(gameMap -> gameMap.getGameType() != gameType);
        }
        GameMap gameMap = mapsAvailable.get(Utils.randomNumber(0,mapsAvailable.size()-1));
        startGame(gameMap);
    }

    public void startGame(GameMap gameMap) {
        GameStatus gameStatus = this.actualGame.getGameStatus();
        if(gameStatus == null)return;
        if(gameStatus != GameStatus.WAITING && gameStatus != GameStatus.ENDING)return;

        this.actualGame = gameMap.getGameType().createInstance();
        this.actualGame.loadMap();
        this.actualGame.initializePlayers();

        this.startCountdown();

    }

    private void startCountdown() {
        if(this.taskCountDown != null){
            this.taskCountDown.cancel();
        }
        this.taskCountDown = Bukkit.getScheduler().runTaskTimer(this.main, new Runnable() {
            int seconds = 15;
            @Override
            public void run() {

                if(seconds == 0){
                    for(Player p : Bukkit.getOnlinePlayers()){
                        p.sendTitle("§a§lC'est parti !","§a§lBonne chance !",0,20*3,10);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,2,2);
                    }
                    actualGame.startGame();
                    taskCountDown.cancel();
                    return;
                }

                if(seconds <= 5){
                    double ratio = (seconds-1)/9.0;
                    ChatColor chatColor = ChatColor.of(new Color((int) ((1-ratio)*255), (int) (ratio*255),0));
                    for(Player p : Bukkit.getOnlinePlayers()){
                        p.sendTitle(chatColor+countDown.get(seconds-1),"§eLancement de la partie...",0,20*3,10);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,2,0);
                    }
                }


                seconds--;
            }
        },20,20);
    }


    public List<GameMap> getPlayedGameMap() {
        return playedGameMap;
    }

    public List<GameType> getPlayedGameType() {
        return playedGameType;
    }

    public PlayerData getPlayerData(UUID uniqueId) {
        return playerData.get(uniqueId);
    }

    public void resetInteractions(){
        this.gameInteraction = new GameInteraction(this.main);
    }

    public GameInteraction getGameInteractions() {
        return this.gameInteraction;
    }
}
