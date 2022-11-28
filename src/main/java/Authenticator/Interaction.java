package Authenticator;

import ConfigAndData.DataReader;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static Core.Core.Debug.sendData;
import static Core.Core.Debug.sendDetail;

public class Interaction implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void interact(AsyncPlayerChatEvent event){
        if (FirstLoginRegistrar.registering.containsKey(event.getPlayer())) {
            String ans = event.getMessage();
            Player player = event.getPlayer();
            event.setCancelled(true);
            DataReader.DataTreeNode now = FirstLoginRegistrar.registering.get(player);
            try {
                //noinspection ResultOfMethodCallIgnored
                UUID.fromString(now.getChildren().get(0).getName());
            } catch (Exception e) {
                for (DataReader.DataTreeNode child : now.getChildren()) {
                    if(Objects.equals(child.getName(), ans)){
                        try {
                            child.ask(player);
                        } catch (InterruptedException e1) {
                            sendData(e1.getLocalizedMessage());
                            sendDetail("询问被打断。");
                        }
                        return;
                    }
                }
                return;
            }
            for (DataReader.DataTreeNode child : now.getChildren()) {
                if(Objects.equals(child.getChildren().get(0).getName(), ans)){
                    try {
                        child.getChildren().get(0).ask(player);
                    } catch (InterruptedException e) {
                        sendData(e.getLocalizedMessage());
                        sendDetail("询问被打断。");
                    }
                    return;
                }
            }

        }
    }

}
