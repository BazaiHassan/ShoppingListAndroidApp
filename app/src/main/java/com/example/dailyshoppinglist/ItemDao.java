package com.example.dailyshoppinglist;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ItemDao {

    @Insert
    long addItem(ItemModel model);

    @Delete
    int deleteItem(ItemModel model);

    @Update
    int updateItem(ItemModel model);

    @Query("SELECT * FROM tbl_items")
    List<ItemModel> getAllItems();

    @Query("DELETE FROM tbl_items")
    void deleteAll();


}
