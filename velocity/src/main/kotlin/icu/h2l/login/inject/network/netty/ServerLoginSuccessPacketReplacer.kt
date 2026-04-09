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

package icu.h2l.login.inject.network.netty

import com.velocitypowered.api.util.GameProfile
import com.velocitypowered.proxy.connection.MinecraftConnection
import com.velocitypowered.proxy.protocol.packet.ServerLoginSuccessPacket
import icu.h2l.api.log.debug
import icu.h2l.api.log.error
import icu.h2l.login.HyperZoneLoginMain
import icu.h2l.login.manager.HyperZonePlayerManager
import icu.h2l.login.player.ForwardingGameProfileSupport
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise

class ServerLoginSuccessPacketReplacer(
    private val channel: Channel
) : ChannelOutboundHandlerAdapter() {
    private lateinit var mcConnection: MinecraftConnection

    override fun write(ctx: ChannelHandlerContext, msg: Any?, promise: ChannelPromise?) {
        try {
            initFields(ctx)

            if (!HyperZoneLoginMain.getMiscConfig().enableReplaceGameProfile || msg !is ServerLoginSuccessPacket) {
                super.write(ctx, msg, promise)
                return
            }

            val hyperPlayer = runCatching {
                HyperZonePlayerManager.getByChannel(channel)
            }.getOrNull()

            if (hyperPlayer == null) {
                debug {
                    "[ProfileSkinFlow] login success passthrough: no hyper player for channel=$channel"
                }
                super.write(ctx, msg, promise)
                return
            }
//        这里不做自己看不见自己皮肤，客户端UUID会错误
            val baseProfile = ForwardingGameProfileSupport.resolveBaseProfile(hyperPlayer, true)
            val mergedProfile = ForwardingGameProfileSupport.resolveProfile(hyperPlayer, false)
            msg.uuid = mergedProfile.id
            msg.username = mergedProfile.name
            msg.properties = mergedProfile.properties

            debug {
                "[ProfileSkinFlow] login success profile patched: player=${hyperPlayer.userName}, baseSource=${
                    ForwardingGameProfileSupport.resolveBaseProfileSource(
                        hyperPlayer,
                        true
                    )
                }, base=${describeProfile(baseProfile)}, final=${describeProfile(mergedProfile)}, channel=$channel"
            }

            retire(ctx, "server login success patched")
            super.write(ctx, msg, promise)
        } catch (throwable: Throwable) {
            error(throwable) { "ServerLoginSuccessPacketReplacer write failed: ${throwable.message}" }
            try {
                ctx.fireExceptionCaught(throwable)
            } catch (_: Throwable) {
                // ignore
            }
        }
    }

    private fun initFields(ctx: ChannelHandlerContext) {
        if (::mcConnection.isInitialized) {
            return
        }
        val conn = ctx.channel().pipeline().get(MinecraftConnection::class.java) ?: return
        this.mcConnection = conn
    }

    private fun retire(ctx: ChannelHandlerContext, reason: String) {
        debug {
            "[ServerLoginSuccessPacketReplacer] retire: reason=$reason, channel=$channel"
        }
        ctx.pipeline().remove(this)
    }

    private fun describeProfile(profile: GameProfile): String {
        val hasTextures = profile.properties.any { it.name.equals("textures", ignoreCase = true) }
        return "id=${profile.id}, name=${profile.name}, properties=${profile.properties.size}, textures=$hasTextures"
    }
}

