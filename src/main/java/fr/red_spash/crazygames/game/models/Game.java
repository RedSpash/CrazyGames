package fr.red_spash.crazygames.game.models;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.map.GameMap;
import org.bukkit.World;

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

    protected void loadMap(){
        List<GameMap> gameMaps = new ArrayList<>(this.getGameManager().getMaps());
        gameMaps.removeIf(gameMap -> gameMap.getGameType() != this.gameType);

        this.gameMap = gameMaps.get(Utils.random_number(0,gameMaps.size()-1));

        World world = this.gameMap.loadWorld();
        this.gameManager.setWorld(world);
    }
    protected abstract void startGame();

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
