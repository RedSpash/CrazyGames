package fr.red_spash.crazygames.game.models;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.error.IncompatibleGameType;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.map.GameMap;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class Game {

    private final String name;
    private final String shortDescription;
    private final String description;
    protected final GameManager gameManager;
    private final GameType gameType;
    private GameMap gameMap;

    public Game(String name, String shortDescription, String description, GameType gameType, GameManager gameManager) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.description = description;
        this.gameManager = gameManager;
        this.gameType = gameType;
    }

    public void loadMap(){
        this.loadMap(null);
    }

    public void loadMap(@Nullable GameMap forceGameMap){
        if(forceGameMap == null){
            List<GameMap> gameMaps = new ArrayList<>(this.getGameManager().getMaps());
            gameMaps.removeIf(gameMap -> gameMap.getGameType() != this.gameType);

            this.gameMap = gameMaps.get(Utils.random_number(0,gameMaps.size()-1));
        }else{
            if(forceGameMap.getGameType() == this.gameType){
                this.gameMap = forceGameMap;
            }else{
                new IncompatibleGameType("La carte '"+forceGameMap.getName()+"' n'est pas compatible avec le mode "+this.gameType+" !").printStackTrace();
                return;
            }

        }

        World world = this.gameMap.loadWorld();
        this.gameManager.setWorld(world);
    }
    public abstract void initializePlayers();
    public abstract void startGame();

    public String getName() {
        return name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public GameType getGameType() {
        return gameType;
    }

    public GameMap getGameMap() {
        return gameMap;
    }
}
