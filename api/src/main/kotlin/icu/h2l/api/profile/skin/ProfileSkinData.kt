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
    fun toProperty(): GameProfile.Property {
        return GameProfile.Property("textures", value, signature)
    }

    val isSigned: Boolean
        get() = !signature.isNullOrBlank()
}

