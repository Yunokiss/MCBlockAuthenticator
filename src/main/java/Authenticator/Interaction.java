package Authenticator;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class Interaction implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void interact(AsyncPlayerChatEvent event){
        if (FirstLoginRegistrar.registering.containsKey(event.getPlayer())) {
            FirstLoginRegistrar.registering.put(event.getPlayer(),event.getMessage());
            event.setCancelled(true);
        }
    }

}
