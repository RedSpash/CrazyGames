package fr.red_spash.crazygames.game.interaction;

import fr.red_spash.crazygames.Main;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.models.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameInteraction {

    private final InteractionListener interactionListener;
    private boolean explosion;
    private boolean vehicleBreak;
    private boolean entityMount;
    private boolean bucketInteract;
    private boolean shootProjectile;
    private boolean pvp;
    private boolean pve;
    private boolean foodChange;
    private double deathY;
    private ArrayList<Material> allowedToBeBreak;
    private ArrayList<Material> allowedToBePlaced;
    private boolean blockLoot;
    private boolean moveItemInventory;

    public GameInteraction(Main main, GameManager gameManager) {
        this.resetInteractions();
        this.interactionListener = new InteractionListener(this,gameManager);
        Bukkit.getPluginManager().registerEvents(this.interactionListener,main);
    }

    public void resetInteractions(){
        this.explosion = false;
        this.moveItemInventory = false;
        this.vehicleBreak = false;
        this.entityMount = false;
        this.bucketInteract = false;
        this.shootProjectile = false;
        this.pvp = false;
        this.pve = false;
        this.foodChange = false;
        this.deathY = -1;
        this.allowedToBeBreak = new ArrayList<>();
        this.allowedToBePlaced = new ArrayList<>();
        this.blockLoot = true;
    }

    public double getDeathY() {
        return deathY;
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

    public GameInteraction addAllowedToBeBreak(Material... material) {
        this.allowedToBeBreak.addAll(Arrays.asList(material));
        return this;
    }

    public GameInteraction addAllowedToBePlaced(Material material) {
        this.allowedToBePlaced.add(material);
        return this;
    }

    public GameInteraction setMoveItemInventory(boolean moveItemInventory) {
        this.moveItemInventory = moveItemInventory;
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

    public List<Material> getAllowedToBeBreak() {
        return allowedToBeBreak;
    }

    public List<Material> getAllowedToBePlaced() {
        return allowedToBePlaced;
    }

    public GameInteraction blockLoot(boolean b) {
        this.blockLoot = b;
        return this;
    }

    public GameInteraction setDeathY(double deathY) {
        this.deathY = deathY;
        return this;
    }

    public boolean isBlockLoot() {
        return this.blockLoot;
    }

    public boolean isFoodLevel() {
        return this.foodChange;
    }

    public boolean isMoveItemInventory() {
        return moveItemInventory;
    }

}
