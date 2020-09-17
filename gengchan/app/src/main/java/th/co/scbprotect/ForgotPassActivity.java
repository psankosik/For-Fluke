package th.co.scbprotect;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import th.co.scbprotect.util.Configuration;
import th.co.scbprotect.util.PostConnector;

public class ForgotPassActivity extends AppCompatActivity implements PostConnector.DataHandler{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Configuration.GENGCHAN_PREF, MODE_PRIVATE);
        final String accessToken = pref.getString(Configuration.Pref.ACCESS_TOKEN, "no token");

        findViewById(R.id.forgetpass_bt_sendlink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText edit_text_email = findViewById(R.id.forgetpass_et_email);
                String email = edit_text_email.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    edit_text_email.setError("Email required");
                    edit_text_email.requestFocus();
                    return;
                }

                String json = String.format("{\"email\":\"%s\"}",email);

                PostConnector postConnector = new PostConnector("http://13.229.69.212:7999/forgetpassword",
                        json,
                        accessToken,
                        ForgotPassActivity.this);
                postConnector.execute();

                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassActivity.this);
                builder.setTitle("Forgot Password");
                builder.setMessage("Please follow the instruction in your email");
                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
                builder.create();
                builder.show();
            }
        });
    }

    @Override
    public void processData(String data) {
        JsonObject json = new Gson().fromJson(data, JsonObject.class);

        Log.e("PUN","json.toString()");

    }
}


