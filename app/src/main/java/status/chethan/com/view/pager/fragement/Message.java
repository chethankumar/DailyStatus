package status.chethan.com.view.pager.fragement;

/**
 * Created by Rahul on 2/27/2015.
 */
public class Message {

    private String userName;
    private String textMessage;

    public Message(String name, String message) {
        userName = name;
        textMessage = message;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public String getUserName() {
        return userName;
    }
}
