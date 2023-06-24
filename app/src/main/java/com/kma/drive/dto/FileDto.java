package com.kma.drive.dto;

import java.sql.Date;

public class FileDto {
    public static final String ID = "id";
    public static final String FILE_NAME = "fileName";
    public static final String DATE = "date";
    public static final String FAVORITE = "favorite";
    public static final String TYPE = "type";
    public static final String OWNER = "owner";

    private Long id;
    private String fileName;
    private Date date;
    private boolean favorite;
    private String type;
    private long owner;

    public FileDto(Long id, String fileName, Date date, boolean favorite, String type, long owner) {
        this.id = id;
        this.fileName = fileName;
        this.date = date;
        this.favorite = favorite;
        this.type = type;
        this.owner = owner;
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

    public String getEntireFileName() {
        return fileName + "." + type;
    }
}
