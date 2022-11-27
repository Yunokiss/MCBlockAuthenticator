package ConfigAndData;

import lombok.Getter;
import lombok.Setter;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.ConfigObject;
import world.bentobox.bentobox.api.configuration.StoreAt;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import Core.*;

import static Core.Core.Debug.sendDetail;

public class ConfigProcessor {

    @Getter
    @Setter
    @StoreAt(filename="BA_config.yml") // Explicitly call out what name this should have.
    public static class Config implements ConfigObject {
        @ConfigEntry(path = "debug")
        private int DEBUG_LEVEL = 4;

        @ConfigEntry(path = "data-path")
        private String data_path = Core.ta.getDataFolder().getAbsolutePath() + File.separator + "data.xlsx";

        @ConfigEntry(path = "confirmation")
        private String confirmation;

        @ConfigEntry(path = "default_world")
        private String default_world_uuid = "bskyblock_world";

    }

    @Getter
    @Setter
    @StoreAt(filename="registered_user.yml") // Explicitly call out what name this should have.
    public static class RegisteredUser implements ConfigObject {
        @ConfigEntry(path = "registered")
        private List<UUID> registered_user;

        @ConfigEntry(path = "registered-users")
        private Map<UUID, UUID> registered_user_data;

    }


}
