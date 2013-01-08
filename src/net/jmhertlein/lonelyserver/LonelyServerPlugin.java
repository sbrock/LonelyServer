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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    private Player mostRecentLogoffPlayer;
    private long mostRecentLogoffTime;
    
    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(new LogListener(), this);
        Logger.getLogger("Minecraft").log(Level.INFO, "LonelyServer is free software. For more information, see http://www.gnu.org/licenses/quick-guide-gplv3.html and http://www.gnu.org/licenses/lgpl.txt");
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
                e.getPlayer().sendMessage(ChatColor.DARK_AQUA + "The last player was online " + getMinutesSinceLastLogoff() + " minutes ago.");
                Logger.getLogger("Minecraft").log(Level.INFO, e.getPlayer().getName() + " logged in alone, and was notified that the last player only logged off " + getMinutesSinceLastLogoff() + " minutes ago.");
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
