package th.co.scbprotect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import th.co.scbprotect.search.SearchActivity;
import th.co.scbprotect.util.Configuration;
import th.co.scbprotect.util.PostConnector;

public class LoginActivity extends AppCompatActivity implements PostConnector.DataHandler {

    @Override
    public void processData(String data) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.login_tv_forget_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassActivity.class);
                startActivity(intent);
            }
        });

        final Button login_bt = (Button) findViewById(R.id.login_bt_login);
        login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText edit_text_email = (EditText) findViewById(R.id.login_et_email);
                EditText edit_text_password = (EditText) findViewById(R.id.login_et_password);

                String email = edit_text_email.getText().toString().trim();
                String password = edit_text_password.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    edit_text_email.setError("Email required");
                    edit_text_email.requestFocus();
                    return;
                }
                /*
                if (!email.endsWith("@scbprotect.co.th")){
                    edit_text_email.setError("Invalid Email");
                    edit_text_email.requestFocus();
                    return;
                }
                */
                if(TextUtils.isEmpty(password)) {
                    edit_text_password.setError("Password required");
                    edit_text_password.requestFocus();
                    return;
                }


                login_bt.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                findViewById(R.id.login_progress_bar).setVisibility(View.VISIBLE);
                findViewById(R.id.login_cv_login_fail).setVisibility(View.GONE);

                String json = String.format("{\"email\":\"%s\",\"password\":\"%s\"}",email,password);

                PostConnector postConnector = new PostConnector(
                        Configuration.getAuthenticationPath(),
                        json,
                        "",
                        (PostConnector.DataHandler) new postHandlerOne());
                postConnector.execute();

                SharedPreferences pref = getApplicationContext().getSharedPreferences(Configuration.GENGCHAN_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(Configuration.Pref.EMAIL, email);
                editor.commit();
            }
        });
    }

    public final class postHandlerOne implements PostConnector.DataHandler {
        @Override
        public void processData(String data) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences(Configuration.GENGCHAN_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            JsonObject json = new Gson().fromJson(data, JsonObject.class);

            if (json.get("access_token") != null){
                editor.putString(Configuration.Pref.ACCESS_TOKEN, json.get("access_token").getAsString());
                editor.putString(Configuration.Pref.REFRESH_TOKEN, json.get("refresh_token").getAsString());
                editor.commit();

                Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                startActivity(intent);
                finishAffinity();
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        findViewById(R.id.login_progress_bar).setVisibility(View.GONE);
                        findViewById(R.id.login_cv_login_fail).setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }
}

