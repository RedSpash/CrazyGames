package fr.red_spash.crazygames.game.games.test;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.games.test.moving_blocks.MovingBlock;
import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.game.manager.PointManager;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Test extends Game {

    private final ArrayList<MovingBlock> movingBlocks =  new ArrayList<>();
    private final PointManager pointManager;

    public Test() {
        super(GameType.TEST);
        this.pointManager = this.getGameManager().getPointManager();
    }

    @Override
    public void preStart() {
        super.gameManager.getGameInteractions().setEntityMount(true);

        for(int x = -10; x <= 10; x ++){
            for(int z = -5; z <= 5; z ++){
                Location location = this.gameMap.getSpawnLocation().add(x,-1,z);
                location.getBlock().setType(Material.STONE);
            }
        }
        this.pointManager.setEliminatePlayers(false);
        this.pointManager.setUnite("vie","vies");
        for(PlayerData playerData : this.gameManager.getPlayerManager().getAlivePlayerData()){
            this.pointManager.addPoint(playerData.getUuid(),3);
        }
    }

    @Override
    public void startGame() {
        this.registerTask(new TestTask(this),1,1);
        super.gameManager.getGameInteractions()
                .setPvp(true)
                .setHitCooldown(5)
                .setDeathUnderSpawn(1);
    }

    public List<MovingBlock> getMovingBlocks() {
        return new ArrayList<>(this.movingBlocks);
    }

    public void spawnShulker() {
        Location loca = this.gameMap.getSpawnLocation().add(Utils.randomNumber(-11,11),0,Utils.randomNumber(-11,11)+50);
        this.spawnShulker(loca);
        this.spawnShulker(loca.add(0,1,0));
    }

    private void spawnShulker(Location loca) {
        MovingBlock movingBlock = new MovingBlock(Material.RED_CONCRETE,Utils.randomNumber(1,5)/10.0, DyeColor.values()[Utils.randomNumber(0,DyeColor.values().length-1)]);
        movingBlock.spawn(loca);
        this.movingBlocks.add(movingBlock);
    }

    public void removeMovingBlock(MovingBlock movingBlock) {
        this.movingBlocks.remove(movingBlock);
    }
}
