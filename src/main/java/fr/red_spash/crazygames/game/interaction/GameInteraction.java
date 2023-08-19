package fr.red_spash.crazygames.game.interaction;

import fr.red_spash.crazygames.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.ArrayList;

public class GameInteraction {

    private final InteractionListener interactionListener;
    private boolean explosion = false;
    private boolean vehicleBreak = false;
    private boolean entityMount = false;
    private boolean bucketInteract = false;
    private boolean shootProjectile = false;
    private boolean pvp = false;
    private boolean pve = false;
    private ArrayList<Material> allowedToBeBreak = new ArrayList<>();
    private ArrayList<Material> allowedToBePlaced = new ArrayList<>();

    public GameInteraction(Main main) {
        this.interactionListener = new InteractionListener(this);
        Bukkit.getPluginManager().registerEvents(this.interactionListener,main);
    }

    public GameInteraction setExplosion(boolean explosion) {
        this.explosion = explosion;
        return this;
    }

    public GameInteraction setVehicleBreak(boolean vehicleBreak) {
        this.vehicleBreak = vehicleBreak;
        return this;
    }

    public GameInteraction setEntityMount(boolean entityMount) {
        this.entityMount = entityMount;
        return this;
    }

    public GameInteraction setBucketInteract(boolean bucketInteract) {
        this.bucketInteract = bucketInteract;
        return this;
    }

    public GameInteraction setShootProjectile(boolean shootProjectile) {
        this.shootProjectile = shootProjectile;
        return this;
    }

    public GameInteraction setPvp(boolean pvp) {
        this.pvp = pvp;
        return this;
    }

    public GameInteraction setPve(boolean pve) {
        this.pve = pve;
        return this;
    }

    public void clearAllowedToBeBreak(){
        this.allowedToBeBreak.clear();
    }

    public void clearAllowedToBePlaced(){
        this.allowedToBePlaced.clear();
    }

    public GameInteraction addAllowedToBeBreak(Material material) {
        this.allowedToBeBreak.add(material);
        return this;
    }

    public GameInteraction addAllowedToBePlaced(Material material) {
        this.allowedToBePlaced.add(material);
        return this;
    }

    public boolean isExplosion() {
        return explosion;
    }

    public boolean isVehicleBreak() {
        return vehicleBreak;
    }

    public boolean isEntityMount() {
        return entityMount;
    }

    public boolean isBucketInteract() {
        return bucketInteract;
    }

    public boolean isShootProjectile() {
        return shootProjectile;
    }

    public boolean isPvp() {
        return pvp;
    }

    public boolean isPve() {
        return pve;
    }

    public ArrayList<Material> getAllowedToBeBreak() {
        return allowedToBeBreak;
    }

    public ArrayList<Material> getAllowedToBePlaced() {
        return allowedToBePlaced;
    }
}
