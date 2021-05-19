package de.rexlmanu.bingo.shared.portal;

import com.google.common.collect.Maps;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;

import java.util.Map;

public class PortalListener implements Listener {

    private static final long PORTAL_ENTER_DELAY = 4000L;

    private Map<Entity, Long> portalEnteredTime;

    public PortalListener() {
        this.portalEnteredTime = Maps.newHashMap();
    }

    @EventHandler
    public void handle(EntityPortalEnterEvent event) {
        if (!this.portalEnteredTime.containsKey(event.getEntity())) {
            this.portalEnteredTime.put(event.getEntity(), System.currentTimeMillis());
            return;
        }

        Long enteredPortal = this.portalEnteredTime.get(event.getEntity());
        if ((System.currentTimeMillis() - enteredPortal) > PORTAL_ENTER_DELAY) {
            this.portalEnteredTime.remove(event.getEntity());
            System.out.println("User can now be teleportet.");
            System.out.println("User: " + event.getEntity().getName());
        }
    }

    @EventHandler
    public void handle(EntityPortalExitEvent event) {
        this.portalEnteredTime.remove(event.getEntity());
        System.out.println(event.getEntity().getName() + " left portal");
    }

}
