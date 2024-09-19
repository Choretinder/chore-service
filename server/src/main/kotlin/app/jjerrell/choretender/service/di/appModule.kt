package app.jjerrell.choretender.service.di

import app.jjerrell.choretender.service.database.ChoreDatabaseBuilder
import app.jjerrell.choretender.service.database.ChoreServiceDatabase
import org.koin.dsl.module

val appModule = module {
    single<ChoreServiceDatabase> { ChoreDatabaseBuilder.build() }
}
