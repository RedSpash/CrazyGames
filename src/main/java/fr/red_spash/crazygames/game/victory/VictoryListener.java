package fr.red_spash.crazygames.game.victory;

import fr.red_spash.crazygames.game.manager.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class VictoryListener implements Listener {
    private final Victory victory;

    public VictoryListener(Victory victory) {
        this.victory = victory;
    }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(victory.getGameMap().getWorld() == p.getWorld()){
            if(victory.getLooserLocation().getY()-1 >= p.getLocation().getY()){
                PlayerData playerData = this.victory.getGameManager().getPlayerData(p.getUniqueId());
                if(playerData.isDead() || playerData.isEliminated()){
                    p.teleport(this.victory.getLooserLocation());
                }else{
                    p.teleport(this.victory.getWinnerLocation());
                }
            }
        }
    }

}
