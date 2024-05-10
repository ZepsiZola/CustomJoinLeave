package zepsizola.me.customjoinleave;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;

public class CustomJoinLeave extends JavaPlugin implements Listener {

    private boolean PAPIEnabled = false;

    @Override
    public void onEnable() {
        // Check if PlaceholderAPI is available
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PAPIEnabled = true;
            getLogger().info("PlaceholderAPI enabled.");
        } else {
            getLogger().info("PlaceholderAPI not found. PAPI support disabled.");
        }

        this.getServer().getPluginManager().registerEvents(this, this);

        this.saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String message = getConfig().getString("joinMessage", "<yellow>%player_name% <yellow>joined the game");
        if (PAPIEnabled) {
            message = PlaceholderAPI.setPlaceholders(event.getPlayer(), message);
        }

        Component joinMessage = MiniMessage.miniMessage().deserialize(message);
        event.joinMessage(joinMessage);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String message = getConfig().getString("leaveMessage", "<yellow>%player_name% <yellow>left the game");
        if (PAPIEnabled) {
            message = PlaceholderAPI.setPlaceholders(event.getPlayer(), message);
        }

        Component quitMessage = MiniMessage.miniMessage().deserialize(message);
        event.quitMessage(quitMessage);
    }
}