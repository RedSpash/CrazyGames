package fr.red_spash.crazygames.map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

public class CheckPoint {

    private static final String LOCATION_MIN_PATH = "locationMin";
    private static final String LOCATION_MAX_PATH = "locationMax";
    public final Location locationMin;
    public final Location locationMax;
    private final int id;

    public CheckPoint(int id, Location locationA, Location locationB) {
        this.id = id;

        final double minX;
        final double maxX;
        if(locationA.getX() < locationB.getX()){
            minX = locationA.getX();
            maxX = locationB.getX();
        }else{
            minX = locationB.getX();
            maxX = locationA.getX();
        }

        final double minY;
        final double maxY;
        if(locationA.getY() < locationB.getY()){
            minY = locationA.getY();
            maxY = locationB.getY();
        }else{
            minY = locationB.getY();
            maxY = locationA.getY();
        }

        final double minZ;
        final double maxZ;
        if(locationA.getZ() < locationB.getZ()){
            minZ = locationA.getZ();
            maxZ = locationB.getZ();
        }else{
            minZ = locationB.getZ();
            maxZ = locationA.getZ();
        }

        this.locationMin = new Location(locationA.getWorld(),minX,minY,minZ);
        this.locationMax = new Location(locationB.getWorld(),maxX,maxY,maxZ);
    }

    public CheckPoint(FileConfiguration fileConfiguration, String path, String id){
        this(fileConfiguration,path,id,null);
    }

    public CheckPoint(FileConfiguration fileConfiguration, String path, String id, World world) {
        double minX = fileConfiguration.getDouble(path+"."+id+ "." + LOCATION_MIN_PATH + ".x");
        double minY = fileConfiguration.getDouble(path+"."+id+ "." + LOCATION_MIN_PATH + ".y");
        double minZ = fileConfiguration.getDouble(path+"."+id+ "." + LOCATION_MIN_PATH + ".z");

        double maxX = fileConfiguration.getDouble(path+"."+id+ "." + LOCATION_MAX_PATH + ".x");
        double maxY = fileConfiguration.getDouble(path+"."+id+ "." + LOCATION_MAX_PATH + ".y");
        double maxZ = fileConfiguration.getDouble(path+"."+id+ "." + LOCATION_MAX_PATH + ".z");

        this.locationMin = new Location(world,minX,minY,minZ);
        this.locationMax = new Location(world,maxX,maxY,maxZ);
        this.id = Integer.parseInt(id);
    }

    public void setWorld(World world){
        this.locationMin.setWorld(world);
        this.locationMax.setWorld(world);
    }

    public Location getMiddle(){
        Vector vector = this.locationMax.clone().subtract(this.locationMin).toVector();
        return this.locationMin.clone().add(vector);
    }

    public boolean isInside(Location location){
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return this.locationMin.getX() <= x && this.locationMax.getX() >= x
                && this.locationMin.getY() <= y && this.locationMax.getY() >= y
                && this.locationMin.getZ() <= z && this.locationMax.getZ() >= z;
    }

    public int getId() {
        return id;
    }


    public void save(FileConfiguration fileConfiguration) {
        fileConfiguration.set("checkpoints."+this.id+ "." + LOCATION_MIN_PATH + ".x",this.locationMin.getX());
        fileConfiguration.set("checkpoints."+this.id+ "." + LOCATION_MIN_PATH + ".y",this.locationMin.getY());
        fileConfiguration.set("checkpoints."+this.id+ "." + LOCATION_MIN_PATH + ".z",this.locationMin.getZ());

        fileConfiguration.set("checkpoints."+this.id+ "." + LOCATION_MAX_PATH + ".x",this.locationMax.getX());
        fileConfiguration.set("checkpoints."+this.id+ "." + LOCATION_MAX_PATH + ".y",this.locationMax.getY());
        fileConfiguration.set("checkpoints."+this.id+ "." + LOCATION_MAX_PATH + ".z",this.locationMax.getZ());
    }
}
