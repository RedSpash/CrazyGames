package fr.red_spash.crazygames;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    private Utils(){

    }

    public static int randomNumber(Integer min, Integer max){
        max = max +1;
        return (int) (Math.random()*(max-min)) + min;
    }

    public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) {
        try {
            Files.walk(Paths.get(sourceDirectoryLocation))
                    .forEach(source -> {
                        Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                                .substring(sourceDirectoryLocation.length()));
                        try {
                            if(!destination.toString().contains("uid.dat")){
                                Files.copy(source, destination);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteWorldFiles(File worldFolder) {
        if (worldFolder.exists() && worldFolder.isDirectory()) {
            File[] files = worldFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteWorldFiles(file);
                    }
                    try {
                        Files.delete(file.toPath());
                    } catch (IOException e) {
                        Bukkit.getLogger().warning("Impossible de supprimer le fichier: "+file.getName()+"!");
                    }
                }
            }
            try {
                Files.delete(worldFolder.toPath());
            } catch (IOException e) {
                Bukkit.getLogger().warning("Impossible de supprimer le monde: "+worldFolder.getName()+"!");
            }
        }
    }

    public static void teleportPlayersAndRemoveWorld(World world,boolean save) {
        for(Player p : world.getPlayers()){
            p.teleport(Main.SPAWN);
            p.sendMessage("§cLe monde vient d'être détruit! Vous êtes désormais au spawn.");
        }
        Bukkit.unloadWorld(world,save);
    }

    public static ItemStack createFastItemStack(Material material, String name, String... lore) {
        return createFastItemStack(material,name,0,new ArrayList<>(Arrays.asList(lore)));
    }

    public static ItemStack createFastItemStack(Material material, String name,int customModelData, String... lore) {
        return createFastItemStack(material,name,customModelData,new ArrayList<>(Arrays.asList(lore)));
    }

    public static ItemStack createFastItemStack(Material material, String name, List<String> lore) {
        return createFastItemStack(material,name,0,lore);
    }

    public static ItemStack createFastItemStack(Material material, String name,int customModelData, List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        if(customModelData != 0){
            itemMeta.setCustomModelData(customModelData);
        }
        if(!lore.isEmpty()){
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static List<String> splitSentance(String shortDescription) {
        List<String> dividedList = new ArrayList<>();
        String[] words = shortDescription.split("\\s+"); // Diviser la phrase en mots

        int currentIndex = 0;
        while (currentIndex < words.length) {
            int endIndex = Math.min(currentIndex + 7, words.length); // Index de fin pour chaque sous-liste
            String[] subArray = Arrays.copyOfRange(words, currentIndex, endIndex); // Sous-liste de 7 mots ou moins
            dividedList.add("§7"+String.join(" ", subArray)); // Ajouter la sous-liste à la liste principale
            currentIndex = endIndex; // Passer à la prochaine sous-liste
        }

        return dividedList;
    }

    public static String onXString(int size, String s) {
        StringBuilder sBuilder = new StringBuilder(s);
        while (sBuilder.length() < size){
            sBuilder.insert(0, "0");
        }
        return sBuilder.toString();
    }

    public static double round(Double x, Double value){
        return Math.round(x*value)/value;
    }
    public static double round(Double x){
        return round(x,10.0);
    }

    public static double round(Float x, Double value){
        return Math.round(x*value)/value;
    }
    public static double round(Float x){
        return round(x,10.0);
    }
}
