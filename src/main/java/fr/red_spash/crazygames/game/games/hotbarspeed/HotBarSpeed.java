package fr.red_spash.crazygames.game.games.hotbarspeed;

import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class HotBarSpeed extends Game {

    private final ArrayList<HotBarPreset> hotBarPresets;
    private final HashMap<UUID,Integer> presetReached;
    public static final int MAX_HOTBAR = 20;



    public HotBarSpeed() {
        super(GameType.HOTBAR_SPEED);

        this.presetReached = new HashMap<>();
        this.hotBarPresets = new ArrayList<>();
        this.hotBarPresets.add(new HotBarPreset(Material.LIME_CONCRETE,Material.RED_CONCRETE));
        this.hotBarPresets.add(new HotBarPreset(Material.LAVA_BUCKET,Material.WATER_BUCKET,Material.BRAIN_CORAL,Material.SEAGRASS,Material.KELP,Material.WET_SPONGE,Material.BUBBLE_CORAL_BLOCK,Material.CHERRY_BOAT,Material.COD,Material.TROPICAL_FISH));
        this.hotBarPresets.add(new HotBarPreset(Material.IRON_CHESTPLATE,Material.FISHING_ROD,Material.DIAMOND_HOE,Material.GOLDEN_SHOVEL,Material.IRON_AXE,Material.NETHERITE_SWORD,Material.STONE_SHOVEL,Material.DIAMOND_AXE,Material.NETHERITE_PICKAXE));
        this.hotBarPresets.add(new HotBarPreset(Material.GREEN_WOOL,Material.ACACIA_LEAVES,Material.AZALEA_LEAVES,Material.BIRCH_LEAVES,Material.JUNGLE_LEAVES,Material.MANGROVE_LEAVES,Material.DARK_OAK_LEAVES,Material.SPRUCE_LEAVES,Material.OAK_LEAVES));
        this.hotBarPresets.add(new HotBarPreset(Material.GRASS_BLOCK,Material.NETHERRACK,Material.NETHER_QUARTZ_ORE,Material.PIGLIN_HEAD,Material.BASALT,Material.BLACKSTONE,Material.NETHER_BRICK,Material.CRIMSON_STEM,Material.NETHER_GOLD_ORE));
        this.hotBarPresets.add(new HotBarPreset(Material.WHITE_CONCRETE_POWDER,Material.WHITE_WOOL,Material.BLACK_WOOL,Material.BLUE_WOOL,Material.BROWN_WOOL,Material.CYAN_WOOL,Material.GRAY_WOOL,Material.BLUE_WOOL,Material.GREEN_WOOL,Material.LIME_WOOL,Material.PINK_WOOL,Material.YELLOW_WOOL,Material.RED_WOOL));
        this.hotBarPresets.add(new HotBarPreset(Material.RED_WOOL,Material.LIME_WOOL));
        this.hotBarPresets.add(new HotBarPreset(Material.MAP,Material.PAPER));
        this.hotBarPresets.add(new HotBarPreset(Material.DIRT,Material.GRASS_BLOCK));
        this.hotBarPresets.add(new HotBarPreset(Material.STONE,Material.DEEPSLATE));
        this.hotBarPresets.add(new HotBarPreset(Material.STONE,Material.COAL_ORE,Material.IRON_ORE,Material.GOLD_ORE,Material.DIAMOND_ORE,Material.COPPER_ORE,Material.EMERALD_ORE,Material.LAPIS_ORE,Material.REDSTONE_ORE));
        this.hotBarPresets.add(new HotBarPreset(Material.GRASS,Material.ACACIA_SAPLING,Material.BIRCH_SAPLING,Material.CHERRY_SAPLING,Material.SPRUCE_SAPLING, Material.OAK_SAPLING,Material.DARK_OAK_SAPLING, Material.JUNGLE_SAPLING));
        this.hotBarPresets.add(new HotBarPreset(Material.PURPLE_WOOL,Material.AMETHYST_BLOCK,Material.AMETHYST_CLUSTER,Material.AMETHYST_SHARD, Material.BUDDING_AMETHYST,Material.LARGE_AMETHYST_BUD,Material.MEDIUM_AMETHYST_BUD,Material.SMALL_AMETHYST_BUD));
        this.hotBarPresets.add(new HotBarPreset(Material.DISC_FRAGMENT_5,Material.COCOA_BEANS,Material.BEETROOT_SEEDS,Material.MELON_SEEDS,Material.PUMPKIN_SEEDS,Material.WHEAT_SEEDS,Material.TORCHFLOWER_SEEDS));
        this.hotBarPresets.add(new HotBarPreset(Material.TARGET,Material.CHEST,Material.CHEST_MINECART,Material.ENDER_CHEST, Material.TRAPPED_CHEST,Material.FURNACE,Material.DROPPER,Material.DISPENSER,Material.BLAST_FURNACE,Material.SMOKER));
        this.hotBarPresets.add(new HotBarPreset(Material.RED_DYE,Material.REDSTONE,Material.REDSTONE_BLOCK,Material.PISTON,Material.DROPPER,Material.REPEATER,Material.REDSTONE_TORCH,Material.REDSTONE_LAMP,Material.TARGET,Material.BAMBOO_BUTTON,Material.POWERED_RAIL,Material.OBSERVER,Material.HOPPER));
        this.hotBarPresets.add(new HotBarPreset(Material.CYAN_CONCRETE_POWDER,Material.PRISMARINE,Material.PRISMARINE_SLAB,Material.PRISMARINE_STAIRS,Material.PRISMARINE_WALL,Material.PRISMARINE_BRICKS,Material.PRISMARINE_BRICK_SLAB,Material.PRISMARINE_BRICK_STAIRS,Material.DARK_PRISMARINE,Material.DARK_PRISMARINE_SLAB,Material.DARK_PRISMARINE_STAIRS));
        this.hotBarPresets.add(new HotBarPreset(Material.HONEY_BLOCK,Material.SEA_LANTERN,Material.OCHRE_FROGLIGHT,Material.PEARLESCENT_FROGLIGHT,Material.VERDANT_FROGLIGHT,Material.SHROOMLIGHT,Material.GLOWSTONE,Material.LANTERN,Material.SOUL_LANTERN,Material.TORCH,Material.SOUL_TORCH,Material.END_ROD));
        this.hotBarPresets.add(new HotBarPreset(Material.MOSS_BLOCK,Material.FARMLAND,Material.ROOTED_DIRT,Material.COARSE_DIRT,Material.DIRT,Material.DIRT_PATH,Material.MYCELIUM,Material.GRASS_BLOCK,Material.PODZOL));
        this.hotBarPresets.add(new HotBarPreset(Material.WHITE_CONCRETE_POWDER,Material.QUARTZ_SLAB,Material.QUARTZ_STAIRS,Material.QUARTZ_BLOCK,Material.SMOOTH_QUARTZ,Material.SMOOTH_QUARTZ_SLAB,Material.SMOOTH_QUARTZ_STAIRS,Material.QUARTZ_PILLAR,Material.QUARTZ_BRICKS,Material.CHISELED_QUARTZ_BLOCK));
        this.hotBarPresets.add(new HotBarPreset(Material.MINECART,Material.COMMAND_BLOCK,Material.CHAIN_COMMAND_BLOCK,Material.REPEATING_COMMAND_BLOCK,Material.JIGSAW,Material.STRUCTURE_BLOCK,Material.STRUCTURE_VOID,Material.BARRIER,Material.LIGHT,Material.DEBUG_STICK,Material.COMMAND_BLOCK_MINECART));
        this.hotBarPresets.add(new HotBarPreset(Material.WHITE_SHULKER_BOX,Material.WHITE_GLAZED_TERRACOTTA,Material.LIGHT_GRAY_GLAZED_TERRACOTTA,Material.GRAY_GLAZED_TERRACOTTA,Material.BLACK_GLAZED_TERRACOTTA,Material.BROWN_GLAZED_TERRACOTTA,Material.RED_GLAZED_TERRACOTTA,Material.ORANGE_GLAZED_TERRACOTTA,Material.YELLOW_GLAZED_TERRACOTTA,Material.LIME_GLAZED_TERRACOTTA,Material.GREEN_GLAZED_TERRACOTTA,Material.CYAN_GLAZED_TERRACOTTA,Material.BLUE_GLAZED_TERRACOTTA,Material.LIGHT_BLUE_GLAZED_TERRACOTTA,Material.PURPLE_GLAZED_TERRACOTTA,Material.PINK_GLAZED_TERRACOTTA,Material.MAGENTA_GLAZED_TERRACOTTA));
        this.hotBarPresets.add(new HotBarPreset(Material.RED_DYE,Material.LIME_DYE));
        this.hotBarPresets.add(new HotBarPreset(Material.LIME_DYE,Material.RED_DYE));
        this.hotBarPresets.add(new HotBarPreset(Material.SPRUCE_PLANKS,Material.OAK_PLANKS));
        this.hotBarPresets.add(new HotBarPreset(Material.DARK_OAK_LOG,Material.SPRUCE_LOG));
        this.hotBarPresets.add(new HotBarPreset(Material.ITEM_FRAME,Material.GLOW_ITEM_FRAME));
        this.hotBarPresets.add(new HotBarPreset(Material.SUSPICIOUS_GRAVEL,Material.GRAVEL));
        this.hotBarPresets.add(new HotBarPreset(Material.SUSPICIOUS_SAND,Material.SAND));
        this.hotBarPresets.add(new HotBarPreset(Material.GLASS_BOTTLE,Material.DRAGON_BREATH));
        this.hotBarPresets.add(new HotBarPreset(Material.SPLASH_POTION,Material.POTION));
        this.hotBarPresets.add(new HotBarPreset(Material.DETECTOR_RAIL,Material.RAIL));
        this.hotBarPresets.add(new HotBarPreset(Material.SOUL_TORCH,Material.TORCH));
        this.hotBarPresets.add(new HotBarPreset(Material.BAMBOO_CHEST_RAFT,Material.OAK_CHEST_BOAT));
        this.hotBarPresets.add(new HotBarPreset(Material.ENCHANTED_GOLDEN_APPLE,Material.GOLDEN_APPLE));
        this.hotBarPresets.add(new HotBarPreset(Material.DAMAGED_ANVIL,Material.ANVIL));
    }

    @Override
    public void preStart(){
        ArrayList<Location> otherSpawns = new ArrayList<>(super.getGameMap().getOtherLocations());
        if(otherSpawns.size() >= super.getGameManager().getPlayerManager().getAlivePlayerData().size()){
            for(Player p : Bukkit.getOnlinePlayers()){
                PlayerData playerData = super.gameManager.getPlayerData(p.getUniqueId());
                if(!playerData.isDead()){
                    p.teleport(otherSpawns.remove(0));
                    p.setWalkSpeed(0);
                }
            }
        }

        Collections.shuffle(this.hotBarPresets);
        super.registerListener(new HotBarSpeedListener(this,this.hotBarPresets));
    }


    public Integer getPresetReached(UUID uuid) {
        return presetReached.get(uuid);
    }

    @Override
    public void startGame() {
        for(Player p : Bukkit.getOnlinePlayers()){
            PlayerData playerData = super.gameManager.getPlayerData(p.getUniqueId());
            if(!playerData.isDead()){
                this.setNextPreset(p);
            }
        }
        super.gameManager.getGameInteractions()
                .setMoveItemInventory(false);
    }

    public void setNextPreset(Player p) {
        if(!presetReached.containsKey(p.getUniqueId())){
            presetReached.put(p.getUniqueId(),-1);
        }
        presetReached.put(p.getUniqueId(),presetReached.get(p.getUniqueId())+1);
        this.giveActualPreset(p);
    }

    public void giveActualPreset(Player p) {
        p.getInventory().clear();
        HotBarPreset hotBarPreset = this.hotBarPresets.get(this.presetReached.get(p.getUniqueId()));

        hotBarPreset.giveRandomItems(p);
    }

    @Override
    public List<String> updateScoreboard(Player p) {
        int amount = 0;
        if(presetReached.containsKey(p.getUniqueId())){
            amount = this.presetReached.get(p.getUniqueId());
        }
        return List.of("Complétés: "+amount+"/"+HotBarSpeed.MAX_HOTBAR);
    }
}
