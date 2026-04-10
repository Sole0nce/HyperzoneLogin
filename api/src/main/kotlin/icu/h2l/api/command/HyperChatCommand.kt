/*
 * This file is part of HyperZoneLogin, licensed under the GNU Affero General Public License v3.0 or later.
 *
 * Copyright (C) ksqeib (庆灵) <ksqeib@qq.com>
 * Copyright (C) contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package icu.h2l.api.command

import com.velocitypowered.api.command.CommandSource

interface HyperChatCommandInvocation {
    fun source(): CommandSource
    fun arguments(): Array<String>
    fun alias(): String
}

interface HyperChatCommandExecutor {
    fun execute(invocation: HyperChatCommandInvocation)

    fun hasPermission(invocation: HyperChatCommandInvocation): Boolean = true
}

data class HyperChatCommandRegistration(
    val name: String,
    val aliases: Set<String> = emptySet(),
    val executor: HyperChatCommandExecutor,
    val brigadier: HyperChatBrigadierRegistration? = null
)

interface HyperChatCommandManager {
    fun register(registration: HyperChatCommandRegistration)
    fun unregister(name: String)
    fun executeChat(source: CommandSource, chat: String): Boolean
    fun getRegisteredCommands(): Collection<HyperChatCommandRegistration>
}

interface HyperChatCommandManagerProvider {
    val chatCommandManager: HyperChatCommandManager
}
