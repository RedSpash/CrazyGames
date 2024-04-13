package fr.red_spash.crazygames.game.games.bridgeit;

import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.map.GameMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BridgeItListener implements Listener {
    private final BridgeIt bridgeIt;

    public BridgeItListener(BridgeIt bridgeIt) {
        this.bridgeIt = bridgeIt;
    }

    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent e){
        if(!this.bridgeIt.getGameManager().isInWorld(e.getPlayer().getWorld()))return;
        Game game = this.bridgeIt.getGameManager().getActualGame();
        if(game != null ){
            GameMap gameMap = game.getGameMap();
            if(gameMap != null &&
                    gameMap.getSpawnLocation().distance(e.getBlock().getLocation()) <= 5){
                e.setCancelled(true);
                e.getPlayer().sendMessage("§cVous êtes trop prôche du point de réapparition !");
            }
        }
    }
}
