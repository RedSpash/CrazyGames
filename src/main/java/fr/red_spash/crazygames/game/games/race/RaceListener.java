package fr.red_spash.crazygames.game.games.race;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.PlayerData;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class RaceListener implements Listener {

    private final GameManager gameManager;
    private final HashMap<UUID,Long> cooldown;

    public RaceListener(GameManager gameManager){
        this.gameManager = gameManager;
        this.cooldown = new HashMap<>();
    }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e) {
        if(e.getTo().getBlock() == e.getFrom().getBlock())return;
        if(!this.gameManager.isInWorld(e.getPlayer().getWorld()))return;
        Player p = e.getPlayer();

        if(cooldown.containsKey(p.getUniqueId())){
            if(cooldown.get(p.getUniqueId()) > System.currentTimeMillis()){
                return;
            }
        }

        if (p.getLocation().add(0, -1, 0).getBlock().getType() == Material.RED_GLAZED_TERRACOTTA) {
            p.setVelocity(p.getLocation().getDirection().setY(0.5).multiply(1.5));
            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT,1,1);
            cooldown.put(p.getUniqueId(),System.currentTimeMillis()+1000*1);
        }
    }



}
