package de.nycode.slpf

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class UpdateChecker(private val plugin: JavaPlugin, private val resourceId: Int) {

    private val client = HttpClient.newHttpClient()

    private var cachedVersion: String? = null

    fun getVersion(callback: (String) -> Unit) {

        if (cachedVersion != null) {
            cachedVersion?.let(callback)
            return
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.spigotmc.org/legacy/update.php?resource=$resourceId"))
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            response.body().let {
                cachedVersion = it
                callback(it)
            }
        })
    }

}
