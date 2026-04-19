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

package icu.h2l.login.config
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
@Suppress("ANNOTATION_WILL_BE_APPLIED_ALSO_TO_PROPERTY_OR_FIELD")
@ConfigSerializable
data class CoreConfig(
    @JvmField
    @Comment("Database Configuration | by ksqeib")
    val database: DatabaseSourceConfig = DatabaseSourceConfig(),
    @JvmField
    @Comment("Remap Configuration | by ksqeib")
    val remap: RemapConfig = RemapConfig(),
    @JvmField
    @Comment("Misc Configuration | by ksqeib")
    val misc: MiscConfig = MiscConfig(),
    @JvmField
    @Comment("Debug Configuration | by ksqeib")
    val debug: DebugConfig = DebugConfig(),
    @JvmField
    @Comment("Embedded Modules Configuration | by ksqeib")
    val modules: ModulesConfig = ModulesConfig(),
    @JvmField
    @Comment("VServer Configuration | by ksqeib")
    val vServer: VServerConfig = VServerConfig(),
    @JvmField
    @Comment("Messages Configuration | by ksqeib")
    val messages: MessagesConfig = MessagesConfig()
)
