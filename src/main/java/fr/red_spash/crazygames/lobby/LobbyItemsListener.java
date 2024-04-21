package fr.red_spash.crazygames.lobby;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.GameStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LobbyItemsListener implements Listener {

    private final GameManager gameManager;
    private final Lobby lobby;

    public LobbyItemsListener(GameManager gameManager, Lobby lobby) {
        this.gameManager = gameManager;
        this.lobby = lobby;
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent e){
        if(this.gameManager.getActualGameStatus() != GameStatus.LOBBY)return;
        if(e.getItem() == null)return;
        e.setCancelled(true);

        Player p = e.getPlayer();
        ItemStack itemStack = e.getItem();
        if(itemStack.isSimilar(Lobby.SHOW_GAMES_ITEM)){
            p.openInventory(this.lobby.getGamesInventory());

        }

    }
}
