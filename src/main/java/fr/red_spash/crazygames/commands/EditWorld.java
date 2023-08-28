package fr.red_spash.crazygames.commands;

import fr.red_spash.crazygames.Main;
import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.MapManager;
import fr.red_spash.crazygames.map.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EditWorld implements CommandExecutor, TabCompleter {
    private final GameManager gameManager;
    private final ArrayList<World> editingWorld;

    public EditWorld(GameManager gameManager) {
        this.gameManager = gameManager;
        this.editingWorld = new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))return true;
        if(strings.length == 0){
            commandSender.sendMessage("§c/editworld <nom du monde>");
            return true;
        };

        Player p = (Player) commandSender;

        ArrayList<String> completer = new ArrayList<>();
        MapManager manager = this.gameManager.getMapManager();
        for(GameMap gameMap : manager.getMaps()){
            completer.add(gameMap.getFile().getName());
        }

        for(File file : manager.getInvalidMaps()){
            completer.add(file.getName());
        }
        String name = "";
        for(String ss : strings){
            name += ss+" ";
        }
        name = name.substring(0,name.length()-1);
        if(!completer.contains(name)){
            commandSender.sendMessage("§cCarte introuvable !");
            return true;
        }

        for(World world : editingWorld){
            if(world.getName().equalsIgnoreCase(name)){
                p.sendMessage("§cImpossible de charger un monde avec le nom '"+name+"' car un monde a déjà ce nom !");
                p.teleport(world.getSpawnLocation());
                return true;
            }
        }

        File mapsFolder = new File(Main.getInstance().getDataFolder(), "maps/"+name);
        String pathName = name;
        Path path2 = Paths.get(pathName);

        Utils.copyDirectory(mapsFolder.getPath().toString(), path2.toString());

        World world = Bukkit.createWorld(new WorldCreator(name));
        if(world == null){
            world = Bukkit.getWorld(name);
        }
        p.teleport(world.getSpawnLocation());
        p.setGameMode(GameMode.CREATIVE);
        p.setFlying(true);
        editingWorld.add(world);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        ArrayList<String> completer = new ArrayList<>();
        MapManager manager = this.gameManager.getMapManager();
        for(GameMap gameMap : manager.getMaps()){
            completer.add(gameMap.getFile().getName());
        }

        for(File file : manager.getInvalidMaps()){
            completer.add(file.getName());
        }

        return completer;
    }

    public ArrayList<World> getEditingWorld() {
        return this.editingWorld;
    }

    public void removeEditingWorld(World world) {
        this.editingWorld.remove(world);
    }
}
