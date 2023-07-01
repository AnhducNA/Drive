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
    public static final String PARAM_SHARE_EMAIL = "email";
    public static final String PARAM_SHARE_PERMISSION = "permission";
    public static final String PARAM_USER_ACCOUNT = "account";

    //
    public static final int MAX_RECENT_FILE_DISPLAY = 5;
    public static final String AUTHORITY = "com.kma.drive.fileprovider";
    public static final int AVATAR_WIDTH_R = 100;
    public static final int AVATAR_HEIGHT_R = 100;
    public static final int ID_PARENT_DEFAULT = 0; // paraent id mac dinh 0 the hien cho no se nam ngay tai cap 1 cua folder user
    // action thao tac voi file - start
    public static final int ACTION_CREATE = 111;
    public static final int ACTION_CHANGE_NAME = 112;
    public static final int ACTION_DELETE = 113;
    // action thao tac voi file - end

    // share pref de luu jwt tren may - start
    public static String SHARE_PREF_NAME = "dataPref";
    public static String JWT = "jwt";
    // share pref de luu jwt tren may - end
    // message xac thuc het han - start
    public static final String MESSAGE_AUTHENTICATION_FAIL = "MESSAGE_AUTHENTICATION_FAIL";
    // message xac thuc het han - end

    public class FileType {
        public static final String DOC = "application/msword"; // file .dot
        public static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        public static final String DOTX = "application/vnd.openxmlformats-officedocument.wordprocessingml.template";
        public static final String DOCM = "application/vnd.ms-word.document.macroEnabled.12";
        public static final String DOTM = "application/vnd.ms-word.template.macroEnabled.12";

        public static final String XLS = "application/vnd.ms-excel"; // cac file .xlt, xlt
        public static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        public static final String XLTX = "application/vnd.openxmlformats-officedocument.spreadsheetml.template";
        public static final String XLSM = "application/vnd.ms-excel.sheet.macroEnabled.12";
        public static final String XLTM = "application/vnd.ms-excel.template.macroEnabled.12";
        public static final String XLAM = "application/vnd.ms-excel.addin.macroEnabled.12";
        public static final String XLSB = "application/vnd.ms-excel.sheet.binary.macroEnabled.12";

        public static final String PPT = "application/vnd.ms-powerpoint"; // cac file .pot, .pps, .pps, .ppa cung co type tuong tu
        public static final String PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        public static final String POTX = "application/vnd.openxmlformats-officedocument.presentationml.template";
        public static final String PPSX = "application/vnd.openxmlformats-officedocument.presentationml.slideshow";
        public static final String PPAM = "application/vnd.ms-powerpoint.addin.macroEnabled.12";
        public static final String PPTM = "application/vnd.ms-powerpoint.presentation.macroEnabled.12";
        public static final String POTM = "application/vnd.ms-powerpoint.template.macroEnabled.12";
        public static final String PPSM = "application/vnd.ms-powerpoint.slideshow.macroEnabled.12";

        public static final String PDF = "application/pdf";
        public static final String TXT = "txt";

        public static final String UNKNOWN = "unknown";
        public static final String FOLDER = "folder";
        public static final String JPG = "image/jpg";
        public static final String JPEG = "image/jpeg";
        public static final String PNG = "image/png";
    }
}
