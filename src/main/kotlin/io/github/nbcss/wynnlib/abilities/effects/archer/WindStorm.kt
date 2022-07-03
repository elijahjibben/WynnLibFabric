package io.github.nbcss.wynnlib.abilities.effects.archer

import com.google.gson.JsonObject
import io.github.nbcss.wynnlib.abilities.Ability
import io.github.nbcss.wynnlib.abilities.display.ArcherStreamTooltip
import io.github.nbcss.wynnlib.abilities.display.DamageTooltip
import io.github.nbcss.wynnlib.abilities.display.EffectTooltip
import io.github.nbcss.wynnlib.abilities.effects.AbilityEffect
import io.github.nbcss.wynnlib.abilities.effects.BaseEffect
import io.github.nbcss.wynnlib.abilities.properties.ArcherStreamProperty
import io.github.nbcss.wynnlib.abilities.properties.DamageProperty

class WindStorm(parent: Ability, json: JsonObject): BaseEffect(parent, json),
    DamageProperty, ArcherStreamProperty {
    companion object: AbilityEffect.Factory {
        override fun create(parent: Ability, properties: JsonObject): WindStorm {
            return WindStorm(parent, properties)
        }
    }
    private val damage: DamageProperty.Damage = DamageProperty.readModifier(json)
    private val streams: Int = ArcherStreamProperty.read(json)

    override fun getDamage(): DamageProperty.Damage = damage

    override fun getArcherStreams(): Int = streams

    override fun getTooltipItems(): List<EffectTooltip> {
        return listOf(DamageTooltip.Modifier, ArcherStreamTooltip.Modifier)
    }
}