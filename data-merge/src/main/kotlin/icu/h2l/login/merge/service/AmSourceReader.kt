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

package icu.h2l.login.merge.service

import icu.h2l.login.merge.config.MergeAmConfig
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager


data class AuthMeRow(
    val username: String,
    val realName: String?,
    val password: String?
)

class AmSourceReader(
    private val dataDirectory: Path,
    private val config: MergeAmConfig
) {
    fun readAuthMeRows(): List<AuthMeRow> {
        connect().use { connection ->
            return readAuthMeRows(connection)
        }
    }

    private fun connect(): Connection {
        val source = config.source
        return when (source.type.uppercase()) {
            "MYSQL" -> {
                Class.forName("com.mysql.cj.jdbc.Driver")
                val mysql = source.mysql
                val url = "jdbc:mysql://${mysql.host}:${mysql.port}/${mysql.database}?${mysql.parameters}"
                DriverManager.getConnection(url, mysql.username, mysql.password)
            }

            "SQLITE", "SQLITE_DB" -> {
                Class.forName("org.sqlite.JDBC")
                val sqlite = source.sqlite
                val url = if (sqlite.jdbcUrl.isNotBlank()) {
                    sqlite.jdbcUrl
                } else {
                    val configuredPath = Paths.get(sqlite.path)
                    val absolutePath = if (configuredPath.isAbsolute) {
                        configuredPath.normalize()
                    } else {
                        dataDirectory.toAbsolutePath().normalize().resolve(configuredPath).normalize()
                    }
                    val dbPath = absolutePath.toString().replace('\\', '/')
                    val extraParams = sqlite.parameters.trim()
                    if (extraParams.isBlank()) {
                        "jdbc:sqlite:$dbPath"
                    } else {
                        "jdbc:sqlite:$dbPath?$extraParams"
                    }
                }
                DriverManager.getConnection(url)
            }

            else -> {
                throw IllegalArgumentException("不支持的源数据库类型: ${source.type}")
            }
        }
    }

    private fun readAuthMeRows(connection: Connection): List<AuthMeRow> {
        val sql = "SELECT username, realname, password FROM ${config.tables.authMeTable}"
        connection.prepareStatement(sql).use { statement ->
            statement.executeQuery().use { rs ->
                val result = mutableListOf<AuthMeRow>()
                while (rs.next()) {
                    result.add(
                        AuthMeRow(
                            username = rs.getString("username") ?: "",
                            realName = rs.getString("realname"),
                            password = rs.getString("password")
                        )
                    )
                }
                return result
            }
        }
    }
}
