package dev.phoenix.levelhead.server.hypixel.api

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken

class Player(val uuid: String, val dataRaw: String) {
    var data: Map<String, Any> = emptyMap()
    var exists: Boolean = false
    var level: Int = 0
    var name: String = ""
    var karma: Int = 0
    var quests: Int = 0
    init {
        if (!dataRaw.contains("error\":")) {
            val jp = JsonParser()
            val root: JsonElement =
                jp.parse(dataRaw)
            val rt = object : TypeToken<Map<String, Any>>() {}.type
            val rootobj = root.asJsonObject
            data = Gson().fromJson(rootobj.toString(), rt)
            exists = true
            level = (data["level"] as Double).toInt()
            name = data["username"] as String
            karma = (data["karma"] as Double).toInt()
        }
    }
}