package com.felipedeveloper.shareplacesapp.di

import android.content.Context
import androidx.room.Room
import com.felipedeveloper.shareplacesapp.data.db.PlacesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        PlacesDatabase::class.java,
        PlacesDatabase.DB_NAME
    ).build()

    @Singleton
    @Provides
    fun getDao(db: PlacesDatabase) = db.getDao()

}
