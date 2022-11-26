package Authenticator;

import ConfigAndData.DataReader;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.eclipse.jdt.annotation.NonNull;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.IslandsManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static Core.Core.*;

public class FirstLoginRegistrar extends world.bentobox.bentobox.listeners.JoinLeaveListener {

    public static Map<Player, String> registering = new HashMap<>();
    @Override
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent){
        Player player = playerJoinEvent.getPlayer();
        UUID uniqueId = player.getUniqueId();
        if(registered_user.getRegistered_user().contains(uniqueId)) return;

        float walkSpeed = player.getWalkSpeed();
        player.setWalkSpeed(0);
        DataReader.DataTreeNode node = DataReader.DataTreeNode.origin_node;
        registering.put(player,"!");
        DataReader.Data data = null;
        DataReader.DataTreeNode dataTreeNode = null;
        try {
            DataReader.DataTreeNode ask = node.ask(player);
            dataTreeNode = ask.getFather();
            data = ask.getData();
        } catch (InterruptedException e) {
            player.setWalkSpeed(walkSpeed);
            player.kickPlayer("请正确完成注册！");
        }
        if(data == null){
            player.setWalkSpeed(walkSpeed);
            player.kickPlayer("请正确完成注册！");
            return;
        }
        register_data.put(player.getUniqueId(),data);
        registering.remove(player);
        registered_user.getRegistered_user().add(uniqueId);
        registered_user.getRegistered_user_data().put(uniqueId,data.getUuid());
        player.setWalkSpeed(walkSpeed);
        player.sendMessage(ChatColor.GREEN + "成功注册！");
        String name = dataTreeNode.getName();

        IslandsManager islands = ta.getIslands();
        if (islands.nameExists(default_world,name)) {
            for (Island island : islands.getIslands(default_world)) {
                if(Objects.equals(island.getName(), name)){
                    islands.deleteIsland(Objects.requireNonNull(islands.getIsland(default_world, User.getInstance(player))),true,null);
                    island.addMember(uniqueId);
                    player.teleport(Objects.requireNonNull(island.getSpawnPoint(island.getWorld().getEnvironment())));
                }
            }
        } else {
            CompletableFuture<Boolean> future = ta.getIslandsManager().homeTeleportAsync(default_world, player, true);
            future.complete(true);
            Island island = islands.getIsland(default_world, User.getInstance(player));
            assert island != null;
            island.setName(name);
        }
    }

    public FirstLoginRegistrar(@NonNull BentoBox plugin) {
        super(plugin);
    }
}
