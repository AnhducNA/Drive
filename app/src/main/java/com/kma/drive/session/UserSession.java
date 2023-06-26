package com.kma.drive.session;

import com.kma.drive.common.Constant;
import com.kma.drive.dto.UserDto;
import com.kma.drive.model.FileModel;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserSession {

    private static UserSession userSession;
    private UserDto user;
    private List<FileModel> files;
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

    public List<FileModel> getFiles() {
        return files;
    }

    public void setFiles(List<FileModel> files) {
        this.files = files;
    }

    public boolean isDataFetching() {
        return dataFetching;
    }

    public void setDataFetching(boolean dataFetching) {
        this.dataFetching = dataFetching;
    }

    public FileModel createNewFile(String fileName, String type) {
        return new FileModel(null, fileName, new Date(Calendar.getInstance().getTimeInMillis()), false, type,
                user.getId(), null, Constant.ID_PARENT_DEFAULT);
    }

    public void getFileChildren(long parentId, List<FileModel> dest, boolean addFiles) {
        for (FileModel fileModel: getFiles()) {
            if (fileModel.getParentId() == parentId) {
                if (!addFiles && !fileModel.getType().equals(Constant.FileType.FOLDER)) {
                    continue;
                }
                dest.add(fileModel);
            }
        }
    }

    public FileModel getRootFolder() {
        return new FileModel(0L, null, new Date(Calendar.getInstance().getTimeInMillis()), false, Constant.FileType.FOLDER, user.getId(),
                null, Constant.ID_PARENT_DEFAULT);
    }
}
