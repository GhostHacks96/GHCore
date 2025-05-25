package me.ghosthacks96.spigot.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class GuiManager {

    private final Plugin plugin;

    public GuiManager(Plugin plugin) {
        this.plugin = plugin;
    }

    // Create and open a chest GUI
    public void createChestGui(Player player, String title, int rows, Consumer<InventoryClickEvent> onClickHandler) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Rows must be between 1 and 6");
        }

        Inventory chestGui = Bukkit.createInventory(null, rows * 9, title);

        // Open the inventory for the player
        player.openInventory(chestGui);

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
    }

    // Create and open an anvil GUI
    public void createAnvilGui(Player player, String defaultText, Consumer<String> onComplete) {
        Inventory anvilGui = Bukkit.createInventory(null, 9, "Rename Item");

        // Open an inventory named "Rename Item", simulating an anvil GUI
        player.openInventory(anvilGui);

        // Add the default text item to the anvil input slot
        ItemStack defaultItem = new ItemStack(org.bukkit.Material.PAPER);
        org.bukkit.inventory.meta.ItemMeta meta = defaultItem.getItemMeta();
        meta.setDisplayName(defaultText);
        defaultItem.setItemMeta(meta);

        anvilGui.setItem(0, defaultItem);

        // Listen for anvil interactions
        Bukkit.getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onPrepareAnvil(PrepareAnvilEvent event) {
                AnvilInventory anvilInventory = event.getInventory();
                if (anvilInventory.getViewers().contains(player)) {
                    // Logic to handle completed text input
                    ItemStack result = anvilInventory.getItem(2);
                    if (result != null && result.getItemMeta() != null) {
                        onComplete.accept(result.getItemMeta().getDisplayName());
                    }
                }
            }
        }, plugin);
    }
}