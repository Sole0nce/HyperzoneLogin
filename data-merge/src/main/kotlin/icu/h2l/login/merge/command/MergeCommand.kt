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

package icu.h2l.login.merge.command

import com.velocitypowered.api.command.SimpleCommand
import java.util.concurrent.atomic.AtomicBoolean

class MergeCommand(
    private val runMlMigration: () -> String,
    private val runAmMigration: () -> String
) : SimpleCommand {
    private val running = AtomicBoolean(false)

    override fun execute(invocation: SimpleCommand.Invocation) {
        val sender = invocation.source()
        val args = invocation.arguments()

        if (args.isEmpty()) {
            sender.sendPlainMessage("§e/hzl-merge ml")
            sender.sendPlainMessage("§e/hzl-merge am")
            return
        }

        val subCommand = args[0].lowercase()
        if (subCommand != "ml" && subCommand != "am") {
            sender.sendPlainMessage("§c未知子命令: ${args[0]}")
            sender.sendPlainMessage("§e可用子命令: ml, am")
            return
        }

        if (!running.compareAndSet(false, true)) {
            sender.sendPlainMessage("§c迁移正在执行中，请稍后再试")
            return
        }

        try {
            val summary = when (subCommand) {
                "ml" -> {
                    sender.sendPlainMessage("§e开始执行 ML 迁移，请稍候...")
                    runMlMigration()
                }

                else -> {
                    sender.sendPlainMessage("§e开始执行 AUTHME 迁移，请稍候...")
                    runAmMigration()
                }
            }

            sender.sendPlainMessage("§a迁移完成: $summary")
            sender.sendPlainMessage(
                "§a详细日志已输出到 ${if (subCommand == "ml") "merge-ml.log" else "merge-am.log"}"
            )
        } catch (ex: Exception) {
            sender.sendPlainMessage("§c迁移失败: ${ex.message}")
        } finally {
            running.set(false)
        }
    }

    override fun hasPermission(invocation: SimpleCommand.Invocation): Boolean {
        return invocation.source().hasPermission("hyperzonelogin.admin")
    }
}
