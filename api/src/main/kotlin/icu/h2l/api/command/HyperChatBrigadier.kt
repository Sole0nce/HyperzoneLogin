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

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandSource

fun interface HyperChatBrigadierRegistration {
    fun create(context: HyperChatBrigadierContext): LiteralArgumentBuilder<CommandSource>
}

class HyperChatBrigadierContext(
    val registration: HyperChatCommandRegistration,
    private val visibility: (CommandSource) -> Boolean,
    private val executor: (CommandSource, String, Array<String>) -> Int
) {
    fun isVisibleTo(source: CommandSource): Boolean = visibility(source)

    fun literal(name: String = registration.name): LiteralArgumentBuilder<CommandSource> {
        return BrigadierCommand.literalArgumentBuilder(name)
            .requires(visibility)
    }

    fun execute(
        source: CommandSource,
        alias: String = registration.name,
        args: Array<String> = emptyArray()
    ): Int {
        return executor(source, alias, args)
    }

    fun executeGreedy(
        source: CommandSource,
        rawArguments: String,
        alias: String = registration.name
    ): Int {
        return execute(source, alias, splitArguments(rawArguments))
    }

    fun greedyArguments(
        argumentName: String = "arguments",
        alias: String = registration.name
    ) = BrigadierCommand.requiredArgumentBuilder(argumentName, StringArgumentType.greedyString())
        .executes { context ->
            executeGreedy(
                context.source,
                StringArgumentType.getString(context, argumentName),
                alias
            )
        }

    fun splitArguments(rawArguments: String): Array<String> {
        return rawArguments.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .toTypedArray()
    }
}

