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

package icu.h2l.api.log

interface HyperZoneLogger {
    fun info(message: String)
    fun debug(message: String)
    fun warn(message: String)
    fun error(message: String, throwable: Throwable? = null)
}

object HyperZoneLogApi {
    @Volatile
    private var logger: HyperZoneLogger = NoopHyperZoneLogger

    @JvmStatic
    fun registerLogger(newLogger: HyperZoneLogger) {
        logger = newLogger
    }

    @JvmStatic
    fun getLogger(): HyperZoneLogger = logger
}

private object NoopHyperZoneLogger : HyperZoneLogger {
    override fun info(message: String) = Unit
    override fun debug(message: String) = Unit
    override fun warn(message: String) = Unit
    override fun error(message: String, throwable: Throwable?) = Unit
}

inline fun info(block: () -> String) {
    HyperZoneLogApi.getLogger().info(block())
}

inline fun debug(block: () -> String) {
    HyperZoneLogApi.getLogger().debug(block())
}

inline fun warn(block: () -> String) {
    HyperZoneLogApi.getLogger().warn(block())
}

inline fun error(throwable: Throwable? = null, block: () -> String) {
    HyperZoneLogApi.getLogger().error(block(), throwable)
}