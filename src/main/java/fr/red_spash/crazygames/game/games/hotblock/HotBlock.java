package fr.red_spash.crazygames.game.games.hotblock;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.bonus.BonusTask;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HotBlock extends Game {

    private ItemStack HOT_POTION;
    private final ArrayList<Block> blocks = new ArrayList<>();
    private HotBlockTask hotBlockTask;
    private int blockBefore;
    private HotBlockListener hotBlockListener;
    private final ArrayList<Material> materialState = new ArrayList<>(Arrays.asList(Material.WHITE_TERRACOTTA,Material.YELLOW_CONCRETE,Material.ORANGE_CONCRETE,Material.RED_CONCRETE,Material.AIR));


    public HotBlock() {
        super(GameType.HOT_BLOCK);
    }

    @Override
    public void initializePlayers() {
        this.blocks.addAll(super.getBlockPlatform(Material.WHITE_TERRACOTTA,materialState.get(0)));

        super.gameManager.getGameInteractions().setDeathUnderSpawn(2).setShootProjectile(true);
        super.initializePlayers();

        this.HOT_POTION = new ItemStack(Material.SPLASH_POTION);
        PotionMeta potionMeta = (PotionMeta) this.HOT_POTION.getItemMeta();
        potionMeta.setDisplayName("§cHot Potion");
        potionMeta.setLore(List.of("§7Permet de réchauffer les blocks."));
        potionMeta.setColor(Color.ORANGE);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.GLOWING,0,0,false,false,false),true);
        this.HOT_POTION.setItemMeta(potionMeta);
    }

    @Override
    public void startGame() {
        super.registerTask(
                new BonusTask(
                        this.blocks,
                        List.of(this.HOT_POTION),
                        20,
                        1,
                        20),
                10, 10
        );

        this.hotBlockListener = new HotBlockListener(this);
        this.hotBlockTask = new HotBlockTask(this);
        super.registerListener(this.hotBlockListener);
        super.registerTask(this.hotBlockTask,0,1);
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    @Override
    public List<String> updateScoreboard(Player p) {
        int removedBlock = 0;
        int blockNumber = 0;
        if(hotBlockTask != null){
            blockNumber = this.hotBlockTask.getBlocksNumber();
            int amount = this.blockBefore-blockNumber;
            if(amount > 0){
                removedBlock  = amount;
            }
            this.blockBefore = blockNumber;
        }
        return Collections.singletonList("Blocks: §a"+blockNumber+" §7(§c-"+ Utils.onXString(2,removedBlock+"")+"§7)");
    }

    public void changeBlock(Block block) {
        block.setType(materialState.get(materialState.indexOf(block.getType())+1));
        if(block.getType() == materialState.get(materialState.size()-1)){
            this.blocks.remove(block);
        }
    }
}
