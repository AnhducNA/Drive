package com.kma.drive.session;

import com.kma.drive.dto.FileDto;
import com.kma.drive.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

public class UserSession {

    private static UserSession userSession;
    private UserDto user;
    private List<FileDto> files;
    private boolean dataFetching; // cho biet data co dang duoc fetch tu phia server ve khong

    private UserSession() {
        files = new ArrayList<>();
        dataFetching = false;
    }

    public static UserSession getInstance() {
        if (userSession == null) {
            userSession = new UserSession();
        }
        return userSession;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public List<FileDto> getFiles() {
        return files;
    }

    public void setFiles(List<FileDto> files) {
        this.files = files;
    }

    public boolean isDataFetching() {
        return dataFetching;
    }

    public void setDataFetching(boolean dataFetching) {
        this.dataFetching = dataFetching;
    }
}
