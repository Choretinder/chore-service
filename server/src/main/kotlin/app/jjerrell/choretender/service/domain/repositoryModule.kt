/*
 * Choretinder
 * Copyright (C) 2024  Jacob Jerrell (@jjerrell)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package app.jjerrell.choretender.service.domain

import app.jjerrell.choretender.service.domain.repository.*
import app.jjerrell.choretender.service.domain.repository.FamilyRepository
import app.jjerrell.choretender.service.domain.repository.UserRepository
import io.ktor.util.logging.*
import org.koin.dsl.module

private class RepositoryLogger : Logger by KtorSimpleLogger("RepositoryLogger")

val repositoryModule = module {
    single<Logger> { RepositoryLogger() }
    factory<IUserRepository> { UserRepository(db = get(), logger = get()) }
    factory<IFamilyRepository> { FamilyRepository(db = get(), logger = get()) }
    factory<IChoreRepository> { ChoreRepository(db = get(), logger = get()) }
}
