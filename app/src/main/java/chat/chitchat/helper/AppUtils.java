package chat.chitchat.helper;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.text.format.DateFormat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import chat.chitchat.R;
import chat.chitchat.notification.ApiInterface;
import chat.chitchat.notification.Data;
import chat.chitchat.notification.MyResponse;
import chat.chitchat.notification.RetrofitClient;
import chat.chitchat.notification.Sender;
import chat.chitchat.notification.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static chat.chitchat.helper.AppConstant.BASE_URL;
import static chat.chitchat.helper.AppConstant.onlineStatusTable;
import static chat.chitchat.helper.AppConstant.profileAboutTable;
import static chat.chitchat.helper.AppConstant.groupDescTable;
import static chat.chitchat.helper.AppConstant.groupImageTable;
import static chat.chitchat.helper.AppConstant.groupNameTable;
import static chat.chitchat.helper.AppConstant.profileImageTable;
import static chat.chitchat.helper.AppConstant.profileNameTable;
import static chat.chitchat.helper.AppConstant.tokenTableName;
import static chat.chitchat.helper.AppPrefrences.getFirebaseToken;

public class AppUtils {

    public static void userStatus(String status) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(onlineStatusTable).child(firebaseUser.getUid());
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);
            reference.updateChildren(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateUserName(String status) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(profileNameTable).child(firebaseUser.getUid());
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userName", status);
            reference.updateChildren(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateUserImage(String url) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(profileImageTable).child(firebaseUser.getUid());
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("imageUrl", url);
            reference.updateChildren(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateGroupName(String name, String groupId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(groupNameTable).child(groupId);
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("groupName", name);
            reference.updateChildren(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateGroupDesc(String description, String groupId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(groupDescTable).child(groupId);
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("groupDesc", description);
            reference.updateChildren(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateGroupImage(String url, String groupId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(groupImageTable).child(groupId);
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("groupImageUrl", url);
            reference.updateChildren(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateUserAbout(String status) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(profileAboutTable).child(firebaseUser.getUid());
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userStatus", status);
            reference.updateChildren(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String convertVideoTime(long millis) {
        String videoTime, hourString, minutesString, secoundsString;
        long secoundsInMilli = 1000;
        long minutesInMilli = secoundsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        long hours = millis / hoursInMilli;
        millis = millis % hoursInMilli;

        long minutes = millis / minutesInMilli;
        millis = millis % minutesInMilli;

        long secounds = millis / secoundsInMilli;

        if (hours == 0) {
            if (minutes >= 0 && minutes <= 9) {
                minutesString = "0" + minutes;
            } else {
                minutesString = String.valueOf(minutes);
            }

            if (secounds >= 0 && secounds <= 9) {
                secoundsString = "0" + secounds;
            } else {
                secoundsString = String.valueOf(secounds);
            }

            videoTime = minutesString + ":" + secoundsString;
        } else {

            if (hours >= 0 && hours <= 9) {
                hourString = "0" + hours;
            } else {
                hourString = String.valueOf(hours);
            }

            if (minutes >= 0 && minutes <= 9) {
                minutesString = "0" + minutes;
            } else {
                minutesString = String.valueOf(minutes);
            }

            if (secounds >= 0 && secounds <= 9) {
                secoundsString = "0" + secounds;
            } else {
                secoundsString = String.valueOf(secounds);
            }

            videoTime = hourString + ":" + minutesString + ":" + secoundsString;
        }

        return videoTime;
    }

    public static void updateFirebaseToken(Context context) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(tokenTableName)
                .child(firebaseUser.getUid());
        HashMap hashMap = new HashMap();
        hashMap.put("token", getFirebaseToken(context));
        reference.updateChildren(hashMap);
    }

    public static String getMyPrettyDate(long neededTimeMilis) {
        Calendar nowTime = Calendar.getInstance();
        Calendar neededTime = Calendar.getInstance();
        neededTime.setTimeInMillis(neededTimeMilis);

        if ((neededTime.get(Calendar.YEAR) == nowTime.get(Calendar.YEAR))) {

            if ((neededTime.get(Calendar.MONTH) == nowTime.get(Calendar.MONTH))) {

                if (neededTime.get(Calendar.DATE) - nowTime.get(Calendar.DATE) == 1) {
                    //here return like "Tomorrow at 12:00 AM/PM"
                    return "Tomorrow at " + DateFormat.format("hh:mm aaa", neededTime);

                } else if (nowTime.get(Calendar.DATE) == neededTime.get(Calendar.DATE)) {
                    //here return like "Today at 12:00 AM/PM"
                    return "Today at " + DateFormat.format("hh:mm aaa", neededTime);

                } else if (nowTime.get(Calendar.DATE) - neededTime.get(Calendar.DATE) == 1) {
                    //here return like "Yesterday at 12:00 AM/PM"
                    return "Yesterday at " + DateFormat.format("hh:mm aaa", neededTime);

                } else {
                    //here return like "May 31, 12:00 AM/PM"
                    return DateFormat.format("MMMM d, hh:mm aaa", neededTime).toString();
                }

            } else {
                //here return like "May 31, 12:00 AM/PM"
                return DateFormat.format("MMMM d, hh:mm aaa", neededTime).toString();
            }

        } else {
            //here return like "May 31 2010, 12:00 AM/PM" - it's a different year we need to show it
            return DateFormat.format("MMMM dd yyyy, hh:mm aaa", neededTime).toString();
        }
    }

    public static String getMyPrettyOnlyDate(long neededTimeMilis) {
        Calendar nowTime = Calendar.getInstance();
        Calendar neededTime = Calendar.getInstance();
        neededTime.setTimeInMillis(neededTimeMilis);

        if ((neededTime.get(Calendar.YEAR) == nowTime.get(Calendar.YEAR))) {

            if ((neededTime.get(Calendar.MONTH) == nowTime.get(Calendar.MONTH))) {

                if (neededTime.get(Calendar.DATE) - nowTime.get(Calendar.DATE) == 1) {
                    //here return like "Tomorrow at 12:00 AM/PM"
                    return "Tomorrow";

                } else if (nowTime.get(Calendar.DATE) == neededTime.get(Calendar.DATE)) {
                    //here return like "Today at 12:00 AM/PM"
                    return "Today";

                } else if (nowTime.get(Calendar.DATE) - neededTime.get(Calendar.DATE) == 1) {
                    //here return like "Yesterday at 12:00 AM/PM"
                    return "Yesterday";

                } else {
                    //here return like "May 31, 12:00 AM/PM"
                    return DateFormat.format("MMMM dd yyyy", neededTime).toString();
                }

            } else {
                //here return like "May 31, 12:00 AM/PM"
                return DateFormat.format("MMMM dd yyyy", neededTime).toString();
            }

        } else {
            //here return like "May 31 2010, 12:00 AM/PM" - it's a different year we need to show it
            return DateFormat.format("MMMM dd yyyy", neededTime).toString();
        }
    }

    public static void settingDialog(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.DialogTheme);
        alertDialog.setMessage("You Have To Give Permission From Your Device Setting To go in Setting Please Click on Settings Button");
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton("Go To Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }
        });
        alertDialog.show();
    }

    public static String filterNumber(String number) {
        if (number == null) {
            number = "112";
            return number;
        } else {
            number = number.replace(" ", "");
            number = number.replace("-", "");
            number = number.replace("(", "");
            number = number.replace(")", "");
            number = number.replace("+91", "");
            String upToNCharacters = number.substring(0, Math.min(number.length(), 1));
            if (number.length() == 12) {
                if (upToNCharacters.equals("91")) {
                    StringBuilder str = new StringBuilder(number);
                    str.delete(0, 2);
                    number = str.toString();
                }
            } else if (number.length() == 11) {
                StringBuilder str = new StringBuilder(number);
                str.delete(0, 1);
                number = str.toString();
            }
        }
        return number;
    }

    public static void clearNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void sendNotification(final Context context, final String header, final String currentUserId, final String receiver, final String username, final String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(tokenTableName);
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(currentUserId, R.mipmap.ic_launcher, username +
                            ": " + msg, header,
                            receiver);
                    Sender sender = new Sender(data, token.getToken());
                    ApiInterface apiInterface = RetrofitClient.getClient(BASE_URL).create(ApiInterface.class);
                    apiInterface.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200) {
                                if (response.body().success != 1) {
//                                    Toast.makeText(UserMessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static boolean isConnectionAvailable(Context ctx) {
        ConnectivityManager mManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mManager.getActiveNetworkInfo();
        return (mNetworkInfo != null) && (mNetworkInfo.isConnected());
    }

    @SuppressLint("NewApi")
    public static String getTimeAgo(long millis) {

        long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - millis);
        long hours = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - millis);
        long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - millis);

        if (seconds < 60) {
            return String.valueOf(seconds) + " seconds ago";
        } else if (minutes == 1) {
            return String.valueOf(minutes) + " minute ago";
        } else if (minutes < 60) {
            return String.valueOf(minutes) + " minutes ago";
        } else if (hours == 1) {
            return String.valueOf(hours) + " hour ago";
        } else if (hours < 24) {
            return String.valueOf(hours) + " hours ago";
        } else if (days == 1) {
            return String.valueOf(days) + " day ago";
        } else if (days < 30) {
            return String.valueOf(days) + " days ago";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aaa");
            return formatter.format(new Date(millis));
        }
    }
}