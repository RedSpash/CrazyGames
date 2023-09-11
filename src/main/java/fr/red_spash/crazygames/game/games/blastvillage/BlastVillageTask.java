package fr.red_spash.crazygames.game.games.blastvillage;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.manager.GameStatus;
import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;

public class BlastVillageTask implements Runnable {

    private final BlastVillage blastVillage;

    private int time =0;

    public BlastVillageTask(BlastVillage blastVillage) {
        this.blastVillage = blastVillage;
    }

    @Override
    public void run() {
        if(blastVillage.getGameStatus() == GameStatus.PLAYING){
            time++;
            if(time %2 == 0){
                for(int i = 1; i<= getAmountOfFireBall(); i++){
                    this.spawnFireBall(this.getFireBallDamage());
                }
            }
        }
    }

    public int getAmountOfFireBall(){
        return (time/2)/60+1;
    }

    public float getFireBallDamage() {
        return (((time/2)/60)+3);
    }

    private void spawnFireBall(float power) {
        int x = Utils.randomNumber(-24,24);
        int z = Utils.randomNumber(-24,24);

        Location location = this.blastVillage.getGameMap().getSpawnLocation().add(x,50,z);
        Fireball fireball = location.getWorld().spawn(location, Fireball.class);
        fireball.setBounce(true);
        fireball.setInvulnerable(true);
        fireball.setPersistent(true);
        fireball.setDirection(new Vector(0,-1,0));
        fireball.setVelocity(new Vector(0,-1,0));
        fireball.setYield(power);
        fireball.setIsIncendiary(true);
    }
}
