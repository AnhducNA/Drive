package com.kma.drive.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.kma.drive.common.Constant;
import com.kma.drive.model.FileModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class FileUtil {
    private static String dataDirPath = "";

    /**
     * Xoa file hoac folder
     * @param fileToDel
     * @return
     */
    public static boolean deleteFileOrFolder(File fileToDel) {
        if (fileToDel.exists()) {
            if (fileToDel.isDirectory()) {
                File[] files = fileToDel.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            deleteFileOrFolder(file);
                        } else {
                            file.delete();
                        }
                    }
                }
            }
            return fileToDel.delete();
        } else {
            return true;
        }
    }

    public static File getFileFromUri(Context context, Uri uri) {
        String name = getFileNameFromUri(context, uri);
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            File file = new File(context.getFilesDir(), name);

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();

            return file;
        } catch (IOException e) {
            return new File(uri.getPath());
        }
    }

    public static String getFileNameFromUri(Context context, Uri uri) {
        String fileName = null;

        if (uri != null) {
            Cursor cursor = null;
            try {
                String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                    fileName = cursor.getString(columnIndex);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return fileName;
    }

    public static String getPathFromUri(Context context, Uri uri) {
        String filePath = "";
        if (uri != null) {
            if (uri.getScheme().equals("content")) {
                Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
                    filePath = cursor.getString(columnIndex);
                    cursor.close();
                }
            } else if (uri.getScheme().equals("file")) {
                filePath = uri.getPath();
            }
        }
        return filePath;
    }

    /**
     * Kiem tra ten file co trung voi cac file trong cung thu muc khong, neu khong thi return ve name, nguoc lai tang
     * bien danh dau len va return ve ten moi
     * @param name
     * @param parentId
     * @param files
     * @return
     */
    public static String checkNameExistedOrCreateNewOne(String name, long parentId, List<FileModel> files) {
        for (FileModel fileModel: files) {
            if (fileModel.getFileName().equals(name) &&
                    fileModel.getParentId() == parentId) {
                //TODO tam thoi chua dung den ham nay, ve sau phat trien them se can dung
            }
        }
        return name;
    }

    public static String getFileNameExceptExtensionPart(String name) {
        int extensionIndex = name.lastIndexOf(".");
        if (extensionIndex == -1) {
            return name;
        }
        return name.substring(0, extensionIndex);
    }

    /**
     * Kiem tra ten co ton tai thu muc khong
     * @param fileName
     * @param parentId
     * @param fileModels
     * @return
     */
    public static boolean checkFileNameExisted(String fileName, long parentId, long ownerId, List<FileModel> fileModels) {
        for (FileModel fileModel: fileModels) {
            if (fileModel.getParentId() == parentId && fileModel.getFileName().equals(fileName)
                    && fileModel.getOwner() == ownerId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Xoa file va neu la folder thi xoa het file con
     * @param file
     * @param list
     * @return
     */
    public static void deleteFile(FileModel file, List<FileModel> list) {
        if (file.getType().equals(Constant.FileType.FOLDER)) {
            for (int i=0; i < list.size(); i++) {
                final FileModel temp = list.get(i);
                if (temp.getParentId() == file.getId()) {
                    if (temp.getType().equals(Constant.FileType.FOLDER)) {
                        deleteFile(temp, list);
                    } else {
                        list.remove(temp);
                    }
                }
            }
        }
        list.remove(file);
    }

    public static void createFakeFileForRequestOneTime(Context context) {
        dataDirPath = context.getFilesDir().getPath();
        File file = new File(dataDirPath, "fake.txt");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                //Ignore
            }
        }
    }

    public static File getFakeFile() {
        File file = new File(dataDirPath, "fake.txt");
        return file;
    }
}
