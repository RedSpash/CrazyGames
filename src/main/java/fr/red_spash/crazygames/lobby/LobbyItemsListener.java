package fr.red_spash.crazygames.lobby;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.GameStatus;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class LobbyItemsListener implements Listener {

    private final GameManager gameManager;
    private final Lobby lobby;
    private final ArrayList<Block> temporaryBlocks;

    public LobbyItemsListener(GameManager gameManager, Lobby lobby) {
        this.gameManager = gameManager;
        this.lobby = lobby;
        this.temporaryBlocks = new ArrayList<>();
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent e){
        if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY)return;
        if(e.getItem() == null)return;

        Player p = e.getPlayer();
        ItemStack itemStack = e.getItem();
        if(itemStack.isSimilar(Lobby.SHOW_GAMES_ITEM)){
            e.setCancelled(true);
            p.openInventory(this.lobby.getGamesInventory());

        }
    }

    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent e){
        if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY)return;
        if(!e.getItemInHand().isSimilar(Lobby.PINK_WOOL_ITEM))return;

        if(!e.getBlockReplacedState().getType().equals(Material.AIR)){
            e.setCancelled(true);
            return;
        }

        Block block = e.getBlockPlaced();
        this.temporaryBlocks.add(block);

        Bukkit.getScheduler().runTaskLater(gameManager.getMain(), () -> {
            if(block.getType() == Lobby.PINK_WOOL_ITEM.getType()){
                block.setType(Material.AIR);
                this.temporaryBlocks.remove(block);
                block.getWorld().spawnParticle(Particle.CLOUD,block.getLocation().add(0.5,0.5,0.5),10,0.1,0.25,0.25,0.25);
                block.getWorld().playSound(block.getLocation(), Sound.ENTITY_CHICKEN_EGG,1,1);
                if(this.gameManager.getActualGameStatus() == GameStatus.LOBBY){
                    ItemStack itemStack = Lobby.PINK_WOOL_ITEM.clone();
                    itemStack.setAmount(1);
                    e.getPlayer().getInventory().addItem(itemStack);
                }
            }
        },20L*10);
    }

    public void clearBlocks() {
        for(Block block : this.temporaryBlocks){
            if(block.getType() == Lobby.PINK_WOOL_ITEM.getType()){
                block.setType(Material.AIR);
            }
        }
        this.temporaryBlocks.clear();
    }
}
