package com.arash.altafi.chatgptsimple.data.local

import androidx.room.*

@Dao
interface MessengerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDialog(dialogEntity: DialogEntity)

    @Query("SELECT * FROM tbl_dialog")
    fun getAllDialog(): List<DialogEntity>

    @Delete
    fun deleteDialog(dialogEntity: DialogEntity)

    @Update
    fun updateDialog(dialogEntity: DialogEntity)

}