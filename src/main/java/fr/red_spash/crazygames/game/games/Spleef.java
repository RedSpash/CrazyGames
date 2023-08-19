package fr.red_spash.crazygames.game.games;


import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class Spleef extends Game {


    public Spleef(GameManager gameManager) {
        super("Spleef","Éliminez en creusant pour faire tomber vos adversaires dans Spleef !","Spleef vous plonge dans une frénésie de creusage, où vous devez éliminer habilement vos rivaux en les faisant chuter de la plateforme. Affinez votre stratégie pour demeurer le dernier en pied dans ce jeu de survie compétitif sur fond de glace et de neige.", GameType.SPLEEF, gameManager);
    }


    @Override
    public void initializePlayers() {
        for(Player p : Bukkit.getOnlinePlayers()){
            p.teleport(super.getGameMap().getSpawnLocation());
        }
    }

    @Override
    public void startGame() {

    }
}
