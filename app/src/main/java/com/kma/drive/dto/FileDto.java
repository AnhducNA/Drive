package com.kma.drive.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FileDto {
    public static final String ID = "id";
    public static final String FILE_NAME = "fileName";
    public static final String DATE = "date";
    public static final String FAVORITE = "favorite";
    public static final String TYPE = "type";
    public static final String OWNER = "owner";
    public static final String LOCATION = "location";
    public static final String PARENT_ID = "parentId";
    public static final String SIZE = "size";
    public static final String DRIVE_PARENT_FOLDER_ID = "driveParentFolderId";
    public static final String DRIVE_FILE_ID = "driveFileId";

    @SerializedName(ID)
    @Expose
    private Long id;
    @SerializedName(FILE_NAME)
    @Expose
    private String fileName;
    @SerializedName(DATE)
    @Expose
    private String date;
    @SerializedName(FAVORITE)
    @Expose
    private boolean favorite;
    @SerializedName(TYPE)
    @Expose
    private String type;
    @SerializedName(OWNER)
    @Expose
    private long owner;
    @SerializedName(LOCATION)
    @Expose
    private String location;
    @SerializedName(PARENT_ID)
    @Expose
    private long parentId;
    @SerializedName(SIZE)
    @Expose
    private long size;

    @SerializedName(DRIVE_PARENT_FOLDER_ID)
    @Expose
    private String driveParentFolderId;

    @SerializedName(DRIVE_FILE_ID)
    @Expose
    private String driveFileId;

    public FileDto(Long id, String fileName, String date, boolean favorite, String type, long owner, String location, long parentId, long size, String driveParentFolderId, String driveFileId) {
        this.id = id;
        this.fileName = fileName;
        this.date = date;
        this.favorite = favorite;
        this.type = type;
        this.owner = owner;
        this.location = location;
        this.parentId = parentId;
        this.size = size;
        this.driveParentFolderId = driveParentFolderId;
        this.driveFileId = driveFileId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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

    public String getEntireFileName() {
        return fileName + "." + type;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDriveParentFolderId() {
        return driveParentFolderId;
    }

    public void setDriveParentFolderId(String driveParentFolderId) {
        this.driveParentFolderId = driveParentFolderId;
    }

    public String getDriveFileId() {
        return driveFileId;
    }

    public void setDriveFileId(String driveFileId) {
        this.driveFileId = driveFileId;
    }
}
