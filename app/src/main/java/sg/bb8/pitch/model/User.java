package sg.bb8.pitch.model;

import java.io.Serializable;

/**
 * Created by Lincoln on 07/01/16.
 */
public class User implements Serializable {
    String id;
    String name;
    String email;
    String private_room_id;
    String pending_request_id;

    public User() {
    }

    public User(String id, String name, String email, String private_room_id, String pending_request_id) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.private_room_id = private_room_id;
        this.pending_request_id = pending_request_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrivate_room_id() {
        return private_room_id;
    }

    public void setPrivate_room_id(String private_room_id) {
        this.private_room_id = private_room_id;
    }

    public String getPending_request_id() {
        return pending_request_id;
    }

    public void setPending_request_id(String pending_request_id) {
        this.pending_request_id = pending_request_id;
    }

}
