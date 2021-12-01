package org.kayteam.natuclans.clan;

import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ClanManager {

    private final NatuClans PLUGIN;

    public ClanManager(NatuClans plugin) {
        this.PLUGIN = plugin;
    }

    private final HashMap<String, Clan> clans = new HashMap<>();

    public Collection<Clan> getClanList() {
        return clans.values();
    }

    public void loadClan(String clanName){
        Yaml clanFile = new Yaml(PLUGIN, "clans", clanName);
    }

    public Clan getClan(String clanName){
        return clans.get(clanName);
    }

    public HashMap<String, Clan> getClansMap(){
        return clans;
    }

    public boolean isClan(String clanName){
        return clans.containsKey(clanName);
    }

    public void loadAllClans(){
        for(File clanFile : Yaml.getFolderFiles(PLUGIN.getDataFolder()+"/clans")){
            loadClan(clanFile.getName().replaceAll(".yml", ""));
        }
    }
}
