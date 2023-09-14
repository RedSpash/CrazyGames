package fr.red_spash.crazygames.game.manager;

import fr.red_spash.crazygames.map.CheckPoint;
import fr.red_spash.crazygames.scoreboard.RedScoreBoard;
import org.bukkit.Location;

import java.util.UUID;

public class PlayerData {

    private static final int DEFAULT_LIFE = 3;
    private final UUID uuid;
    private boolean isDead;
    private boolean isQualified;
    private int point;
    private CheckPoint lastCheckPoint;
    private RedScoreBoard redScoreboard;
    private boolean eliminated;
    private int life;
    private Location leftCheckPointCreation;
    private Location rightCheckPointCreation;


    public PlayerData(UUID uuid){
        this.uuid = uuid;
        this.isDead = false;
        this.life = PlayerData.DEFAULT_LIFE;
        this.isQualified = false;
        this.eliminated = false;
        this.point = 0;
        this.lastCheckPoint = null;
        this.redScoreboard = new RedScoreBoard(MessageManager.PREFIX);
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public boolean isQualified() {
        return isQualified;
    }

    public CheckPoint getLastCheckPoint() {
        return lastCheckPoint;
    }

    public void setQualified(boolean qualified) {
        isQualified = qualified;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public boolean canUnlockCheckPoint(CheckPoint checkPoint){
        return lastCheckPoint == null || checkPoint.getId() > this.lastCheckPoint.getId();
    }

    public void unlockCheckPoint(CheckPoint checkPoint) {
        this.lastCheckPoint = checkPoint;
    }

    public void resetGameData() {
        this.eliminated = false;
        this.isQualified = false;
        this.point = 0;
        this.lastCheckPoint = null;
    }

    public void reset(){
        this.resetGameData();
        this.isDead = false;
        this.life = PlayerData.DEFAULT_LIFE;
    }

    public RedScoreBoard getScoreboard() {
        return this.redScoreboard;
    }

    public boolean isEliminated() {
        return this.eliminated;
    }

    public void setEliminated(boolean value) {
        this.eliminated = value;
    }

    public int getLife() {
        return life-1;
    }

    public int getVisualLife(){
        return life;
    }

    public void loseLife() {
        this.life = this.life - 1;
    }

    public void setLife(int i) {
        this.life = i;
    }

    public int getDefaultLife() {
        return PlayerData.DEFAULT_LIFE;
    }

    public String getPlayerState() {
        String state = "§aVIVANT";
        if(this.isDead()){
            state = "§cMORT";
        } else if (this.getDefaultLife() > 0) {
            state = "§c"+this.getVisualLife()+" ❤";
        }
        return state;
    }

    public Location getLeftCheckPointCreation() {
        return leftCheckPointCreation;
    }

    public void setLeftCheckPointCreation(Location leftCheckPointCreation) {
        this.leftCheckPointCreation = leftCheckPointCreation;
    }

    public void setRightCheckPointCreation(Location rightCheckPointCreation) {
        this.rightCheckPointCreation = rightCheckPointCreation;
    }

    public Location getRightCheckPointCreation() {
        return rightCheckPointCreation;
    }
}
