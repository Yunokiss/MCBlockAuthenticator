package ConfigAndData;

import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.ConfigObject;
import world.bentobox.bentobox.api.configuration.StoreAt;

import java.io.File;
import java.util.List;
import java.util.UUID;

import Core.*;

public class ConfigProcessor {


    @StoreAt(filename="config.yml") // Explicitly call out what name this should have.
    public static class Config implements ConfigObject {
        @ConfigEntry(path = "debug")
        private int DEBUG_LEVEL;

        @ConfigEntry(path = "data-path")
        private String data_path = Core.ta.getDataFolder().getAbsolutePath() + File.separator + "data.xlsx";

        @ConfigEntry(path = "confirmation")
        private String confirmation;


        public int getDEBUG_LEVEL() {
            return DEBUG_LEVEL;
        }

        public void setDEBUG_LEVEL(int DEBUG_LEVEL) {
            this.DEBUG_LEVEL = DEBUG_LEVEL;
        }

        public String getData_path() {
            return data_path;
        }

        public void setData_path(String data_path) {
            this.data_path = data_path;
        }

        public String getConfirmation() {
            return confirmation;
        }

        public void setConfirmation(String confirmation) {
            this.confirmation = confirmation;
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
