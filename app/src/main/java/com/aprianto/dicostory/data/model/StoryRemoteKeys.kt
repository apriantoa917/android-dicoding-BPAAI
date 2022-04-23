package com.aprianto.dicostory.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story_remote_keys")
data class StoryRemoteKeys (
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)