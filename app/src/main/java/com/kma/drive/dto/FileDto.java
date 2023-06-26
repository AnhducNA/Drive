package com.kma.drive.dto;

public class FileDto {
    public static final String ID = "id";
    public static final String FILE_NAME = "fileName";
    public static final String DATE = "date";
    public static final String FAVORITE = "favorite";
    public static final String TYPE = "type";
    public static final String OWNER = "owner";
    public static final String LOCATION = "location";
    public static final String PARENT_ID = "parentId";

    private Long id;
    private String fileName;
    private String date;
    private boolean favorite;
    private String type;
    private long owner;
    private String location;
    private long parentId;

    public FileDto(Long id, String fileName, String date, boolean favorite, String type, long owner, String location, long parentId) {
        this.id = id;
        this.fileName = fileName;
        this.date = date;
        this.favorite = favorite;
        this.type = type;
        this.owner = owner;
        this.location = location;
        this.parentId = parentId;
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
}
