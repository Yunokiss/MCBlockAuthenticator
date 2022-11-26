package Authenticator;

import org.eclipse.jdt.annotation.NonNull;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.listeners.JoinLeaveListener;

public class LoginTeleport extends JoinLeaveListener {
    /**
     * @param plugin - plugin object
     */
    public LoginTeleport(@NonNull BentoBox plugin) {
        super(plugin);
    }
}
