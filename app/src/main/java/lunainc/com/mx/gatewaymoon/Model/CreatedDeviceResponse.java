package lunainc.com.mx.gatewaymoon.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreatedDeviceResponse extends ResponseDefault {

    @Expose
    @SerializedName("device_id")
    private String device_id;


    public CreatedDeviceResponse() {
    }

    public CreatedDeviceResponse(String device_id) {
        this.device_id = device_id;
    }

    public CreatedDeviceResponse(String status, String message, String device_id) {
        super(status, message);
        this.device_id = device_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
