package fr.red_spash.crazygames.commands;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartGame implements CommandExecutor {
    private final GameManager gameManager;

    public StartGame(GameManager gameManager) {
        this.gameManager = gameManager;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender.isOp()){
            if(strings.length > 0){
                GameType gameType = GameType.valueOf(strings[0].toUpperCase());
                this.gameManager.fillPlayerData();
                commandSender.sendMessage("§aLancement d'un nouveau jeu !");
                this.gameManager.startAGameType(gameType);
                return true;
            }
            this.gameManager.fillPlayerData();
            commandSender.sendMessage("§aLancement d'un nouveau jeu !");
            this.gameManager.rollGames();
        }
        return false;
    }
}
