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

package icu.h2l.login.auth.offline.command

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.Player
import icu.h2l.login.auth.offline.service.OfflineAuthService

class ChangePasswordCommand(
	private val authService: OfflineAuthService
) : BasePlaceholderAuthCommand("/changepassword <oldPassword> <newPassword>") {
	override fun execute(invocation: SimpleCommand.Invocation) {
		val source = invocation.source()
		if (source !is Player) {
			source.sendPlainMessage("§c该命令只能由玩家执行")
			return
		}

		val args = invocation.arguments()
		if (args.size != 2) {
			source.sendPlainMessage("§e/changepassword <oldPassword> <newPassword>")
			return
		}

		val result = authService.changePassword(source, args[0], args[1])
		source.sendPlainMessage(result.message)
	}
}
