package de.nycode.slpf

import net.luckperms.api.LuckPerms
import net.luckperms.api.event.group.GroupCreateEvent
import net.luckperms.api.event.group.GroupDataRecalculateEvent
import net.luckperms.api.event.group.GroupDeleteEvent
import net.luckperms.api.model.group.Group
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Scoreboard
import java.util.function.Consumer

class SimpleLuckPermsFormatter : JavaPlugin(), Listener {

    private lateinit var scoreboard: Scoreboard
    private lateinit var luckPerms: LuckPerms
    private lateinit var chatFormat: String
    private var useColorInsteadOfPrefix: Boolean = true

    override fun onEnable() {
        luckPerms = loadLuckPerms() ?: error("Unable to load the LuckPerms API")
        Bukkit.getPluginManager()
            .registerEvents(this, this)

        createScoreboard()

        val update = Consumer<Any> {
            updateScoreboard()
        }
        luckPerms.eventBus.subscribe(this, GroupDataRecalculateEvent::class.java, update)
        luckPerms.eventBus.subscribe(this, GroupCreateEvent::class.java, update)
        luckPerms.eventBus.subscribe(this, GroupDeleteEvent::class.java, update)

        saveDefaultConfig()
        reloadConfig()
        chatFormat = config.getString("chat-format") ?: "{username}: &7{message}"
        useColorInsteadOfPrefix = config.getBoolean("chat-use-color-instead-of-prefix")

        Metrics(this, 10680)
    }

    private fun createScoreboard() {
        scoreboard = Bukkit.getScoreboardManager()?.newScoreboard ?: error("Unable to create new scoreboard!")
        updateScoreboard()
    }

    private fun updateScoreboard() {
        scoreboard.teams.forEach {
            it.unregister()
        }
        luckPerms.groupManager.loadedGroups
            .sortedByDescending { it.weight.orElse(0) }
            .forEachIndexed { index, group ->
                val team = scoreboard.registerNewTeam(getNextIndex(index) + group.name)
                team.prefix = group.cachedData.metaData.prefix ?: ""
                team.suffix = group.cachedData.metaData.suffix ?: ""
                team.color = ChatColor.valueOf(group.cachedData.metaData.getMetaValue("color") ?: return@forEachIndexed)
            }

        Bukkit.getOnlinePlayers()
            .forEach {
                it.updatePrefix()
            }
    }

    private fun loadLuckPerms(): LuckPerms? {
        val provider = Bukkit.getServicesManager()
            .getRegistration(LuckPerms::class.java)
        return provider?.provider
    }

    private fun getNextIndex(index: Int): String {
        val withZeros = "%07d".format(index)
        return withZeros.substring(withZeros.lastIndex - 5, withZeros.lastIndex)
    }

    private fun Player.updatePrefix() {
        val group = getLuckPermsGroup() ?: return
        val index = luckPerms.groupManager.loadedGroups.sortedByDescending { it.weight.orElse(0) }
            .indexOf(group)
        this.scoreboard = this@SimpleLuckPermsFormatter.scoreboard
        val team = this@SimpleLuckPermsFormatter.scoreboard.getTeam(getNextIndex(index) + group.name)
        team?.addEntry(name)
    }

    private fun Player.getLuckPermsGroup(): Group? {
        val groupName = luckPerms.userManager.getUser(uniqueId)?.primaryGroup ?: return null
        return luckPerms.groupManager.getGroup(groupName)
    }

    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        player.updatePrefix()
    }

    @EventHandler
    private fun onChat(event: AsyncPlayerChatEvent) {
        val player = event.player
        val group = player.getLuckPermsGroup()
        val prefix = if (useColorInsteadOfPrefix) {
            ChatColor.valueOf(group?.cachedData?.metaData?.getMetaValue("color") ?: "WHITE")
                .toString()
        } else {
            ChatColor.valueOf(group?.cachedData?.metaData?.getMetaValue("color") ?: "WHITE")
                .toString() + group?.cachedData?.metaData?.prefix
        }
        val format = ChatColor.translateAlternateColorCodes(
            '&',
            chatFormat.replace("{username}", "$prefix%s")
                .replace("{message}", "%s")
        )
        event.format = format
    }

}
