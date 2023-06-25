package com.kma.drive.callback;

import com.kma.drive.model.FileModel;

public interface AwareDataStateChange {
    void onDataLoadingFinished();
    void onDataStateChanged();
    void onDataDeleted(FileModel fileModel);
    void onDataCreated(FileModel fileModel);
}
