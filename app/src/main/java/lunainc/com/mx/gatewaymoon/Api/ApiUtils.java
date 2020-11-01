package lunainc.com.mx.gatewaymoon.Api;

public class ApiUtils {

    private ApiUtils() {}

    private static final String BASE_URL = "https://hugofest.fun";


    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

}
