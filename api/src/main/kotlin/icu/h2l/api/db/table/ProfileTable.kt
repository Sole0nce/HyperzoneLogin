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

package icu.h2l.api.db.table

import org.jetbrains.exposed.sql.Table

/**
 * 游戏内档案表
 * 存储玩家的游戏内档案信息
 * name和UUID需要保持整个库中无重复
 *
 * @param prefix 表名前缀，默认为空字符串
 */
class ProfileTable(prefix: String = "") : Table("${prefix}profile") {
    /**
     * 档案ID（主键）
     * 用作和入口关联的映射
     */
    val id = uuid("id")

    /**
     * 游戏内名称
     */
    val name = varchar("name", 32).uniqueIndex()

    /**
     * 游戏内UUID
     */
    val uuid = uuid("uuid").uniqueIndex()

    override val primaryKey = PrimaryKey(id)
}