package chat.chitchat.model;

public class ChatList {

    private String id;
    private String  time;

    public ChatList(String id, String time) {
        this.id = id;
        this.time = time;
    }

    public ChatList() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
