package me.ghosthacks96.spigot.utils;

import me.ghosthacks96.spigot.GHCore;
import org.bukkit.Bukkit;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.function.Consumer;
import java.util.logging.Level;

public class GuiManager {

    private final GHCore plugin;

    public GuiManager(GHCore plugin) {
        this.plugin = plugin;
    }

    // Create and open a chest GUI
    public Inventory createChestGui(Player player, String title, int rows, Consumer<InventoryClickEvent> onClickHandler) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Rows must be between 1 and 6");
        }

        Inventory chestGui = Bukkit.createInventory(null, rows * 9, title);

        // Handle the click actions via an event listener
        Bukkit.getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (!event.getWhoClicked().equals(player) || event.getInventory() != chestGui) {
                    return;
                }

                event.setCancelled(true); // Prevent default behavior
                onClickHandler.accept(event); // Pass the event to the handler
            }
        }, plugin);
        return chestGui;
    }

    public Inventory createAnvilGui(Player player, String name, String defaultText, Consumer<String> onComplete) {
        Inventory anvilGui = Bukkit.createInventory(null, InventoryType.ANVIL, name);

        // Add the default text item to the anvil input slot
        ItemStack defaultItem = new ItemStack(org.bukkit.Material.PAPER);
        org.bukkit.inventory.meta.ItemMeta meta = defaultItem.getItemMeta();
        meta.setDisplayName(defaultText);
        defaultItem.setItemMeta(meta);

        anvilGui.setItem(0, defaultItem); // Slot 0 is the left input slot of the anvil

        // Listen for inventory interactions
        Bukkit.getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent event) {
                if (event.getInventory().equals(anvilGui) && event.getWhoClicked().equals(player)) {
                    if (event.getSlot() == 2) { // Slot 2 is the result slot
                         // Prevent default behavior (like taking items)

                        ItemStack result = event.getInventory().getItem(2);
                        if (result != null && result.hasItemMeta() && result.getItemMeta().hasDisplayName()) {
                            // Pass the resulting name to the callback
                            onComplete.accept(result.getItemMeta().getDisplayName());
                        }else{
                            plugin.logger.log(Level.INFO,plugin.prefix,"ItemName: "+result.getItemMeta().getDisplayName());
                        }

                        // Close the anvil GUI after handling the interaction
                        event.setCancelled(true);
                        InventoryClickEvent.getHandlerList().unregister(this); // Unregister the listener
                    }else{
                        plugin.logger.log(Level.INFO,plugin.prefix,"item slot: "+event.getSlot());
                    }
                }else{
                    plugin.logger.log(Level.INFO,plugin.prefix,"Inv Config GUi: "+event.getInventory().equals(anvilGui)+" Player: "+event.getWhoClicked().equals((player)));
                }
            }

            @org.bukkit.event.EventHandler
            public void onInventoryClose(org.bukkit.event.inventory.InventoryCloseEvent event) {
                if (event.getInventory().equals(anvilGui) && event.getPlayer().equals(player)) {
                    InventoryCloseEvent.getHandlerList().unregister(this); // Unregister the listener when the inventory is closed
                }
            }
        }, plugin);

        return anvilGui;
    }


    public void createSignGui(Player player, Consumer<String[]> onComplete) {
        Location signLoc = player.getLocation().clone().subtract(0, 2, 0);
        Block block = signLoc.getBlock();
        block.setType(Material.OAK_SIGN);

        Sign sign = (Sign) block.getState();

        // Schedule NMS GUI open
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.openSign(sign);
            // Register event listener
            Listener listener = new Listener() {
                @EventHandler
                public void onSignChange(SignChangeEvent event) {
                    if (event.getBlock().equals(block) && event.getPlayer().equals(player)) {
                        onComplete.accept(event.getLines());
                        block.setType(Material.AIR);
                        HandlerList.unregisterAll(this);
                    }
                }

                @EventHandler
                public void onPlayerQuit(PlayerQuitEvent event) {
                    if (event.getPlayer().equals(player)) {
                        block.setType(Material.AIR);
                        HandlerList.unregisterAll(this);
                    }
                }

                @EventHandler
                public void onBlockBreak(BlockBreakEvent event) {
                    if (event.getBlock().equals(block)) {
                        event.setCancelled(true);
                    }
                }
            };
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        }, 2L);
    }

}