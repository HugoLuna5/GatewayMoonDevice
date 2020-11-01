package lunainc.com.mx.gatewaymoon.Model;

public class MessageEvent {

    public String phone;

    public String bebida;

    public String estado_refill;

    public String name;


    public MessageEvent() {
    }

    public MessageEvent(String phone, String bebida, String estado_refill, String name) {
        this.phone = phone;
        this.bebida = bebida;
        this.estado_refill = estado_refill;
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBebida() {
        return bebida;
    }

    public void setBebida(String bebida) {
        this.bebida = bebida;
    }

    public String getEstado_refill() {
        return estado_refill;
    }

    public void setEstado_refill(String estado_refill) {
        this.estado_refill = estado_refill;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
