package dev.phoenix.levelhead.levelhead

import dev.phoenix.levelhead.server.hypixel.api.HypixelAPIClient
import dev.phoenix.levelhead.server.hypixel.api.Player
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RendererLivingEntity
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11


object LevelHeadManager {

    var players: MutableMap<String, Player?> = mutableMapOf()
    var playerStringMap: MutableMap<Player, String?> = mutableMapOf()

    fun registerPlayer(player: EntityOtherPlayerMP)
    {
        val uuid = uuidOf(player)
        if (players.containsKey(uuid))
            return;
        players[uuid] = null
        HypixelAPIClient.getDataForPlayer(uuid, LevelHeadManager::playerDataCallback)
    }

    fun playerDataCallback(player: Player)
    {
        if (!player.exists)
            return;
        players[player.uuid] = player
        playerStringMap[player] = "Level: ${player.level}"
    }

    private fun uuidOf(player: EntityPlayer): String {
        return EntityPlayer.getUUID(player.gameProfile).toString().replace("-", "")
    }

    @SubscribeEvent
    fun render(event: RenderPlayerEvent.Pre) {
        if (event.entity is EntityPlayer)
        {
            if (players.containsKey(uuidOf(event.entityPlayer)))
                if (playerStringMap.containsKey(players[uuidOf(event.entityPlayer)]))
                {
                    renderName(
                    event.renderer,
                    playerStringMap[players[uuidOf(event.entityPlayer)]],
                    event.entityPlayer,
                    event.x,
                    event.y + 0.6,
                    event.z
                )
                }

        }


    }

    fun renderName(
        renderer: RendererLivingEntity<*>,
        str: String?,
        entityIn: EntityPlayer,
        x: Double,
        y: Double,
        z: Double
    ) {
        val fontrenderer = renderer.fontRendererFromRenderManager
        val f = 1.6f
        val f1 = 0.016666668f * f
        GlStateManager.pushMatrix()
        GlStateManager.translate(x.toFloat() + 0.0f, y.toFloat() + entityIn.height + 0.5f, z.toFloat())
        GL11.glNormal3f(0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(-renderer.renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(renderer.renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
        GlStateManager.scale(-f1, -f1, f1)
        GlStateManager.disableLighting()
        GlStateManager.depthMask(false)
        GlStateManager.disableDepth()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        val i = 0
        val j = fontrenderer.getStringWidth(str) / 2
        GlStateManager.disableTexture2D()
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR)
        worldrenderer.pos((-j - 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        worldrenderer.pos((-j - 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        worldrenderer.pos((j + 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        worldrenderer.pos((j + 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        tessellator.draw()
        GlStateManager.enableTexture2D()
        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, 553648127)
        GlStateManager.enableDepth()
        GlStateManager.depthMask(true)
        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, -1)
        GlStateManager.enableLighting()
        GlStateManager.disableBlend()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.popMatrix()
    }

}