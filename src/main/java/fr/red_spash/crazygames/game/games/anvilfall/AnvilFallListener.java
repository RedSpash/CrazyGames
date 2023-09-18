package fr.red_spash.crazygames.game.games.anvilfall;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class AnvilFallListener implements Listener {
    private final GameManager gameManager;

    public AnvilFallListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

}
