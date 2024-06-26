package fr.red_spash.crazygames.game.games.spleef;


import fr.red_spash.crazygames.game.bonus.BonusTask;
import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class Spleef extends Game {

    private final ArrayList<Block> availableBlocks;

    public Spleef() {
        super(GameType.SPLEEF);
        this.availableBlocks = new ArrayList<>();
    }

    @Override
    public void initializePlayers(){
        this.availableBlocks.addAll(super.getBlockPlatform(Material.SNOW_BLOCK));
        this.availableBlocks.addAll(super.getBlockPlatform(Material.CLAY));
        super.registerListener(new SpleefListener(this));
        super.initializePlayers();
    }



    @Override
    public void startGame() {

        super.registerTask(
                new BonusTask(
                        this.availableBlocks,
                        List.of(new ItemStack(Material.SNOW_BLOCK,5)),
                        20,
                        1,
                        20),
                10,10
        );

        for(Player p :Bukkit.getOnlinePlayers()){
            PlayerData playerData = this.gameManager.getPlayerData(p.getUniqueId());
            if(!playerData.isDead()){
                ItemStack itemStack = new ItemStack(Material.GOLDEN_SHOVEL);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setUnbreakable(true);
                itemMeta.addEnchant(Enchantment.DIG_SPEED,5,false);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                itemStack.setItemMeta(itemMeta);
                p.getInventory().setItem(0,itemStack);
                p.getInventory().setHeldItemSlot(0);
            }
        }

        this.gameManager.getGameInteractions()
                .setShootProjectile(true)
                .addAllowedToBeBreak(Material.CLAY,Material.SNOW_BLOCK)
                .addAllowedToBePlaced(Material.SNOW_BLOCK)
                .blockLoot(false)
                .setDeathUnderSpawn(5)
                .setProjectileDamage(true);
    }

    @Override
    public List<String> updateScoreboard(Player p) {
        return List.of("Blocks restants: "+this.availableBlocks.size());
    }

    public List<Block> getAvailableBlocks() {
        return availableBlocks;
    }
}
