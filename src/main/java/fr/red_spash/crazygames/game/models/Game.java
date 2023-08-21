package fr.red_spash.crazygames.game.models;

import com.google.common.base.MoreObjects;
import fr.red_spash.crazygames.Main;
import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.error.IncompatibleGameType;
import fr.red_spash.crazygames.game.games.spleef.SpleefListener;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.GameStatus;
import fr.red_spash.crazygames.map.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Game implements Cloneable{

    protected final GameManager gameManager;
    private final GameType gameType;
    private GameMap gameMap;
    private GameStatus gameStatus;
    private final ArrayList<Listener> activeListeners;

    protected Game(GameType gameType) {
        this.activeListeners = new ArrayList<>();
        this.gameManager = Main.getInstance().getGameManager();
        this.gameType = gameType;
        this.gameStatus = GameStatus.WAITING;
    }

    public void loadMap(){
        this.loadMap(null);
    }

    public void loadMap(@Nullable GameMap forceGameMap){
        if(forceGameMap == null){
            List<GameMap> gameMaps = new ArrayList<>(this.getGameManager().getMaps());
            gameMaps.removeIf(map -> map.getGameType() != this.gameType);

            this.gameMap = gameMaps.get(Utils.randomNumber(0,gameMaps.size()-1));
        }else{
            if(forceGameMap.getGameType() == this.gameType){
                this.gameMap = forceGameMap;
            }else{
                new IncompatibleGameType("La carte '"+forceGameMap.getName()+"' n'est pas compatible avec le mode "+this.gameType+" !").printStackTrace();
                return;
            }

        }

        this.gameMap.loadWorld();
    }
    public void initializePlayers(){
        for(Player p : Bukkit.getOnlinePlayers()){
            p.teleport(this.gameMap.getSpawnLocation());
            p.sendTitle("§a§l"+this.gameType.getName(),"§9"+this.gameType.getShortDescription(),20,20*3,20);
            p.sendMessage("§2§l"+this.gameType.getName()+"\n§a"+this.gameType.getLongDescription());
        }
    }
    public abstract void startGame();

    public GameManager getGameManager() {
        return gameManager;
    }

    public GameType getGameType() {
        return gameType;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void unRegisterListeners(){
        for(Listener listener : this.activeListeners){
            HandlerList.unregisterAll(listener);
        }
        this.activeListeners.clear();
    }

    protected void initializeListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, Main.getInstance());

        this.activeListeners.add(listener);
    }
}
