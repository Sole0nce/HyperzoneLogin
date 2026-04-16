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

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.tree.CommandNode
import com.velocitypowered.api.command.CommandSource
import icu.h2l.api.command.HyperChatBrigadierContext
import icu.h2l.api.command.HyperChatBrigadierRegistration
import icu.h2l.api.command.HyperChatCommandExecutor
import icu.h2l.api.command.HyperChatCommandInvocation
import icu.h2l.api.command.HyperChatCommandRegistration
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OfflineAuthBrigadierCommandsTest {
    @Test
    fun `register keeps descriptive password placeholders`() {
        val dispatcher = dispatcher("register", OfflineAuthBrigadierCommands.register())
        val registerNode = requireNotNull(dispatcher.root.getChild("register"))
        val passwordNode = requireNotNull(registerNode.getChild("password"))

        assertEquals(setOf("password", "arguments"), childNames(registerNode))
        assertEquals(setOf("confirmPassword"), childNames(passwordNode))
    }

    @Test
    fun `login keeps descriptive password and as placeholders`() {
        val dispatcher = dispatcher("login", OfflineAuthBrigadierCommands.login())
        val loginNode = requireNotNull(dispatcher.root.getChild("login"))
        val passwordNode = requireNotNull(loginNode.getChild("password"))
        val asNode = requireNotNull(loginNode.getChild("as"))
        val usernameNode = requireNotNull(asNode.getChild("username"))

        assertEquals(setOf("password", "as", "arguments"), childNames(loginNode))
        assertEquals(setOf("code"), childNames(passwordNode))
        assertEquals(setOf("password"), childNames(usernameNode))
    }

    @Test
    fun `email and totp keep descriptive subcommand trees`() {
        val emailDispatcher = dispatcher("email", OfflineAuthBrigadierCommands.email())
        val totpDispatcher = dispatcher("totp", OfflineAuthBrigadierCommands.totp())
        val emailNode = requireNotNull(emailDispatcher.root.getChild("email"))
        val totpNode = requireNotNull(totpDispatcher.root.getChild("totp"))

        assertEquals(setOf("add", "change", "show", "recovery", "code", "setpassword", "arguments"), childNames(emailNode))
        assertEquals(setOf("add", "enable", "confirm", "remove", "disable", "arguments"), childNames(totpNode))
        assertEquals(setOf("currentPassword"), childNames(requireNotNull(emailNode.getChild("add"))))
        assertEquals(setOf("password"), childNames(requireNotNull(totpNode.getChild("add"))))
    }

    @Test
    fun `login root executes with empty arguments so usage can be shown`() {
        val execution = execute(
            registrationName = "login",
            brigadier = OfflineAuthBrigadierCommands.login(),
            input = "login"
        )

        assertEquals("login", execution.alias)
        assertArrayEquals(emptyArray(), execution.args)
    }

    @Test
    fun `register root executes with empty arguments so usage can be shown`() {
        val execution = execute(
            registrationName = "register",
            brigadier = OfflineAuthBrigadierCommands.register(),
            input = "register"
        )

        assertEquals("register", execution.alias)
        assertArrayEquals(emptyArray(), execution.args)
    }

    @Test
    fun `register passes special character passwords unchanged`() {
        val execution = execute(
            registrationName = "register",
            brigadier = OfflineAuthBrigadierCommands.register(),
            input = "register !@#$1234 !@#$1234"
        )

        assertEquals("register", execution.alias)
        assertArrayEquals(arrayOf("!@#$1234", "!@#$1234"), execution.args)
    }

    @Test
    fun `login passes special character password unchanged`() {
        val execution = execute(
            registrationName = "login",
            brigadier = OfflineAuthBrigadierCommands.login(),
            input = "login !@#$1234"
        )

        assertEquals("login", execution.alias)
        assertArrayEquals(arrayOf("!@#$1234"), execution.args)
    }

    @Test
    fun `login as keeps explicit username syntax with special character password`() {
        val execution = execute(
            registrationName = "login",
            brigadier = OfflineAuthBrigadierCommands.login(),
            input = "login as Alice !@#$1234 123456"
        )

        assertEquals("login", execution.alias)
        assertArrayEquals(arrayOf("as", "Alice", "!@#$1234", "123456"), execution.args)
    }

    @Test
    fun `change password command keeps both special character passwords`() {
        val execution = execute(
            registrationName = "changepassword",
            brigadier = OfflineAuthBrigadierCommands.changePassword(),
            input = "changepassword old!@# new$%^"
        )

        assertEquals("changepassword", execution.alias)
        assertArrayEquals(arrayOf("old!@#", "new$%^"), execution.args)
    }

    @Test
    fun `unregister passes special character password unchanged`() {
        val execution = execute(
            registrationName = "unregister",
            brigadier = OfflineAuthBrigadierCommands.unregister(),
            input = "unregister !@#$1234"
        )

        assertEquals("unregister", execution.alias)
        assertArrayEquals(arrayOf("!@#$1234"), execution.args)
    }

    @Test
    fun `email add keeps current password and at-sign email arguments`() {
        val execution = execute(
            registrationName = "email",
            brigadier = OfflineAuthBrigadierCommands.email(),
            input = "email add !@#$1234 user@example.com user@example.com"
        )

        assertEquals("email", execution.alias)
        assertArrayEquals(
            arrayOf("add", "!@#$1234", "user@example.com", "user@example.com"),
            execution.args
        )
    }

    @Test
    fun `email setpassword keeps special character recovery password`() {
        val execution = execute(
            registrationName = "email",
            brigadier = OfflineAuthBrigadierCommands.email(),
            input = "email setpassword !@#$1234 !@#$1234"
        )

        assertEquals("email", execution.alias)
        assertArrayEquals(arrayOf("setpassword", "!@#$1234", "!@#$1234"), execution.args)
    }

    @Test
    fun `totp commands keep password arguments with special characters`() {
        val addExecution = execute(
            registrationName = "totp",
            brigadier = OfflineAuthBrigadierCommands.totp(),
            input = "totp add !@#$1234"
        )
        val removeExecution = execute(
            registrationName = "totp",
            brigadier = OfflineAuthBrigadierCommands.totp(),
            input = "totp remove !@#$1234 123456"
        )

        assertEquals("totp", addExecution.alias)
        assertArrayEquals(arrayOf("add", "!@#$1234"), addExecution.args)
        assertEquals("totp", removeExecution.alias)
        assertArrayEquals(arrayOf("remove", "!@#$1234", "123456"), removeExecution.args)
    }

    private fun execute(
        registrationName: String,
        brigadier: HyperChatBrigadierRegistration,
        input: String
    ): CapturedExecution {
        var alias: String? = null
        var args: Array<String>? = null
        val dispatcher = dispatcher(registrationName, brigadier) { commandAlias, commandArgs ->
                alias = commandAlias
                args = commandArgs
                1
            }

        val source = mockk<CommandSource>(relaxed = true)
        dispatcher.execute(input, source)

        return CapturedExecution(
            alias = requireNotNull(alias),
            args = requireNotNull(args)
        )
    }

    private fun dispatcher(
        registrationName: String,
        brigadier: HyperChatBrigadierRegistration,
        executor: (String, Array<String>) -> Int = { _, _ -> 1 }
    ): CommandDispatcher<CommandSource> {
        val registration = HyperChatCommandRegistration(
            name = registrationName,
            executor = NoopExecutor,
            brigadier = brigadier
        )
        val context = HyperChatBrigadierContext(
            registration = registration,
            visibility = { true },
            executor = { _, commandAlias, commandArgs ->
                executor(commandAlias, commandArgs)
            }
        )

        return CommandDispatcher<CommandSource>().also { dispatcher ->
            dispatcher.register(brigadier.create(context))
        }
    }

    private fun childNames(node: CommandNode<CommandSource>): Set<String> {
        return node.children.mapTo(linkedSetOf()) { it.name }
    }

    private class CapturedExecution(
        val alias: String,
        val args: Array<String>
    )

    private object NoopExecutor : HyperChatCommandExecutor {
        override fun execute(invocation: HyperChatCommandInvocation) = Unit
    }
}


