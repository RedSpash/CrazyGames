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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private Location spawnLocation;
    private final String name;
    private final File file;
    private final GameType gameType;
    private final FileConfiguration fileConfiguration;
    private final ArrayList<CheckPoint> checkPoints;
    private World world;

    public GameMap(String name, File file,FileConfiguration fileConfiguration) {
        this.name = name;
        this.file = file;
        this.fileConfiguration = fileConfiguration;
        this.checkPoints = new ArrayList<>();

        this.gameType = GameType.valueOf(this.fileConfiguration.getString("gametype","").toUpperCase());
    }

    private void loadMapData() {
        this.spawnLocation = this.getConfigurationLocation("spawnlocation");

        if(this.fileConfiguration.isSet("checkpoints")){
            this.fileConfiguration.getConfigurationSection("checkpoints").getKeys(false).forEach(checkpointId ->{
                Location firstLocation = this.getConfigurationLocation("checkpoints."+checkpointId+".pointA");
                Location secondLocation = this.getConfigurationLocation("checkpoints."+checkpointId+".pointB");
                this.checkPoints.add(new CheckPoint(Integer.parseInt(checkpointId),firstLocation,secondLocation));
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
        if(this.getWorld() != null){
            Utils.teleportPlayersAndRemoveWorld(this.getWorld(),false);
            Utils.deleteWorldFiles(this.world.getWorldFolder());
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
        }
        try{
            this.loadMapData();
        }catch (Exception e){
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

}
