package fr.red_spash.crazygames.scoreboard;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import fr.red_spash.crazygames.map.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ScoreboardTask implements Runnable{
    private final GameManager gameManager;

    public ScoreboardTask(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            PlayerData playerData = this.gameManager.getPlayerData(p.getUniqueId());
            RedScoreBoard board = playerData.getScoreboard();

            board.setLine(15,"§f");

            if(this.gameManager.getActualGame() != null){
                Game game = this.gameManager.getActualGame();
                GameType gameType = game.getGameType();

                if(gameType.isQualificationMode()){
                    board.setLine(14,"§fQualifiés: §a"+this.gameManager.getQualifiedPlayers().size()+"/"+(this.gameManager.getAlivePlayerData().size()-1));
                }else{
                    board.setLine(14,"§fÉliminés: §c"+this.gameManager.getEliminatedPlayer().size()+"/1");
                }
                board.setLine(13,"Jeu: §a"+gameType.getName());
            }else{
                board.setLine(14,"§cEn attente du");
                board.setLine(13,"§clancement de la partie.");
            }

            board.setLine(1,"§f");
            board.setLine(0,"§7Développé par Red_Spash");

        }
    }
}
