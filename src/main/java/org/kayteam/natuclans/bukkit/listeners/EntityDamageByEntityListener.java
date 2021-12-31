package org.kayteam.natuclans.bukkit.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.kayteam.natuclans.NatuClans;

public class EntityDamageByEntityListener implements Listener {

    private final NatuClans PLUGIN;

    public EntityDamageByEntityListener(NatuClans PLUGIN) {
        this.PLUGIN = PLUGIN;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getEntity().getType() == EntityType.PLAYER){
            if(event.getDamager().getType() == EntityType.PLAYER){

            }
        }
    }
}
