package chatondiscord;

import chatondiscord.webhook.DiscordWebhook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getConfig().addDefault("links.webhook", "discord.com/...");

        getConfig().addDefault("colors.chat.red", 153);
        getConfig().addDefault("colors.chat.green", 40);
        getConfig().addDefault("colors.chat.blue", 32);

        getConfig().addDefault("colors.server.start.red",25);
        getConfig().addDefault("colors.server.start.green",166);
        getConfig().addDefault("colors.server.start.blue",15);

        getConfig().addDefault("colors.server.stop.red",255);
        getConfig().addDefault("colors.server.stop.green",50);
        getConfig().addDefault("colors.server.stop.blue",50);

        getConfig().addDefault("colors.player.left_server.red",255);
        getConfig().addDefault("colors.player.left_server.green",50);
        getConfig().addDefault("colors.player.left_server.blue",50);

        getConfig().addDefault("colors.player.join_server.red",25);
        getConfig().addDefault("colors.player.join_server.green",166);
        getConfig().addDefault("colors.player.join_server.blue",15);

        getConfig().addDefault("messages.chat", "[%time%] %username%: %chat%");

        getConfig().addDefault("messages.player.join_server", "%username% joined the server! %player_count%/%player_max%");
        getConfig().addDefault("messages.player.left_server", "%username% left the server :( %player_count%/%player_max%");

        getConfig().addDefault("messages.server.stop", ":exclamation: Server stopped!");
        getConfig().addDefault("messages.server.start", ":white_check_mark: Server started!");

        getConfig().addDefault("messages.options.addAuthor", true);
        getConfig().addDefault("messages.options.24hourformat", true);

        if(!new File(getDataFolder(), "config.yml").exists()){
            getConfig().options().copyDefaults(true);
            saveConfig();
        }

        System.out.println("Sending start webhook!");
        DiscordWebhook start = new DiscordWebhook(getConfig().getString("links.webhook"));
        start.addEmbed(new DiscordWebhook.EmbedObject().setDescription(getConfig().getString("messages.server.start").replaceAll("%time%",new SimpleDateFormat(getConfig().getBoolean("messages.options.24hourformat") ? "HH:mm" : "hh:mm").format(new Date()))).setColor(new Color(getConfig().getInt("colors.server.start.red"),getConfig().getInt("colors.server.start.green"), getConfig().getInt("colors.server.start.blue"))));
        try {
            start.execute();
        } catch (IOException e) {
            System.out.println("Error sending webhook! " + "\"" + e.getMessage() + "\"");
        }

        Bukkit.getPluginManager().registerEvents(this,this);

        getServer().getPluginCommand("chatondiscord").setExecutor((sender,cmd,label,args) -> {
            if(cmd.getName().equalsIgnoreCase("chatondiscord")) {
              if(args.length == 1) {
                  if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")){
                    reloadConfig();
                    sender.sendMessage(ChatColor.DARK_GREEN + "Sucessfully reloaded configuration!");
                  }else{
                      sender.sendMessage(ChatColor.DARK_RED + "/chatondiscord || cnd || chatondsc <reload || rl>");
                  }
              } else {
                  sender.sendMessage(ChatColor.DARK_RED + "/chatondiscord || cnd || chatondsc <reload || rl>");
              }
           }
            return false;
        });



    }


    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onDisable() {
        DiscordWebhook shutdown = new DiscordWebhook(getConfig().getString("links.webhook"));
        shutdown.addEmbed(new DiscordWebhook.EmbedObject().setDescription(getConfig().getString("messages.server.stop").replaceAll("%time%",new SimpleDateFormat(getConfig().getBoolean("messages.options.24hourformat") ? "HH:mm" : "hh:mm").format(new Date()))).setColor(new Color(getConfig().getInt("colors.server.stop.red"),getConfig().getInt("colors.server.stop.green"), getConfig().getInt("colors.server.stop.blue"))));
        System.out.println("Sending shutdown webhook!");
        try {
            shutdown.execute();

        } catch (IOException e) {
            System.out.println("Error sending webhook! " + "\"" + e.getMessage() + "\"");
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        DiscordWebhook chat = new DiscordWebhook(getConfig().getString("links.webhook"));
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        chat.addEmbed(embed.setDescription(getConfig().getString("messages.chat").replaceAll("%time%",new SimpleDateFormat(getConfig().getBoolean("messages.options.24hourformat") ? "HH:mm" : "hh:mm").format(new Date())).replaceAll("%chat%", event.getMessage()).replaceAll("%username%", event.getPlayer().getDisplayName())).setColor(new Color(getConfig().getInt("colors.chat.red"),getConfig().getInt("colors.chat.green"),getConfig().getInt("colors.chat.blue"))));
        if(getConfig().getBoolean("messages.options.addAuthor")) {
            String username = event.getPlayer().getDisplayName();
            String author_icon_url = String.format("https://minotar.net/helm/%s/1024.png",username);
            embed.setAuthor(username,String.format("https://namemc.com/profile/%s.1", username),author_icon_url);
        }
        System.out.println("Sending chat webhook!");
        try {
            chat.execute();
        } catch (IOException e) {
            System.out.println("Error sending webhook! " + "\"" + e.getMessage() + "\"");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        DiscordWebhook join = new DiscordWebhook(getConfig().getString("links.webhook"));
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        join.addEmbed(embed.setDescription(getConfig().getString("messages.player.join_server").replaceAll("%time%",new SimpleDateFormat(getConfig().getBoolean("messages.options.24hourformat") ? "HH:mm" : "hh:mm").format(new Date())).replaceAll("%username%", event.getPlayer().getDisplayName()).replaceAll("%player_count%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size())).replaceAll("%player_max%", String.valueOf(Bukkit.getServer().getMaxPlayers()))).setColor(new Color(getConfig().getInt("colors.player.join_server.red"),getConfig().getInt("colors.player.join_server.green"),getConfig().getInt("colors.player.join_server.blue"))));
        if(getConfig().getBoolean("messages.options.addAuthor")) {
            String username = event.getPlayer().getDisplayName();
            String author_icon_url = String.format("https://minotar.net/helm/%s/1024.png",username);
            embed.setAuthor(username,String.format("https://namemc.com/profile/%s.1", username),author_icon_url);
        }
        System.out.println("Sending join webhook!");
        try {
            join.execute();
        } catch (IOException e) {
            System.out.println("Error sending webhook! " + "\"" + e.getMessage() + "\"");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        DiscordWebhook quit = new DiscordWebhook(getConfig().getString("links.webhook"));
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        quit.addEmbed(embed.setDescription(getConfig().getString("messages.player.left_server").replaceAll("%time%",new SimpleDateFormat(getConfig().getBoolean("messages.options.24hourformat") ? "HH:mm" : "hh:mm").format(new Date())).replaceAll("%username%", event.getPlayer().getDisplayName()).replaceAll("%player_count%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size())).replaceAll("%player_max%", String.valueOf(Bukkit.getServer().getMaxPlayers()))).setColor(new Color(getConfig().getInt("colors.player.left_server.red"),getConfig().getInt("colors.player.left_server.green"),getConfig().getInt("colors.player.left_server.blue"))));

        if(getConfig().getBoolean("messages.options.addAuthor")) {
            String username = event.getPlayer().getDisplayName();
            String author_icon_url = String.format("https://minotar.net/helm/%s/1024.png",username);
            embed.setAuthor(username,String.format("https://namemc.com/profile/%s.1", username),author_icon_url);
        }
        System.out.println("Sending join webhook!");
        try {
            quit.execute();
        } catch (IOException e) {
            System.out.println("Error sending webhook! " + "\"" + e.getMessage() + "\"");
        }
    }



}
