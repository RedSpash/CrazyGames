package fr.red_spash.crazygames.map;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import fr.red_spash.crazygames.world.WorldManager;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GameMap implements Cloneable{
    private final WorldManager worldManager;
    private Location spawnLocation;
    private final String name;
    private final File file;
    private final GameType gameType;
    private final FileConfiguration fileConfiguration;
    private final ArrayList<CheckPoint> checkPoints;
    private World world;
    private ArrayList<Location> otherLocations;
    private boolean isVictoryMap;

    public GameMap(String name, File file, FileConfiguration fileConfiguration, WorldManager worldManager) {
        this.name = name;
        this.file = file;
        this.fileConfiguration = fileConfiguration;
        this.worldManager = worldManager;
        this.checkPoints = new ArrayList<>();
        this.otherLocations = new ArrayList<>();

        String gameTypeString = this.fileConfiguration.getString("gametype","").toUpperCase();
        if(gameTypeString.equalsIgnoreCase("victory")){
            this.gameType = null;
        }else{
            this.gameType = GameType.valueOf(gameTypeString);
        }
        this.isVictoryMap = this.gameType == null;
    }

    private void loadMapData() {
        this.otherLocations.clear();
        this.checkPoints.clear();

        this.spawnLocation = this.getConfigurationLocation("spawnlocation");

        if(this.fileConfiguration.isSet("checkpoints")){
            this.fileConfiguration.getConfigurationSection("checkpoints").getKeys(false).forEach(checkpointId ->{
                this.checkPoints.add(new CheckPoint(this.fileConfiguration,"checkpoints",checkpointId,this.world));
            });
        }

        if(this.fileConfiguration.isSet("spawns")){
            this.fileConfiguration.getConfigurationSection("spawns").getKeys(false).forEach(spawnId ->{
                this.otherLocations.add(this.getConfigurationLocation("spawns."+spawnId));
            });
        }
    }

    private Location getConfigurationLocation(String path) {
        Location location = this.fileConfiguration.getLocation(path,null);
        if(location == null){
            location = new Location(
                    this.world,
                    this.fileConfiguration.getDouble(path+".x",0.0),
                    this.fileConfiguration.getDouble(path+".y",101.5),
                    this.fileConfiguration.getDouble(path+".z",0.0),
                    this.fileConfiguration.getInt(path+".yaw",0),
                    this.fileConfiguration.getInt(path+".pitch",0)
            );
        }
        return location;

    }

    public String getName() {
        return this.name;
    }

    public File getFile() {
        return this.file;
    }

     public void deleteWorld(){
        this.deleteWorld(this.world);
    }

    public void deleteWorld(World world){
        if(world != null){
            this.worldManager.teleportPlayersAndRemoveWorld(world,false);
            this.worldManager.deleteWorldFiles(world.getWorldFolder());
        }else{
            Bukkit.broadcastMessage("§cMonde introuvable !");
        }

    }

    public boolean loadWorld(){
        Path path = Paths.get(this.file.getPath());
        String pathName =System.currentTimeMillis()+"-"+this.name;
        Path path2 = Paths.get(pathName);

        Utils.copyDirectory(path.toString(), path2.toString());

        this.world = Bukkit.createWorld(new WorldCreator(pathName));
        if(world == null){
            world = Bukkit.getWorld(pathName);
        }else{
            world.setGameRule(GameRule.DO_MOB_SPAWNING,false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false);
            world.setGameRule(GameRule.DO_FIRE_TICK,false);
            world.setGameRule(GameRule.DO_VINES_SPREAD,false);
        }

        try{
            this.loadMapData();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;

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
        return spawnLocation.clone();
    }

    public World getWorld() {
        return this.world;
    }

    public List<CheckPoint> getCheckPoints() {
        return this.checkPoints;
    }

    public boolean hasCheckpoint(){
        return !this.checkPoints.isEmpty();
    }

    public List<Location> getOtherLocations() {
        return this.otherLocations;
    }

    @Override
    public GameMap clone() {
        try {
            GameMap clone = (GameMap) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public boolean isVictoryMap() {
        return isVictoryMap;
    }
}
