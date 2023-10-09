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
    private List<String> strFiles;
    private boolean dataFetching; // cho biet data co dang duoc fetch tu phia server ve khong

    private UserSession() {
        files = new ArrayList<>();
        strFiles = new ArrayList<>();
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

    public List<String> getStrFiles() {
        return strFiles;
    }

    public boolean isDataFetching() {
        return dataFetching;
    }

    public void setDataFetching(boolean dataFetching) {
        this.dataFetching = dataFetching;
    }

    public FileModel createNewFile(String fileName, String type) {
        return createNewFile(fileName, type, 0);
    }

    public FileModel createNewFile(String fileName, String type, long size) {
        return new FileModel(0, fileName, new Date(Calendar.getInstance().getTimeInMillis()), false, type,
                user.getId(), null, Constant.ID_PARENT_DEFAULT, false, size);
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
                null, Constant.ID_PARENT_DEFAULT, false, 0);
    }

    public FileModel getFileModelByFileName(String fileName) {
        for (FileModel fileModel: files) {
            if (fileModel.getFileName().equals(fileName)) {
                return fileModel;
            }
        }

        return null;
    }

    public void updateStrFile(FileModel fileModel, int action) {
        switch (action) {
            case Constant.ACTION_CREATE: {
                strFiles.add(fileModel.getFileName());
                break;
            }
            case Constant.ACTION_DELETE: {
                for (String s: strFiles) {
                    if (s.equals(fileModel.getFileName())) {
                        strFiles.remove(s);
                        break;
                    }
                }
                break;
            }
            case Constant.ACTION_CHANGE_NAME: {
                int index = files.indexOf(fileModel);
                strFiles.set(index, fileModel.getFileName());
                break;
            }
        }
    }

    public void clearSession() {
        this.user = null;
        this.files = null;
        this.strFiles = null;
        userSession = null;
    }
}
