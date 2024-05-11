package zepsizola.me.customjoinleave;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;

import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.AMPERSAND_CHAR;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.SECTION_CHAR;
import static org.bukkit.Bukkit.broadcast;

public class CustomJoinLeave extends JavaPlugin implements Listener, CommandExecutor {

    private boolean PAPIEnabled = false;
    private LuckPerms luckPerms;
    private boolean luckPermsEnabled = false;
    private final String configJoinMessage = getConfig().getString("joinMessage", "<yellow>%player_name% <yellow>joined the game");
    private final String configLeaveMessage = getConfig().getString("leaveMessage", "<yellow>%player_name% <yellow>left the game");

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
        if (getCommand("vanishjoin") != null) {
            getCommand("vanishjoin").setExecutor(this);
        } else {
            getLogger().warning("Could not register 'vanishjoin' command.");
        }
        if (getCommand("vanishleave") != null) {
            getCommand("vanishleave").setExecutor(this);
        } else {
            getLogger().warning("Could not register 'vanishleave' command.");
        }

        setupPAPI();
        setupLuckPerms();
    }

    private void setupLuckPerms() {
        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            luckPerms = LuckPermsProvider.get();
            luckPermsEnabled = true;
            getLogger().info("LuckPerms enabled.");
        } else {
            getLogger().info("LuckPerms not found. LuckPerms support disabled.");
        }
    }

    private void setupPAPI() {
        // Check if PlaceholderAPI is available
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PAPIEnabled = true;
            getLogger().info("PlaceholderAPI enabled.");
        }else{
            getLogger().info("PlaceholderAPI not found. PAPI support disabled.");}
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Component joinMessage = customMessage(configJoinMessage, player); //Creates the message as a component
        event.joinMessage(joinMessage);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Component quitMessage = customMessage(configLeaveMessage, player); //Creates the message as a component
        event.quitMessage(quitMessage);
    }

    private Component customMessage (String message, Player player){
        Component joinLeaveMessage = null;
        if (!(player.hasPermission("essentials.silentjoin") || player.hasPermission("essentials.silentquit"))){
            if (PAPIEnabled) { //Sets PAPI placeholders if PAPI is enabled
                message = PlaceholderAPI.setPlaceholders(player, message);}

            if (luckPermsEnabled) {//Set
                User user = luckPerms.getUserManager().getUser(player.getUniqueId());
                //if (user.getCachedData() != null){
                    String prefix_string = user.getCachedData().getMetaData().getPrefix(); //Gets namecolour prefix (might be null)
                    String suffix_string = user.getCachedData().getMetaData().getSuffix();// gets tag &7.exe (might be null)
                    prefix_string = prefix_string != null ? prefix_string.replace(SECTION_CHAR, AMPERSAND_CHAR) : ""; //Replaces § with &
                    suffix_string = suffix_string != null ? suffix_string.replace(SECTION_CHAR, AMPERSAND_CHAR) : ""; //Replaces § with &
                    //suffix_string = suffix_string.replace(SECTION_CHAR, AMPERSAND_CHAR);
                    getLogger().info("Prefix string: " + prefix_string);
                    getLogger().info("Suffix string: " + suffix_string);
                    getLogger().info("Conversion happening...");
                    prefix_string = MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix_string));
                    suffix_string = MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(suffix_string)); //Turns "&7.exe" into "<gray>.exe"
                    getLogger().info("Prefix string: " + prefix_string);
                    getLogger().info("Suffix string: " + suffix_string);

                    message = message.replace("<prefix>", prefix_string);
                    message = message.replace("<suffix>", suffix_string+"<reset>"); //replaces <suffix> with "<gray>.exe" in message
            //}
            joinLeaveMessage = MiniMessage.miniMessage().deserialize(message);}
        }
        return joinLeaveMessage;
    }

    private void fakeJoinLeave (String message, Player player){

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (sender instanceof Player == false && args.length == 0){ //If sender is console and provided no playername
            sender.sendMessage("Usage: /" + label + " <player>");
            return true;
        } else if (sender instanceof Player == false && args.length > 0){ //If sender is console and provided playername
            player = Bukkit.getPlayerExact(args[0]);
            if (player == null) {
                sender.sendMessage("Player not found");
                return true;}
        } else { //Sender is player.
            player = ((Player) sender).getPlayer();
        }

        if (player.hasPermission("essentials.vanish")) {
            if (command.getName().equalsIgnoreCase("vanishjoin")) {
                Component joinMessage = customMessage(configJoinMessage, player);
                broadcast(joinMessage);
                player.performCommand("vanish off");
            } else if (command.getName().equalsIgnoreCase("vanishleave")) {
                Component leaveMessage = customMessage(configLeaveMessage, player);
                broadcast(leaveMessage);
                player.performCommand("vanish on");}
        } else {
            sender.sendMessage("You do not have permission to vanish.");
        }
        return false;
    }
}