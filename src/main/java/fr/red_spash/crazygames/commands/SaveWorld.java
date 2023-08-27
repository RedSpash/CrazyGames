package fr.red_spash.crazygames.commands;

import fr.red_spash.crazygames.Main;
import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

public class SaveWorld implements CommandExecutor {


    private final EditWorld editWorld;
    private final ArrayList<UUID> waitingReSend;

    public SaveWorld(EditWorld editWorld) {
        this.editWorld = editWorld;
        this.waitingReSend = new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player p){

            if(strings.length != 1){
                p.sendMessage("§c/saveworld <nom du monde>");
                return true;
            }

            if(!this.editWorld.getEditingWorld().contains(p.getWorld())){
                p.sendMessage("§cImpossible de sauvegarder un monde n'étant pas en mode modification !");
                return true;
            }

            World world = p.getWorld();
            File file = world.getWorldFolder();
            String name = strings[0];

            if(!this.waitingReSend.contains(p.getUniqueId())){
                File configurationFile = new File(file.getPath(),"config.yml");
                p.sendMessage(configurationFile+"");
                if(configurationFile.exists()){
                    FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(configurationFile);

                    GameType gameType = null;

                    try {
                        gameType = GameType.valueOf(fileConfiguration.getString("gametype"));
                    }catch (Exception e){
                        p.sendMessage("§cImpossible de continuer, vous n'avez modifié le type du jeu !");
                        return true;
                    }

                    int checkpointNumber = 0;
                    if (fileConfiguration.isSet("checkpoints")){
                        checkpointNumber = fileConfiguration.getConfigurationSection("checkpoints").getKeys(false).size();
                    }
                    boolean spawn = fileConfiguration.isSet("spawnlocation");

                    p.sendMessage("§aVotre mini jeu contient les informations suivantes:\nType du jeu: "+gameType+"\nNombre de checkpoint: "+checkpointNumber+"\nContient un spawn: "+spawn);
                    this.waitingReSend.add(p.getUniqueId());
                }else{
                    p.sendMessage("§cOn dirait bien que tu n'as pas configuré le point de spawn, le type du jeu...\nFait §c§l/edittools§r§c pour récuperer les outils.");
                }
                return true;
            }

            this.waitingReSend.remove(p.getUniqueId());
            File path = new File(Main.getInstance().getDataFolder(), "maps/"+name);

            try {
                p.sendMessage("§aDéchargement du monde...");
                Utils.teleportPlayersAndRemoveWorld(world,true);
                p.sendMessage("§aMonde déchargé !");
                p.sendMessage("§aSauvegarde en cours du monde ...");
                Utils.copyDirectory(file.getPath(),path.toString());
                p.teleport(Main.SPAWN);
                p.sendMessage("§aMonde sauvegardé correctement !");
                p.sendMessage("§aSuppresion des fichiers...");
                Utils.deleteWorldFiles(file);
                p.sendMessage("§aFichiers supprimé corectement !");
                p.sendMessage("§a§lMonde sauvegardé sous le nom de '"+name+"'");
                this.editWorld.removeEditingWorld(world);
            }catch (Exception e){
                p.sendMessage("§cImpossible de sauvegarder correctement le monde ! §7("+e.getMessage()+")");
            }
            return true;
        }
        return false;
    }
}
