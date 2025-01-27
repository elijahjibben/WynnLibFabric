package io.github.nbcss.wynnlib.gui.widgets.buttons

import com.mojang.blaze3d.systems.RenderSystem
import io.github.nbcss.wynnlib.gui.TooltipScreen
import io.github.nbcss.wynnlib.items.identity.TooltipProvider
import io.github.nbcss.wynnlib.render.RenderKit
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.PressableWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.function.Consumer

class ConfirmButtonWidget(private val handler: Consumer<ConfirmButtonWidget>,
                          private val tooltipProvider: TooltipProvider? = null,
                          private val screen: TooltipScreen,
                          x: Int, y: Int):
    PressableWidget(x, y, 10, 10, Text.empty()) {
    private val texture: Identifier = Identifier("wynnlib", "textures/gui/check_button.png")
    //override fun appendNarrations(builder: NarrationMessageBuilder?) {
    //    appendDefaultNarrations(builder)
    //}

    override fun onPress() {
        handler.accept(this)
    }

    override fun renderButton(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        RenderSystem.enableDepthTest()
        val v = if (isHovered) 10 else 0
        RenderKit.renderTexture(context, texture, x, y, 0, v, 10, 10, 10, 20)
        if(isHovered && tooltipProvider != null) {
            val tooltip = tooltipProvider.getTooltip()
            screen.drawTooltip(context!!, tooltip, mouseX, mouseY)
        }
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder?) {
        // todo
    }
}