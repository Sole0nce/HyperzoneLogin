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

package icu.h2l.api.profile.skin

import com.velocitypowered.api.util.GameProfile

object ProfileSkinModel {
    const val CLASSIC = "classic"
    const val SLIM = "slim"

    fun normalize(model: String?): String {
        return if (model.equals(SLIM, ignoreCase = true)) SLIM else CLASSIC
    }
}

data class ProfileSkinSource(
    val skinUrl: String,
    val model: String = ProfileSkinModel.CLASSIC
) {
    fun normalized(): ProfileSkinSource {
        return copy(model = ProfileSkinModel.normalize(model))
    }
}

data class ProfileSkinTextures(
    val value: String,
    val signature: String? = null
) {
    /**
     * Velocity 当前只提供“必须携带完整 signature”的 `GameProfile.Property` 构造器。
     *
     * 因此：
     * 1. 有非空白签名时，才能安全构造 property；
     * 2. 无签名或空白签名时，必须由上层决定“跳过注入”或“回退到其它完整资料”，
     *    绝不能把空签名直接传给 Velocity。
     */
    fun toPropertyOrNull(): GameProfile.Property? {
        if (signature.isNullOrBlank()) {
            return null
        }
        return GameProfile.Property("textures", value, signature)
    }

    fun toProperty(): GameProfile.Property {
        return requireNotNull(toPropertyOrNull()) {
            "ProfileSkinTextures cannot be converted to GameProfile.Property without a non-blank signature"
        }
    }

    val isSigned: Boolean
        get() = !signature.isNullOrBlank()
}

