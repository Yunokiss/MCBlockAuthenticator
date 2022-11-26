package Authenticator;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.eclipse.jdt.annotation.NonNull;
import world.bentobox.bentobox.BentoBox;

import java.util.UUID;

public class FirstLoginRegistrar extends world.bentobox.bentobox.listeners.JoinLeaveListener {

    @Override
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent){
        Player player = playerJoinEvent.getPlayer();
        UUID uniqueId = player.getUniqueId();

    }

    public FirstLoginRegistrar(@NonNull BentoBox plugin) {
        super(plugin);

    }
}
