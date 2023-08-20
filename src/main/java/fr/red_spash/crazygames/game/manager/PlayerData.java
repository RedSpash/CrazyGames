package fr.red_spash.crazygames.game.manager;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private boolean isDead;
    private boolean isQualified;
    private int point;


    public PlayerData(UUID uuid){
        this.uuid = uuid;
        this.isDead = false;
        this.isQualified = false;
        this.point = 0;
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

    public void setQualified(boolean qualified) {
        isQualified = qualified;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
