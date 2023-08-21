package fr.red_spash.crazygames.map;

import org.bukkit.Location;

public class CheckPoint {

    public final Location locationMin;
    public final Location locationMax;

    public CheckPoint(Location locationA, Location locationB) {
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

    public boolean isInside(Location location){
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return this.locationMin.getX() <= x && this.locationMax.getX() >= x
                && this.locationMin.getY() <= y && this.locationMax.getY() >= y
                && this.locationMin.getZ() <= z && this.locationMax.getZ() >= z;
    }

}
