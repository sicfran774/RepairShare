package io.sicfran.repairShare;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.sicfran.repairShare.listeners.OnExpChange;
import io.sicfran.repairShare.listeners.SelfRepair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public final class RepairShare extends JavaPlugin {

    public static final String VERSION = "1.0";
    private final List<UUID> ignoredPlayers = new ArrayList<>();
    public double XP_MULTIPLIER;
    public boolean ALLOW_SELF_REPAIR;
    public int REPAIR_COST;
    public int SELF_REPAIR_AMOUNT;

    @Override
    public void onEnable() {
        //create config
        saveDefaultConfig();
        //get missing keys due to update
        getConfig().options().copyDefaults(true);
        updateConfig();

        registerListeners(
                new OnExpChange(this),
                new SelfRepair(this)
        );

        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("repair")
                .then(Commands.literal("toggle")
                        .requires(ctx -> ctx.getSender().hasPermission("repair.toggle"))
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            Entity executor = ctx.getSource().getExecutor();

                            if(sender == executor && executor instanceof Player player){
                                UUID playerId = player.getUniqueId();
                                if(ignoredPlayers.contains(playerId)){
                                    ignoredPlayers.remove(playerId);
                                    executor.sendPlainMessage("You have now enabled RepairShare.");
                                } else {
                                    ignoredPlayers.add(player.getUniqueId());
                                    executor.sendPlainMessage("You have now disabled RepairShare.");
                                }
                            } else {
                                sender.sendPlainMessage("You must be a player to run this command.");
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("reload")
                        .requires(ctx -> ctx.getSender().hasPermission("repair.reload"))
                        .executes(this::reloadConfigCommand)
                );
        this.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS, commands ->
                        commands.registrar().register(root.build())
        );

        this.getLogger().info("RepairShare v" + VERSION + " successfully enabled!");

        checkIfNewVersion();
    }

    private int reloadConfigCommand(CommandContext<CommandSourceStack> ctx){
        CommandSender sender = ctx.getSource().getSender();
        updateConfig();
        sender.sendPlainMessage("RepairShare config reloaded.");
        return Command.SINGLE_SUCCESS;
    }

    private void updateConfig(){
        reloadConfig();

        XP_MULTIPLIER = getConfig().getDouble("xp_multiplier");
        ALLOW_SELF_REPAIR = getConfig().getBoolean("allow_self_repair");
        REPAIR_COST = getConfig().getInt("self_repair_cost");
        SELF_REPAIR_AMOUNT = getConfig().getInt("self_repair_amount");
    }

    private void registerListeners(Listener... listeners){
        PluginManager pm = Bukkit.getPluginManager();
        for(Listener listener : listeners){
            pm.registerEvents(listener, this);
        }
    }

    private void checkIfNewVersion(){
        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://hangar.papermc.io/api/v1/projects/sicfran/RepairShare/versions"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            // Find the first occurrence of "name":"<version>"
            int nameIndex = body.indexOf("\"name\":\"");
            if (nameIndex == -1) return;

            int start = nameIndex + 8; // length of "name":" is 8
            int end = body.indexOf("\"", start);
            String latestVersion = body.substring(start, end);

            if(!VERSION.equals(latestVersion)){
                getLogger().info("New version available! v" + latestVersion);
                getLogger().info("Download here: ");
            }
        } catch (Exception e){
            getLogger().info("Failed to check if there is a new version. Please check " +
                    "https://hangar.papermc.io/api/v1/projects/sicfran/RepairShare/versions" +
                    " for updates.");
        }
    }

    @Override
    public void onDisable() {
        this.getLogger().info("RepairShare " + VERSION + " has been disabled.");
    }

    public List<UUID> getIgnoredPlayers() {
        return ignoredPlayers;
    }
}
