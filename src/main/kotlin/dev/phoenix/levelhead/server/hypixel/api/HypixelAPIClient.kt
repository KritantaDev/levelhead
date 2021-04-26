package dev.phoenix.levelhead.server.hypixel.api

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection
import kotlin.reflect.KFunction1

object HypixelAPIClient {
    val rootURL = "https://api.slothpixel.me/api/"
    val playerEndpoint = "players/"
    var players: MutableMap<String, Player?> = mutableMapOf()

    fun getDataForPlayer(uuid: String, callback: KFunction1<Player, Unit>)
    {
        if (players.containsKey(uuid))
            callback(players[uuid]!!)
        else {
            updateForPlayer(uuid, callback)
        }
        players[uuid] = null
    }

    fun updateForPlayer(uuid: String, callback: (player: Player) -> Unit)
    {
        FetchStatsThread(uuid, callback).start()

    }

    fun addPlayerObject(player: Player)
    {
        players[player.uuid] = player
    }

    class FetchStatsThread(val uuid: String, val callback: (player: Player) -> Unit): Thread() {
        override fun run() {
            println("Fetching stats for ${uuid}")
            val url = URL("${HypixelAPIClient.rootURL}${HypixelAPIClient.playerEndpoint}${uuid}")
            val request: URLConnection = url.openConnection()
            request.connect()
            val str = url.readText()
            val playerData = Player(uuid, str)
            HypixelAPIClient.addPlayerObject(playerData)
            callback(playerData)
        }
    }
}