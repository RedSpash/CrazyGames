package fr.red_spash.crazygames.game.tasks;

import fr.red_spash.crazygames.game.manager.GameManager;
import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitTask;

public class GameTimer implements Runnable {

    private final int maxTime;
    private final GameManager gameManager;
    private int seconds = 0;

    public GameTimer(GameManager gameManager, int maxTime) {
        this.gameManager = gameManager;
        this.maxTime = maxTime;
    }

    @Override
    public void run() {
        seconds++;
        if(seconds >= maxTime){
            this.gameManager.stopGame();
        }
    }

    public int getTimeRemaining(){
        return this.maxTime - this.seconds;
    }
}
