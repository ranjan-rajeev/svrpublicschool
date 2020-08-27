package com.svrpublicschool.Util;

public class Constants {
    public static final String APP_NAME = "SVR";
    public static final int FIREBASE_PAGE_SIZE = 20;
    public static final int PAGE_SIZE = 20;
    public static final int THRESHOLD = 5;


    public static final int NOT_SENT = 1000;
    public static final int SENT = 1001;
    public static final int RECEIVED = 1002;
    public static final int READ = 1003;

    public static final int VIEW_STATE_PROGRESS = 2000;
    public static final int VIEW_STATE_SUCCESS = 3000;
    public static final int VIEW_STATE_FAILED = 4000;

    public static final String PREF_GCM_TOKEN = "PREF_GCM_TOKEN";
    public static final String DEFAULT_CHANNEL_ID = "MARKETING";

    //========================================================
    // INTENT FILTER ACTIONS
    //========================================================
    public static class ActionTags {
        public static final String ACTION_NETWORK = "action_network";
        public static final String ACTION_VOICE_SEARCH = "action_voice_search";
        public static final String ACTION_CAMERA_SEARCH = "action_camera_search";
        public static final String ACTION_NOTIFY = "ACTION_NOTIFY";

        public static final String VIEW_LABEL_COMPONENT = "labelComponent";
        public static final String VIEW_TEXTBOX_COMPONENT = "textboxComponent";
        public static final String VIEW_TEXT_AREA_COMPONENT = "textAreaComponent";
        public static final String VIEW_RADIO_COMPONENT = "radioComponent";
        public static final String VIEW_CHECKBOX_COMPONENT = "checkboxComponent";
        public static final String VIEW_ATTACHMENT_COMPONENT = "attachmentComponent";

    }



    //excel urls
    public static final String BASE_EXCEL_URL = "https://script.google.com/macros/s/AKfycbxOLElujQcy1-ZUer1KgEvK16gkTLUqYftApjNCM_IRTL3HSuDk/exec?id=1Ww5hvAr3X-ctNYNUYvFS3cGJ-EycWU0893Pc-KILCaY&sheet=";
    //public static final String BASE_EXCEL_URL = "https://script.google.com/macros/s/AKfycbxOLElujQcy1-ZUer1KgEvK16gkTLUqYftApjNCM_IRTL3HSuDk/exec?id=14Y3gWmBrWaPqVP6yRIHrqCwDuscI2etU&sheet=";

    public static final String KEY_VALUE_STRING_URL = BASE_EXCEL_URL + "KeyValue";
    public static final String URL_DBVERSION = BASE_EXCEL_URL + "dbversion";
    public static final String URL_FACULTY = BASE_EXCEL_URL + "faculty";
    public static final String URL_BOOKS = BASE_EXCEL_URL + "books";
    public static final String URL_DASHBOARD = BASE_EXCEL_URL + "dashboard";

    public static final String KEY_VALUE_STRING = "key_value_string";

    public static final String BANNER_URL = BASE_EXCEL_URL + "Banner";
    public static final String BANNER = "banner";
    public static final int USER_TYPE = 1; // 1- > admin

    //firebase keys
    public static final String DB_USER = "user";
    public static final String DB_CLASSES = "classes";
    public static final String DB_VERSION = "version";
    public static final String DB_SUBJECT = "subject";
    public static final String DB_CHAT = "chat";
    public static final String DB_GROUP_CHAT = "groupchat";
    public static final String DB_GROUP_DETAILS = "groupdetails";
    public static final String DB_NOTICE = "notice";

    //Sharedpref keys
    public static final String SHD_PRF_VERSION_FIREBASE = "firebase_version";

    public static final String SHD_PRF_VERSION_EXCEL = "excel_version";

    public static final String SHD_PRF_CLASSES_VERSION = "classesversion";
    public static final String SHD_PRF_CLASSES_DATA = "classesdata";

    public static final String SHD_PRF_SUBJECT_VERSION = "subjectversion";
    public static final String SHD_PRF_SUBJECT_DATA = "subjectdata";


    public static final String SHD_PRF_FACULTY_VERSION = "faculty";
    public static final String SHD_PRF_BOOKS_VERSION = "books";
    public static final String SHD_PRF_DASHBOARD_VERSION = "dashboard";
    public static final String SHD_PRF_USER_DETAILS = "userdetails";
    public static final String SHD_PRF_USER_MASTER = "usermaster";
    public static final String SHD_PRF_NOTICE_VERSION = "notice";

    public static final String SHD_PRF_REALTIME_ENABLED = "realtime";
    public static final String SHD_PRF_UPLOAD_ENABLED = "enableUpload";


    //region intent param
    public static final String INTENT_CHAT_TYPE = "chattype";
    public static final String INTENT_PARAM_USER = "user";
    public static final String INTENT_PARAM_GROUP = "group";
    public static final String INTENT_PARAM_GALLERY_ALLOWED = "galleryallowed";
    public static final String INTENT_PARAM_PDF_ALLOWED = "pdfallowed";
    public static final String INTENT_PARAM_MSG_ALLOWED = "msgallowed";

    public static final String INTENT_PARAM_FROM = "comingfrom";

    public static final String INTENT_PARAM_CHAT = "chatItem";
    public static final String INTENT_PARAM_URL = "url";
    public static final String INTENT_PARAM_TITLE = "title";
    public static final String INTENT_PARAM_BOOK = "book";
    //endregion


}
