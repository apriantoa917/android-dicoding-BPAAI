package com.aprianto.dicostory.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aprianto.dicostory.data.model.StoryRemoteKeys

@Dao
interface StoryRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<StoryRemoteKeys>)

    @Query("SELECT * FROM story_remote_keys WHERE id = :id")
    suspend fun getRemoteKeysId(id: String): StoryRemoteKeys?

    @Query("DELETE FROM story_remote_keys")
    suspend fun deleteRemoteKeys()
}