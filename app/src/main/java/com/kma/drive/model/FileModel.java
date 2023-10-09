package com.kma.drive.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.kma.drive.util.Util;

import java.sql.Date;
import java.text.ParseException;
import java.util.Calendar;

public class FileModel implements Parcelable {
    private long id;
    private String fileName;
    private Date date;
    private boolean favorite;
    private String type;
    private long owner;
    private String location;
    private long parentId;
    private boolean shared;
    private long size;

    public FileModel(long id, String fileName, Date date, boolean favorite, String type, long owner, String location, long parentId, boolean shared, long size) {
        this.id = id;
        this.fileName = fileName;
        this.date = date;
        this.favorite = favorite;
        this.type = type;
        this.owner = owner;
        this.location = location;
        this.parentId = parentId;
        this.shared = shared;
        this.size = size;
    }

    protected FileModel(Parcel in) {
        id = in.readLong();
        try {
            date = Util.convertStringToDate(in.readString());
        } catch (ParseException e) {
            date = new Date(Calendar.getInstance().getTimeInMillis());
        }
        fileName = in.readString();
        favorite = in.readByte() != 0;
        type = in.readString();
        owner = in.readLong();
        location = in.readString();
        parentId = in.readLong();
        shared = in.readByte() != 0;
        size = in.readLong();
    }

    public static final Creator<FileModel> CREATOR = new Creator<FileModel>() {
        @Override
        public FileModel createFromParcel(Parcel in) {
            return new FileModel(in);
        }

        @Override
        public FileModel[] newArray(int size) {
            return new FileModel[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(date.toString());
        parcel.writeString(fileName);
        parcel.writeByte((byte) (favorite ? 1 : 0));
        parcel.writeString(type);
        parcel.writeLong(owner);
        parcel.writeString(location);
        parcel.writeLong(parentId);
        parcel.writeByte((byte) (shared ? 1 : 0));
        parcel.writeLong(size);
    }
}
