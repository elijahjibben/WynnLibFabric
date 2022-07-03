package io.github.nbcss.wynnlib.abilities.effects.archer

import com.google.gson.JsonObject
import io.github.nbcss.wynnlib.abilities.Ability
import io.github.nbcss.wynnlib.abilities.display.*
import io.github.nbcss.wynnlib.abilities.effects.AbilityEffect
import io.github.nbcss.wynnlib.abilities.effects.BaseEffect
import io.github.nbcss.wynnlib.abilities.properties.*
import io.github.nbcss.wynnlib.data.SpellSlot
import io.github.nbcss.wynnlib.registry.AbilityRegistry

class PhantomRay(parent: Ability, json: JsonObject): BaseEffect(parent, json),
    BoundSpellProperty, DamageProperty, ManaCostModifierProperty, RangeProperty, ReplacingProperty {
    companion object: AbilityEffect.Factory {
        override fun create(parent: Ability, properties: JsonObject): PhantomRay {
            return PhantomRay(parent, properties)
        }
    }
    private val spell: SpellSlot = BoundSpellProperty.read(json)
    private val range: Double = RangeProperty.read(json)
    private val modifier: Int = ManaCostModifierProperty.read(json)
    private val damage: DamageProperty.Damage = DamageProperty.readDamage(json)
    private val replacing: String = ReplacingProperty.read(json)

    override fun getSpell(): SpellSlot = spell

    override fun getManaModifier(): Int = modifier

    override fun getDamage(): DamageProperty.Damage = damage

    override fun getRange(): Double = range

    override fun getReplacingAbility(): Ability? {
        return AbilityRegistry.get(replacing)
    }

    override fun getTooltipItems(): List<EffectTooltip> {
        return listOf(ManaCostModifierTooltip, DamageTooltip, RangeTooltip, ReplacingTooltip)
    }
}