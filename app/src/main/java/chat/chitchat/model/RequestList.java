package chat.chitchat.model;

public class RequestList {

    private String request_type;

    public RequestList() {
    }

    public RequestList(String request_type) {
        this.request_type = request_type;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }
}
