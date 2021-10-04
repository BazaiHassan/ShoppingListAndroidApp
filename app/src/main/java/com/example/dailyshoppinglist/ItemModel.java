package com.example.dailyshoppinglist;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_items")
public class ItemModel implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String itemName;

    private boolean isChecked;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.itemName);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readLong();
        this.itemName = source.readString();
        this.isChecked = source.readByte() != 0;
    }

    public ItemModel() {
    }

    protected ItemModel(Parcel in) {
        this.id = in.readLong();
        this.itemName = in.readString();
        this.isChecked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ItemModel> CREATOR = new Parcelable.Creator<ItemModel>() {
        @Override
        public ItemModel createFromParcel(Parcel source) {
            return new ItemModel(source);
        }

        @Override
        public ItemModel[] newArray(int size) {
            return new ItemModel[size];
        }
    };
}
