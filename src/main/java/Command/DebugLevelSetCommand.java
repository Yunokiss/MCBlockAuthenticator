package Command;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;

import java.util.List;
import java.util.Optional;

public class DebugLevelSetCommand extends CompositeCommand {

    protected DebugLevelSetCommand(Addon addon, String label, String... aliases) {
        super(addon, "ba", aliases);
    }

    protected DebugLevelSetCommand(String label, String... aliases) {
        super("ba", aliases);
    }

    protected DebugLevelSetCommand(CompositeCommand parent, String label, String... aliases) {
        super(parent, "ba", new String[]{"debug"});
    }

    protected DebugLevelSetCommand(Addon addon, CompositeCommand parent, String label, String... aliases) {
        super(addon, parent, "ba", aliases);
    }

    @Override
    public void setup() {
        this.setPermission("ba.debug");
        this.setOnlyPlayer(false);
        this.setParametersHelp("commands.ba.parameter");
        this.setDescription("commands.ba.debug.description");
    }

    @Override
    public boolean canExecute(User user, String label, List<String> args) {
        return super.canExecute(user, "ba", args);
    }

    @Override
    public boolean execute(User user, String s, List<String> list) {
        return false;
    }

    @Override
    public Optional<List<String>> tabComplete(User user, String alias, List<String> args) {
        return super.tabComplete(user, alias, args);
    }
}
