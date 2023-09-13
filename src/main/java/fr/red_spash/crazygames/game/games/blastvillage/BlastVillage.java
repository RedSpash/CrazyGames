package fr.red_spash.crazygames.game.games.blastvillage;

import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class BlastVillage extends Game {

    private final BlastVillageTask blastVillageTask;

    public BlastVillage() {
        super(GameType.BLAST_VILLAGE);
        this.blastVillageTask = new BlastVillageTask(this);
        super.initializeTask(blastVillageTask,0,10);
    }

    @Override
    public void initializePlayers() {
        this.gameManager.getGameInteractions().setAllowInteraction(true);
        super.initializePlayers();
    }

    @Override
    public void startGame() {
        for(Player p : Bukkit.getOnlinePlayers()){
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
            p.setHealth(30);
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

    @Override
    public List<String> updateScoreboard() {
        if(this.blastVillageTask != null){
            return Arrays.asList("Fireballs: §c"+this.blastVillageTask.getAmountOfFireBall(),"Dégats: §c"+this.blastVillageTask.getFireBallDamage());
        }
        return super.updateScoreboard();
    }
}
