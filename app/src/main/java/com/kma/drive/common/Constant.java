package com.kma.drive.common;

public class Constant {
    public static final int NO_ANIMATION = 0;
    public static final int ACCOUNT_MIN_LENGTH = 6;
    public static final int ACCOUNT_MAX_LENGTH = 255;
    public static final int USERNAME_MAX_LENGTH = 255;
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PASSWORD_MAX_LENGTH = 6;

    //JSON Request response from server
    public static final String RESPONSE_STATUS = "status";
    public static final String RESPONSE_MESSAGE = "message";
    public static final String RESPONSE_TOKEN = "token";

    // Request params API
    public static final String PARAM_USER_ID = "userId";
    public static final String PARAM_FILE_ID = "fileId";
    public static final String PARAM_FILE = "file";

    //
    public static final int MAX_RECENT_FILE_DISPLAY = 5;
    public static final String AUTHORITY = "com.kma.drive.fileprovider";
    public static final int AVATAR_WIDTH_R = 100;
    public static final int AVATAR_HEIGHT_R = 100;

    public class FileType {
        public static final String PDF = "pdf";
        public static final String DOC = "doc";
        public static final String TXT = "txt";
        public static final String PPTX = "pptx";
        public static final String UNKNOWN = "unknown";
        public static final String FOLDER = "folder";
        public static final String JPG = "jpg";
        public static final String JPEG = "jpeg";
        public static final String PNG = "png";
    }
}
