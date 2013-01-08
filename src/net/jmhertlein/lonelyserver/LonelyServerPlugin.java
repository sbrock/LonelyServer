/*
This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jmhertlein.lonelyserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author joshua
 */
public class LonelyServerPlugin extends JavaPlugin {
    private static final Logger mcLogger = Logger.getLogger("Minecraft");
    private static final String configFile = "config.yml";
    private static ChatColor color;
    
    private Player mostRecentLogoffPlayer;
    private long mostRecentLogoffTime;
    
    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(new LogListener(), this);
        mcLogger.log(Level.INFO, "LonelyServer is free software. For more information, see http://www.gnu.org/licenses/quick-guide-gplv3.html and http://www.gnu.org/licenses/lgpl.txt");
        mcLogger.log(Level.INFO, "LonelyServer's source code is available as per its license here: https://github.com/jmhertlein/LonelyServer");
        
        
        color = ChatColor.DARK_AQUA;
        File sourceDir = getDataFolder();
        
        if(!sourceDir.exists())
            sourceDir.mkdir();
        
        FileConfiguration config = new YamlConfiguration();
        try {
            mcLogger.log(Level.INFO, "Lonely Server: Config loaded.");
            config.load(new File(sourceDir, configFile));
            color = ChatColor.valueOf(config.getString("chatColor"));
        } catch (FileNotFoundException ex) {
            config.set("chatColor", color.name()); //load default
            try {
                config.save(new File(sourceDir, configFile));
                mcLogger.log(Level.INFO, "Lonely Server: Default config written.");
                
            } catch (IOException ex1) {
                mcLogger.log(Level.SEVERE, "Lonely Server: Error writing default config");
            }
        } catch (IOException | InvalidConfigurationException ex) {
            config.set("chatColor", color.toString());
            mcLogger.log(Level.SEVERE, "Lonely Server: Error loading config; probably bad markup in the file?");
        }
    }
    
    private class LogListener implements Listener {
        @EventHandler
        public void onPlayerLogoff(PlayerQuitEvent e) {
            mostRecentLogoffPlayer = e.getPlayer();
            mostRecentLogoffTime = System.currentTimeMillis();
        }
        
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent e) {
            //System.out.println("This method observes that there are " + Bukkit.getServer().getOnlinePlayers().length + " players online.");
            if(Bukkit.getServer().getOnlinePlayers().length == 1 && mostRecentLogoffPlayer != null) {
                e.getPlayer().sendMessage(color + "The last player was online " + getMinutesSinceLastLogoff() + " minutes ago.");
                mcLogger.log(Level.INFO, e.getPlayer().getName() + " logged in alone, and was notified that the last player only logged off " + getMinutesSinceLastLogoff() + " minutes ago.");
            }
        }
        
    }
    
    private long getMinutesSinceLastLogoff() {
        long timeSpan = System.currentTimeMillis() - mostRecentLogoffTime;
        
        timeSpan /= 1000;
        timeSpan /= 60;
        
        return timeSpan;
    }
    
}
