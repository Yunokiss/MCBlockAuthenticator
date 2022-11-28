package Authenticator;

import ConfigAndData.DataReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import java.util.function.Consumer;

import static ConfigAndData.DataReader.DataTreeNode.done;
import static Core.Core.*;
import static Core.Core.Debug.sendData;
import static Core.Core.Debug.sendDetail;

public class FirstLoginRegistrar extends world.bentobox.bentobox.listeners.JoinLeaveListener {

    public static Map<Player, DataReader.DataTreeNode> registering = new HashMap<>();
    @Override
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent){
        sendData("Authenticator.FirstLoginRegistrar.onPlayerJoin called");
        workAsync(playerJoinEvent);
        new Thread(() -> workAsync(playerJoinEvent)).start();
    }

    private void workAsync(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        UUID uniqueId = player.getUniqueId();
        if(registered_user.getRegistered_user().contains(uniqueId)) return;
        sendDetail(player.getName() + "start registering");
        player.setWalkSpeed(0);
        DataReader.DataTreeNode node = DataReader.DataTreeNode.origin_node;
        FirstLoginRegistrar.registering.put(player, DataReader.DataTreeNode.origin_node);
        try {
            done.put(player, ask -> {
                DataReader.Data data;
                data = ask.getData();
                if(data == null){
                    player.setWalkSpeed(defaultWalkSpeed);
                    player.kickPlayer("请正确完成注册！");
                    return;
                }
                register_data.put(player.getUniqueId(),data);
                registering.remove(player);
                registered_user.getRegistered_user().add(uniqueId);
                registered_user.getRegistered_user_data().put(uniqueId,data.getUuid());
                player.setWalkSpeed(defaultWalkSpeed);
                player.sendMessage(ChatColor.GREEN + "成功注册！");
                String name = ask.getName(config.getConfirmation());
                name = name==null ? "Your Block" : name;
                done.remove(player);
                IslandsManager islands = ta.getIslands();
                if (islands.nameExists(default_world,name)) {
                    for (Island island : islands.getIslands(default_world)) {
                        if(Objects.equals(island.getName(), name)){
                            try {
                                islands.deleteIsland(Objects.requireNonNull(islands.getIsland(default_world, User.getInstance(player))),true,null);
                            } catch (Exception ignored) {
                            }
                            island.addMember(uniqueId);
                        }
                    }
                } else {
                    CompletableFuture<Boolean> future = ta.getIslandsManager().homeTeleportAsync(default_world, player, true);
                    future.complete(true);
                    Island island = islands.getIsland(default_world, User.getInstance(player));
                    assert island != null;
                    island.setName(name);
                }
            });
            node.ask(player);
        } catch (InterruptedException e) {
            player.setWalkSpeed(defaultWalkSpeed);
            player.kickPlayer("请正确完成注册！");
        }

    }

    public FirstLoginRegistrar(@NonNull BentoBox plugin) {
        super(plugin);
    }
}
