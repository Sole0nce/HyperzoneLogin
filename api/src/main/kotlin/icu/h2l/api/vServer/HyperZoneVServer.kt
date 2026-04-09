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

package icu.h2l.api.vServer

import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.command.SimpleCommand

/**
 * Adapter interface for Limbo functionality used by the project.
 * This keeps the API module free of a compile-time dependency on the
 * Limbo third-party API; implementations (in the main plugin) can bridge
 * to the real Limbo implementation when available.
 */
interface HyperZoneVServerAdapter {
    fun registerCommand(meta: CommandMeta, command: SimpleCommand)
}

interface HyperZoneVServerProvider {
    /**
     * Returns the limbo adapter if available, or null when Limbo is not present.
     */
    val serverAdapter: HyperZoneVServerAdapter?
}
