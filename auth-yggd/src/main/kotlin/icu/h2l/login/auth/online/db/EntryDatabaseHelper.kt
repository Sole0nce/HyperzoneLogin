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

package icu.h2l.login.auth.online.db

import icu.h2l.api.db.HyperZoneDatabaseManager
import icu.h2l.api.log.warn
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.util.*

/**
 * Entry 表数据操作帮助类
 */
class EntryDatabaseHelper(
    private val databaseManager: HyperZoneDatabaseManager,
    private val entryTableManager: EntryTableManager
) {
    fun findEntryByNameAndUuid(entryId: String, name: String, uuid: UUID): UUID? {
        val entryTable = entryTableManager.getEntryTable(entryId) ?: return null

        return databaseManager.executeTransaction {
            entryTable.selectAll().where { (entryTable.name eq name) and (entryTable.uuid eq uuid) }
                .map { it[entryTable.pid] }
                .firstOrNull()
        }
    }

    fun createEntry(entryId: String, name: String, uuid: UUID, pid: UUID): Boolean {
        val entryTable = entryTableManager.getEntryTable(entryId) ?: return false

        return try {
            databaseManager.executeTransaction {
                entryTable.insert {
                    it[entryTable.name] = name
                    it[entryTable.uuid] = uuid
                    it[entryTable.pid] = pid
                }
            }
            true
        } catch (e: Exception) {
            warn { "创建入口记录失败: ${e.message}" }
            false
        }
    }

    fun updateEntryName(entryId: String, oldUuid: UUID, newName: String): Boolean {
        val entryTable = entryTableManager.getEntryTable(entryId) ?: return false

        return try {
            databaseManager.executeTransaction {
                entryTable.update({ entryTable.uuid eq oldUuid }) {
                    it[name] = newName
                }
            } > 0
        } catch (e: Exception) {
            warn { "更新入口名称失败: ${e.message}" }
            false
        }
    }

    fun verifyEntry(entryId: String, name: String, uuid: UUID): Boolean {
        val entryTable = entryTableManager.getEntryTable(entryId) ?: return false

        return databaseManager.executeTransaction {
            entryTable.selectAll().where { (entryTable.name eq name) and (entryTable.uuid eq uuid) }.count() > 0
        }
    }
}
