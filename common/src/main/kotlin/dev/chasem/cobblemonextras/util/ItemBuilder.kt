package dev.chasem.cobblemonextras.util

import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.util.Unit
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.component.CustomModelData
import net.minecraft.world.item.component.ItemLore
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream


class ItemBuilder {
    var stack: ItemStack? = null

    constructor(item: Item?) {
        this.stack = ItemStack(item)
    }

    constructor(item: ItemStack?) {
        this.stack = item
    }

    fun addLore(newLore: Array<Component>?): ItemBuilder {
        var itemLore = stack!!.get(DataComponents.LORE)

        if (itemLore == null) {
            itemLore = ItemLore(listOf())
        }

        var list = Stream.concat(itemLore.lines().stream(), Arrays.stream(newLore)).collect(Collectors.toList())
        // Go through every Component, and get the current style and set Italic to false
        list = list.stream().map { component: Component -> component.copy().withStyle(component.style.withItalic(false)) }.collect(Collectors.toList()) as List<Component>?

        itemLore = ItemLore(list)
        stack!!.set(DataComponents.LORE, itemLore)
        return this
    }

    fun setCustomModel(customModelVal: Int): ItemBuilder {
        this.stack!!.set(DataComponents.CUSTOM_MODEL_DATA, CustomModelData(customModelVal))
        return this
    }


    fun setCustomData(data: CustomData): ItemBuilder {
        // If the stack has custom data, get it and append the new data to it, otherwise create a new custom data with the new data
        val customData = if (stack!!.has(DataComponents.CUSTOM_DATA)) stack!!.get(DataComponents.CUSTOM_DATA) else CustomData.of(CompoundTag())

        val newTag = customData!!.copyTag()
        val tag = data.copyTag()

        tag.allKeys.forEach { key -> newTag.put(key, tag.get(key)) }

        stack!!.set(DataComponents.CUSTOM_DATA, CustomData.of(newTag))
        return this
    }

    fun setAmount(amount: Int): ItemBuilder {
        this.stack!!.setCount(amount);
        return this;
    }

    fun hideAdditional(): ItemBuilder {
//        this.stack.addHideFlag(ItemStack.TooltipSection.ADDITIONAL);
        stack!!.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
        return this
    }

    fun setCustomName(customName: Component?): ItemBuilder {
//        Text pokemonName = Texts.join(customName.getWithStyle(Style.EMPTY.withItalic(false)), Component.literal(""));
//        this.stack.setCustomName(pokemonName);
        stack!!.set(DataComponents.CUSTOM_NAME, customName)

        return this
    }

    fun build(): ItemStack {
        return this.stack!!
    }
}
