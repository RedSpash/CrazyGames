package fr.red_spash.crazygames.game.games.blastvillage;

import fr.red_spash.crazygames.game.games.spleef.SpleefListener;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class BlastVillage extends Game {

    public BlastVillage() {
        super(GameType.BLAST_VILLAGE);
        super.initializeTask(new BlastVillageTask(this),0,10);
    }

    @Override
    public void initializePlayers() {
        this.gameManager.getGameInteractions().setAllowInteraction(true);
        super.initializePlayers();
    }

    @Override
    public void startGame() {
        for(Player p : Bukkit.getOnlinePlayers()){
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
            p.setHealth(40);
        }
        super.gameManager.getGameInteractions()
                .setExplosion(true)
                .setPve(true)
                .setPlayerRegen(false)
                .setTeleportUnderBlock(30)
                .setShootProjectile(true)
                .setBlockLootItem(false)
                .setAllowInteraction(true)
                .addAllowedToBeBreak(Material.FIRE);
    }
}
