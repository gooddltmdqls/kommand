/*
 * Kommand
 * Copyright (C) 2021 Monun
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package xyz.icetang.lib.kommand

import xyz.icetang.lib.kommand.loader.KommandLoader
import xyz.icetang.lib.kommand.node.RootNode
import org.bukkit.plugin.Plugin

@xyz.icetang.lib.kommand.KommandDSL
interface Kommand {
    companion object : xyz.icetang.lib.kommand.Kommand by KommandLoader.loadCompat(xyz.icetang.lib.kommand.Kommand::class.java)

    fun register(
        plugin: Plugin,
        name: String,
        vararg aliases: String,
        init: RootNode.() -> Unit
    ): xyz.icetang.lib.kommand.KommandDispatcher
}

@DslMarker
annotation class KommandDSL

@xyz.icetang.lib.kommand.KommandDSL
class PluginKommand internal constructor(
    private val plugin: Plugin
) {
    fun register(
        name: String,
        vararg aliases: String,
        init: RootNode.() -> Unit
    ) = xyz.icetang.lib.kommand.Kommand.register(plugin, name, *aliases) { init() }

    operator fun String.invoke(
        vararg aliases: String,
        init: RootNode.() -> Unit
    ) = register(this, *aliases, init = init)
}

fun Plugin.kommand(
    init: xyz.icetang.lib.kommand.PluginKommand.() -> Unit
) {
    xyz.icetang.lib.kommand.PluginKommand(this).init()
}
