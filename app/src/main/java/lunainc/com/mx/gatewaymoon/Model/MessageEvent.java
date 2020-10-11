package lunainc.com.mx.gatewaymoon.Model;

public class MessageEvent {

    public String phone;

    public String message;


    public MessageEvent() {
    }

    public MessageEvent(String phone, String message) {
        this.phone = phone;
        this.message = message;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
