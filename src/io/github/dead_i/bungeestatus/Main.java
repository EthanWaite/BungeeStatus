package io.github.dead_i.bungeestatus;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.craftminecraft.bungee.bungeeyaml.pluginapi.ConfigurablePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main extends ConfigurablePlugin {
    public void onEnable() {
        getLogger().info("BungeeStatus Mojang Server Monitor is enabled.");
        saveDefaultConfig();

        long interval = getConfig().getLong("interval");
        getProxy().getScheduler().schedule(this, new Runnable() {
            Map<String, Boolean> servers = new HashMap<String, Boolean>();
            @Override
            public void run() {
                try {
                    InputStream input = new URL("http://status.mojang.com/check").openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String status = reader.readLine();
                    reader.close();
                    input.close();

                    JsonParser parser = new JsonParser();
                    JsonArray json = parser.parse(status).getAsJsonArray();
                    for (Object obj : json) {
                        Map.Entry<String, JsonElement> entry  = parser.parse(obj.toString()).getAsJsonObject().entrySet().iterator().next();
                        if (entry.getValue().getAsString().equals("green")) {
                            servers.put(entry.getKey(), true);
                        }else if (!servers.containsKey(entry.getKey()) || servers.get(entry.getKey())) {
                            servers.put(entry.getKey(), false);
                            getProxy().broadcast(new TextComponent(ChatColor.translateAlternateColorCodes('&', getConfig().getString("format").replace("{MESSAGE}", getConfig().getString("messages." + entry.getKey())))));
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    getLogger().warning("Failed to reach the Mojang status page.");
                }
            }
        }, interval, interval, TimeUnit.SECONDS);
    }
}
