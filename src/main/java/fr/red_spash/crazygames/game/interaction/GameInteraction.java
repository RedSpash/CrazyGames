package fr.red_spash.crazygames.game.interaction;

import fr.red_spash.crazygames.Main;
import fr.red_spash.crazygames.game.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GameInteraction {

    private Material blockWin;
    private boolean explosion;
    private boolean vehicleBreak;
    private boolean entityMount;
    private boolean bucketInteract;
    private boolean shootProjectile;
    private boolean pvp;
    private boolean pve;
    private boolean foodChange;
    private double deathUnderSpawn;
    private ArrayList<Material> allowedToBeBreak;
    private ArrayList<Material> allowedToBePlaced;
    private boolean blockLoot;
    private boolean moveItemInventory;
    private int teleportUnderBlock;
    private int maxBuildHeight;
    private boolean playerRegen;
    private boolean blockLootItem;
    private boolean allowInteraction;
    private HashMap<Material, PotionEffect> materialPotionEffectHashMap;
    private ArrayList<Material> killPlayerMaterials;
    private boolean dropItem;
    private double hitCooldown;

    public GameInteraction(Main main, GameManager gameManager) {
        this.resetInteractions();
        InteractionListener interactionListener = new InteractionListener(this, gameManager);
        Bukkit.getPluginManager().registerEvents(interactionListener,main);
    }

    public void resetInteractions(){
        this.materialPotionEffectHashMap = new HashMap<>();
        this.allowInteraction = false;
        this.blockLootItem = true;
        this.playerRegen = true;
        this.maxBuildHeight = -1;
        this.teleportUnderBlock = -1;
        this.blockWin = null;
        this.explosion = false;
        this.moveItemInventory = false;
        this.vehicleBreak = false;
        this.entityMount = false;
        this.bucketInteract = false;
        this.shootProjectile = false;
        this.pvp = false;
        this.pve = false;
        this.foodChange = false;
        this.deathUnderSpawn = 0;
        this.allowedToBeBreak = new ArrayList<>();
        this.allowedToBePlaced = new ArrayList<>();
        this.killPlayerMaterials = new ArrayList<>();
        this.blockLoot = true;
        this.hitCooldown = 0;
    }

    public GameInteraction addMaterialPotionEffectHashMap(Material material, PotionEffect potionEffect) {
        this.materialPotionEffectHashMap.put(material,potionEffect);
        return this;
    }

    public GameInteraction setHitCooldown(double hitCooldown) {
        this.hitCooldown = hitCooldown;
        return this;
    }

    public GameInteraction setMaxBuildHeight(int maxBuildHeight) {
        this.maxBuildHeight = maxBuildHeight;
        return this;
    }


    public GameInteraction addKillPlayerMaterials(Material... killPlayerMaterials) {
        this.killPlayerMaterials.addAll(List.of(killPlayerMaterials));
        return this;
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

    public GameInteraction setDeathUnderSpawn(double deathY) {
        this.deathUnderSpawn = deathY;
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

    public GameInteraction setBlockWin(Material blockWin) {
        this.blockWin = blockWin;
        return this;
    }


    public Material getBlockWin() {
        return blockWin;
    }

    public GameInteraction addAllowedToBePlaced(List<Material> list) {
        this.allowedToBePlaced.addAll(list);
        return this;
    }

    public GameInteraction addAllowedToBeBreak(List<Material> list) {
        this.allowedToBeBreak.addAll(list);
        return this;
    }

    public GameInteraction setTeleportUnderBlock(int teleportUnderBlock) {
        this.teleportUnderBlock = teleportUnderBlock;
        return this;
    }

    public int getTeleportUnderBlock() {
        return this.teleportUnderBlock;
    }

    public int getMaxBuildHeight() {
        return maxBuildHeight;
    }

    public boolean isPlayerRegen() {
        return this.playerRegen;
    }

    public GameInteraction setPlayerRegen(boolean playerRegen) {
        this.playerRegen = playerRegen;
        return this;
    }

    public GameInteraction setBlockLootItem(boolean b) {
        this.blockLootItem = b;
        return this;
    }

    public boolean isBlockLootItem() {
        return blockLootItem;
    }

    public GameInteraction setAllowInteraction(boolean b) {
        this.allowInteraction = b;
        return this;
    }

    public boolean isAllowInteraction() {
        return allowInteraction;
    }

    public double getHitCooldown() {
        return hitCooldown;
    }

    public ArrayList<Material> getKillPlayerMaterials() {
        return killPlayerMaterials;
    }

    public HashMap<Material, PotionEffect> getMaterialPotionEffectHashMap() {
        return materialPotionEffectHashMap;
    }

    public double getDeathUnderSpawn() {
        return deathUnderSpawn;
    }

}
