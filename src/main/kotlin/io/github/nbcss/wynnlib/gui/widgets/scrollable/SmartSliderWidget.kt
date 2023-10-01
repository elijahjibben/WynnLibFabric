package io.github.nbcss.wynnlib.gui.widgets.scrollable

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ButtonTextures
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ButtonWidget.NarrationSupplier
import net.minecraft.client.render.GameRenderer
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import java.util.function.Consumer
import kotlin.math.abs
import kotlin.math.roundToInt

open class SmartSliderWidget(private val posX: Int,
                             private val posY: Int,
                             private val minValue: Int,
                             private val maxValue: Int,
                             width: Int,
                             private val format: String?):
    ButtonWidget(-1000, -1000, width, 20, Text.empty(), null, NarrationSupplier { Text.empty() }), ScrollElement {
    protected var gap = if (minValue == maxValue) 1 else abs(maxValue - minValue)
    private var handler: Consumer<Int>? = null
    private var interactable: Boolean = true
    private var dragging = false
    protected var pos = minValue
    protected var previous:Int = -1
    protected var buffer: String? = null
    init {
        visible = false
    }

    fun setHandler(handler: Consumer<Int>?) {
        this.handler = handler
    }

    fun setPosition(pos: Int) {
        this.pos = MathHelper.clamp(pos, minValue, maxValue)
    }

    protected fun getHoverState(mouseOver: Boolean): Int {
        return 0
    }

    override fun updateState(x: Int, y: Int, active: Boolean) {
        this.x = posX + x
        this.y = posY + y
        this.interactable = active
        visible = true
    }

    override fun renderButton(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        if (this.visible) {
            val textures  = ButtonTextures(
                Identifier("widget/button"),
                Identifier("widget/button_disabled"),
                Identifier("widget/button_highlighted")
            )
            RenderSystem.setShader { GameRenderer.getPositionTexProgram() }
            RenderSystem.setShaderTexture(0, textures.get(this.active, this.isSelected))
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha)
            RenderSystem.enableBlend()
            RenderSystem.defaultBlendFunc()
            context?.drawGuiTexture(textures.get(this.active, this.isSelected), this.x, this.y, 0, this.width, this.height)
//            context?.drawTexture(WIDGETS_TEXTURE, this.x, this.y, 0, 46F,
//                (this.width / 2).toFloat(), this.width, this.height, 256, 256)
            context?.drawGuiTexture(textures.get(this.active, this.isSelected), this.x + this.width / 2, this.y, 0, 20, this.height)
//            context?.drawTexture(
//                WIDGETS_TEXTURE,
//                this.x + this.width / 2,
//                this.y,
//                200 - this.width / 2,
//                46F,
//                (this.width / 2).toFloat(),
//                20,
//                this.height,
//                256,
//                256
//            )
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha)
            val slider = abs(pos - minValue) / gap.toFloat()
            context?.drawGuiTexture(textures.get(this.active, this.isSelected), this.x + (slider * (this.width - 8).toFloat()).toInt(), this.y, 0, 20, this.height)
//            context?.drawTexture(WIDGETS_TEXTURE,
//                this.x + (slider * (this.width - 8).toFloat()).toInt(),
//                this.y,
//                0,
//                66F,
//                4F,
//                20,
//                this.height,
//                256,
//                256
//            )
            context?.drawGuiTexture(
                textures.get(this.active, this.isSelected),
                this.x + (slider * (this.width - 8).toFloat()).toInt() + 4,
                this.y,
                196,
                20,
                this.height
            )
//            context?.drawTexture(
//                WIDGETS_TEXTURE,
//                this.x + (slider * (this.width - 8).toFloat()).toInt() + 4,
//                this.y,
//                196,
//                66F,
//                4F,
//                20,
//                this.height,
//                256,
//                256
//            )
            val textColor = if (!this.active) {
                10526880
            } else if (this.hovered) {
                16777120
            }else {
                14737632
            }
            val s = format!!.replace("%value%", if (buffer == null) pos.toString() + "" else buffer + "_")
            context!!.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, s, this.x + this.width / 2, this.y + (this.height -8) / 2, textColor)
        }
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (this.visible && dragging) {
            var slider = (mouseX - (this.x + 4)).toFloat() / (this.width - 8).toFloat()
            slider = MathHelper.clamp(slider, 0.0f, 1.0f)
            setSliderPosition(slider)
            return true
        }
        return false
    }

    private fun clearBuffer() {
        buffer = null
        if (handler != null) handler!!.accept(pos)
    }

    fun setSliderPosition(slider: Float) {
        setPosition(minValue + (gap * slider).roundToInt())
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return if (isMouseOver(mouseX, mouseY)) {
            playDownSound(MinecraftClient.getInstance().soundManager);
            if (button == 1) {
                buffer = if (buffer == null) "" + pos else ""
                setPosition(if (buffer == "") minValue else parseBuffer())
            } else if (buffer != null) {
                clearBuffer()
            }
            if (button == 0) {
                previous = pos
                var slider = (mouseX - (this.x + 4)).toFloat() / (this.width - 8).toFloat()
                slider = MathHelper.clamp(slider, 0.0f, 1.0f)
                setSliderPosition(slider)
                dragging = true
            } else {
                return false
            }
            true
        } else {
            if (buffer != null) {
                clearBuffer()
            }
            false
        }
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (dragging && previous != pos && handler != null) {
            handler!!.accept(pos)
        }
        previous = -1
        dragging = false
        return buffer != null
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (buffer != null) {
            if (keyCode == 28) {
                clearBuffer()
                return true
            }
            if (keyCode == 14 && buffer!!.isNotEmpty()) {
                buffer = buffer!!.substring(0, buffer!!.length - 1)
            }
            try {
                setPosition(if (buffer == "") minValue else buffer!!.toInt())
                if (buffer != "") buffer = "" + pos
            } catch (ignore: NumberFormatException) {
            }
            return true
        }
        return false
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        buffer?.let {
            if (chr in '0'..'9') buffer = it + chr
            try {
                setPosition(if (buffer == "") minValue else buffer!!.toInt())
                if (buffer != "")
                    buffer = "" + pos
            } catch (ignore: NumberFormatException) {
            }
            return true
        }
        return false
    }

    private fun parseBuffer(): Int {
        return try {
            buffer!!.toInt()
        } catch (e: NumberFormatException) {
            minValue
        }
    }
}