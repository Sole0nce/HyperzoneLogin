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

package icu.h2l.login.auth.offline.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class OfflineMatchConfig {

    @Comment("是否允许进行匹配")
    val enable = true

    @Comment("UUID匹配设定")
    val uuidMatch = UUIDMatch()

    @Comment("Host匹配设定")
    val hostMatch = HostMatch()

    @ConfigSerializable
    class UUIDMatch {
        @ConfigSerializable
        class PCL2 {
            @Comment("PCL2的UUID匹配")
            val enable = true

            @Comment("PCL2的UUID进行哈希计算匹配")
            val hash = true

            @Comment("PCL2的苗条模型UUID匹配")
            val slim = true
        }

        @Comment("是否允许全0的UUID(Zalith) 匹配为离线")
        val zero = true

        @Comment("是否允许默认uuid生成方法 匹配为离线")
        val offline = true

        @Comment("关于PCL2启动器匹配的细节设定")
        val pcl2 = PCL2()
    }

    @ConfigSerializable
    class HostMatch {
        val start = listOf("offline", "o-")
    }

    @Comment("高级设定")
    @JvmField
    val advanced = Advanced()

    @ConfigSerializable
    class Advanced {
        // 留空，保留扩展位
    }
}

