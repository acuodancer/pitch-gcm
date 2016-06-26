package sg.bb8.pitch.app;

/**
 * Created by Darren Le on 22/06/16.
 * All code credit from: androidhive.info
 */

public class EndPoints {
    public static final String BASE_URL = "http://139.59.242.26/gcm_chat/v1";
    public static final String LOGIN = BASE_URL + "/user/login";
    public static final String USER = BASE_URL + "/user/_ID_";
    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";
}
