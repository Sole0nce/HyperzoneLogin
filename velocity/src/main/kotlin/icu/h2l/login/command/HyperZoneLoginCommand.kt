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

package icu.h2l.login.command

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.Player
import icu.h2l.login.HyperZoneLoginMain
import icu.h2l.login.manager.HyperZonePlayerManager

class HyperZoneLoginCommand : SimpleCommand {
    override fun execute(invocation: SimpleCommand.Invocation) {
        val args = invocation.arguments()
        val sender = invocation.source()
        if (args.size == 0) {
            sender.sendPlainMessage("§e/hzl reload")
            return
        }
        if (args[0].equals("reload", ignoreCase = true)) {
            sender.sendPlainMessage("§aReloaded!")
            return
        } else if (args[0].equals("re", ignoreCase = true)) {
            if (sender !is Player) {
                sender.sendPlainMessage("§c该命令只能由玩家执行")
                return
            }

            sender.sendPlainMessage("§e开始重新认证...")
            HyperZoneLoginMain.getInstance().triggerLimboAuthForPlayer(sender)
            return
        } else if (args[0].equals("uuid", ignoreCase = true)) {
            if (sender !is Player) {
                sender.sendPlainMessage("§c该命令只能由玩家执行")
                return
            }

            val proxyPlayer = sender
            val hyperZonePlayer = HyperZonePlayerManager.getByPlayer(proxyPlayer)
            val profile = hyperZonePlayer.getDBProfile()

            sender.sendPlainMessage("§e[ProxyPlayer] name=${proxyPlayer.username} uuid=${proxyPlayer.uniqueId}")
            sender.sendPlainMessage("§e[HyperZonePlayer] verified=${hyperZonePlayer.isVerified()} canRegister=${hyperZonePlayer.canRegister()}")
            if (profile != null) {
                sender.sendPlainMessage("§e[Profile] id=${profile.id} name=${profile.name} uuid=${profile.uuid}")
            } else {
                sender.sendPlainMessage("§e[Profile] null")
            }
            return
        }
    }

    override fun hasPermission(invocation: SimpleCommand.Invocation): Boolean {
        return invocation.source().hasPermission("hyperzonelogin.admin")
    }
} 