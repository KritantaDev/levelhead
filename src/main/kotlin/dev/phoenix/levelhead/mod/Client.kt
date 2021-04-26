package dev.phoenix.levelhead.mod

import net.minecraft.client.entity.EntityPlayerSP
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraft.client.Minecraft
import dev.phoenix.chat.server.hypixel.LocationTracker
import dev.phoenix.levelhead.levelhead.LevelHeadManager
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.network.FMLNetworkEvent
import javax.xml.stream.Location

/**
 * This represents the local minecraft client. 
 * 
 * It abstracts out interactions at the forge/network level, processes some events, and stores current player/server objects
 */
object Client {
    var currentServer: ServerData? = null
    var player: EntityPlayerSP? = null
    var world: World? = null

    fun sendChat(string: String?) {
        player!!.sendChatMessage(string)
    }

    @SubscribeEvent
    fun clientConnectedToServer(event: FMLNetworkEvent.ClientConnectedToServerEvent) {
        player = null
    }

    @SubscribeEvent
    fun entityJoinWorld(event: EntityJoinWorldEvent) {
        if (event.entity is EntityPlayerSP) {

            if (currentServer != null) {
                if (currentServer!!.serverIP.contains("hypixel"))
                {
                    if (LocationTracker.waiting)
                        return

                    MinecraftForge.EVENT_BUS.register(LocationTracker)
                    LocationTracker.waiting = true
                    sendChat("/locraw")
                    world = event.world
                }
                return
            }

            player = event.entity as EntityPlayerSP
            currentServer = Minecraft.getMinecraft().currentServerData


        } else if (event.entity is EntityOtherPlayerMP) {
            LevelHeadManager.registerPlayer(event.entity as EntityOtherPlayerMP)
        }
    }
}