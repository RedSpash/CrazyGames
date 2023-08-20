package fr.red_spash.crazygames.game.games;


import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class Spleef extends Game {


    public Spleef(GameType gameType) {
        super(gameType);
    }



    @Override
    public void startGame() {
        for(Player p :Bukkit.getOnlinePlayers()){
            PlayerData playerData = this.gameManager.getPlayerData(p.getUniqueId());
            if(!playerData.isDead()){
                ItemStack itemStack = new ItemStack(Material.GOLDEN_SHOVEL);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setUnbreakable(true);
                itemMeta.addEnchant(Enchantment.DIG_SPEED,5,false);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                itemStack.setItemMeta(itemMeta);
                p.getInventory().setItem(4,itemStack);
                p.getInventory().setHeldItemSlot(4);
            }
        }

        this.gameManager.getGameInteractions().addAllowedToBeBreak(Material.CLAY,Material.SNOW_BLOCK);
    }
}
