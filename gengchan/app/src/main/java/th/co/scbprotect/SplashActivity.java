package th.co.scbprotect;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import th.co.scbprotect.search.SearchActivity;
import th.co.scbprotect.util.Configuration;
import th.co.scbprotect.util.PostConnector;

public class SplashActivity extends AppCompatActivity implements PostConnector.DataHandler {
    private final int STANDARD_PERMISSIONS_REQUEST = 101;
    private Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler = new Handler();
        requestPermissions(new String[] {Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO}, STANDARD_PERMISSIONS_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String [] permissions, int [] results) {
        switch (requestCode) {
            case STANDARD_PERMISSIONS_REQUEST:
            {
                boolean denies = false;
                for(int i = 0; i < results.length; i += 1) {
                    if (results[i] != PackageManager.PERMISSION_GRANTED) {
                        denies = true;
                        break;
                    }
                }
                if (denies) {
                    Toast.makeText(SplashActivity.this, "Fatal error: All requested permissions must be granted to use the application", Toast.LENGTH_LONG).show();
                }
                else {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Configuration.GENGCHAN_PREF, MODE_PRIVATE);
                    final String accessToken = pref.getString(Configuration.Pref.ACCESS_TOKEN, "no token");

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PostConnector postConnector = new PostConnector(
                                    Configuration.getAuthenticationPath(),
                                    "",
                                    accessToken,
                                    (PostConnector.DataHandler) SplashActivity.this);
                            postConnector.execute();
                        }
                    }, 100);
                }
            }
            break;
        }
    }

    @Override
    public void processData(String data) {
        JsonObject json = new Gson().fromJson(data, JsonObject.class);
        String error = json.get("status") != null ? json.get("status").getAsString() : "error";

        //if (false) {
        if (error.compareTo("error") != 0) {
            Intent intent = new Intent(SplashActivity.this, SearchActivity.class);
            startActivity(intent);
            finishAffinity();
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finishAffinity();
        }

    }
}