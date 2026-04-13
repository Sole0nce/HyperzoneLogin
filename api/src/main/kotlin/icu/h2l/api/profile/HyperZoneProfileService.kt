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

package icu.h2l.api.profile

import icu.h2l.api.db.Profile
import icu.h2l.api.player.HyperZonePlayer
import java.util.UUID

/**
 * 核心层 Profile 访问入口。
 *
 * 认证子模块只应通过该服务读取或解析 Profile，
 * 不应再把 Profile 生命周期控制逻辑直接写进 [HyperZonePlayer]。
 */
interface HyperZoneProfileService {
    fun getProfile(profileId: UUID): Profile?

    /**
     * 登录流程中的“resolve”语义必须明确等价于“按既有 profileId 解析已有档案”，
     * 严禁再把“可能创建新档案”混入 resolve 语义。
     */
    fun canResolve(profileId: UUID): Boolean {
        return getProfile(profileId) != null
    }

    fun resolve(profileId: UUID): Profile {
        return getProfile(profileId)
            ?: throw IllegalStateException("未找到 Profile: $profileId")
    }

    fun getAttachedProfile(player: HyperZonePlayer): Profile?

    fun attachProfile(player: HyperZonePlayer, profileId: UUID): Profile?

    fun hasAttachedProfile(player: HyperZonePlayer): Boolean {
        return getAttachedProfile(player) != null
    }

    /**
     * 登录流程中的“create”语义必须明确等价于“以指定注册名尝试新建档案”，
     * 严禁在 create 内夹带 resolve / rename / 回退匹配等不相干流程。
     */
    fun canCreate(userName: String, uuid: UUID? = null): Boolean

    fun create(userName: String, uuid: UUID? = null): Profile

    fun attachVerifiedCredentialProfile(player: HyperZonePlayer): Profile?

    fun bindSubmittedCredentials(player: HyperZonePlayer, profileId: UUID): Profile
}

object HyperZoneProfileServiceProvider {
    @Volatile
    private var service: HyperZoneProfileService? = null

    fun bind(service: HyperZoneProfileService) {
        this.service = service
    }

    fun get(): HyperZoneProfileService = service ?: error("HyperZone profile service is not available yet")

    fun getOrNull(): HyperZoneProfileService? = service
}

