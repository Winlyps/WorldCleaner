//1.File: WorldCleaner.kt
package winlyps.worldCleaner

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class WorldCleaner : JavaPlugin(), CommandExecutor, Listener {

    private val collectedItems: Inventory = Bukkit.createInventory(null, 54, "Collected Items")

    override fun onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this)
        getCommand("zlomiarz")?.setExecutor(this)

        // Schedule task to clean items every 2 minutes
        Bukkit.getScheduler().runTaskTimer(this, Runnable {
            Bukkit.getWorlds().forEach { world ->
                world.entities.filter { it.type == EntityType.DROPPED_ITEM }
                        .forEach { entity ->
                            val item = entity as org.bukkit.entity.Item
                            addItemToInventory(collectedItems, item.itemStack)
                            item.remove()
                        }
            }
        }, 2400L, 2400L) // 2400 ticks = 2 minutes
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player && command.name.equals("zlomiarz", ignoreCase = true)) {
            sender.openInventory(collectedItems)

            // Schedule the inventory to close after 10 seconds (200 ticks)
            Bukkit.getScheduler().runTaskLater(this, Runnable {
                if (sender.openInventory.topInventory == collectedItems) {
                    sender.closeInventory()
                }
            }, 200L) // 200 ticks = 10 seconds

            return true
        }
        return false
    }

    @EventHandler
    fun onItemSpawn(event: ItemSpawnEvent) {
        // Optional: You can add logic here if you want to track items as they spawn
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        // Optional: You can add logic here if you want to track when the inventory is closed
    }

    private fun addItemToInventory(inventory: Inventory, itemStack: ItemStack) {
        if (inventory.firstEmpty() == -1) {
            // Inventory is full, randomly replace an item
            val randomSlot = Random().nextInt(inventory.size)
            inventory.setItem(randomSlot, itemStack)
        } else {
            // Inventory has space, add the item
            inventory.addItem(itemStack)
        }
    }
}