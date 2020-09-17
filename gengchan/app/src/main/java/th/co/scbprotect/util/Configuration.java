package th.co.scbprotect.util;

public class Configuration {
    public static final String CONSENT_SERVICE = "https:/jinsom.com:3888";
    public static final String GENGCHAN_GATEWAY = "https:/jinsom.com:3889";
    public static final String SPEECH_GATEWAY = "https:/jinsom.com:3009";
    public static final String PA_COMPONENTS = "https:/jinsom.com:3890";
    // public static final String CHAT_ENGINE = "https:/jinsom.com:";

    public static final String DEV_CHAT_SERVICE = "http://13.229.69.212:7222/search-engine/search";
    public static final String DEV_SPEECH_GATEWAY = "http://13.229.69.212:7500/";

    // For Dev
    public static String getDevChatService(){
        return DEV_CHAT_SERVICE;
    }
    public static String getDevSpeechGateway(){
        return DEV_SPEECH_GATEWAY;
    }

    public static final String GENGCHAN_PREF = "MyPref";

    // Consent Service
    public static String getConsentPath() {
        return CONSENT_SERVICE;
    }

    // Gengchan Gateway
    public static String getAuthenticationPath() {
        return GENGCHAN_GATEWAY + "/Authentication";
    }
    public static String getChangePasswordPath() {
        return GENGCHAN_GATEWAY + "/changepassword";
    }
    public static String getFilePath() {
        return GENGCHAN_GATEWAY + "/getfile";
    }
    public static String getProfile(String userId) {
        return String.format(GENGCHAN_GATEWAY + "/gami/idinfo/%s", userId);
    }
    public static String getUserId(String email) {
        return String.format(GENGCHAN_GATEWAY + "/gami/get_user_id/%s", email);
    }

    // Speech Gateway
    public static String getSpeechProcessingPath() {
        return SPEECH_GATEWAY;
    }

    // PA Components
    public static String getPaComponents() {
        return PA_COMPONENTS;
    }

    public static class Pref
    {
        public static final String USER_ID = "USER_ID";
        public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
        public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
        public static final String EMAIL = "EMAIL";
        public static final String NAME = "NAME";
    }
}
