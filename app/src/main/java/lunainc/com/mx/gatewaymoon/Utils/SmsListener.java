package lunainc.com.mx.gatewaymoon.Utils;

public interface SmsListener {
    public void messageReceived(String messageText, String phone);
}
