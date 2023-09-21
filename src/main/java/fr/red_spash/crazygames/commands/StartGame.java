package fr.red_spash.crazygames.commands;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class StartGame implements CommandExecutor, TabCompleter {
    private final GameManager gameManager;

    public StartGame(GameManager gameManager) {
        this.gameManager = gameManager;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender.isOp()){
            this.gameManager.getPlayerManager().fillPlayerData();
            this.gameManager.getMapManager().reset();
            if(strings.length > 0){
                GameType gameType = GameType.valueOf(strings[0].toUpperCase());
                commandSender.sendMessage("§aLancement d'un nouveau jeu !");
                this.gameManager.startAGameType(gameType);
                return true;
            }
            commandSender.sendMessage("§aLancement d'un nouveau jeu !");
            this.gameManager.rollGames();
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        ArrayList<String> completer = new ArrayList<>();
        for(GameType gameType : GameType.values()){
            completer.add(gameType.toString());
        }
        return completer;
    }
}
