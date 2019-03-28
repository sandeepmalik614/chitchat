package chat.chitchat.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAkjEjuVU:APA91bH5SByqiw1c7XhEhBSKQfZvn7Ek-quUFksZ49tMppg4WDJgAJKR6T8STyz0kwEqg-tai9eSjdJ82kl8922xYBFPnFCh_m_NKqgNRLcYEKm6VuhuYhEN4Fc7FG8_ABJ6A4EJcio2"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
