package Config;

import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.ConfigObject;
import world.bentobox.bentobox.api.configuration.StoreAt;

import java.util.List;
import java.util.UUID;

public class ConfigProcessor {


    @StoreAt(filename="config.yml") // Explicitly call out what name this should have.
    public static class Config implements ConfigObject {
        @ConfigEntry(path = "debug")
        private int DEBUG_LEVEL;

        public int getDEBUG_LEVEL() {
            return DEBUG_LEVEL;
        }

        public void setDEBUG_LEVEL(int DEBUG_LEVEL) {
            this.DEBUG_LEVEL = DEBUG_LEVEL;
        }

    }

    @StoreAt(filename="registered_user.yml") // Explicitly call out what name this should have.
    public static class RegisteredUser implements ConfigObject {
        @ConfigEntry(path = "registered")
        private List<UUID> registered_user;

        public void setRegistered_user(List<UUID> registered_user) {
            this.registered_user = registered_user;
        }

        public List<UUID> getRegistered_user() {
            return registered_user;
        }
    }


}
