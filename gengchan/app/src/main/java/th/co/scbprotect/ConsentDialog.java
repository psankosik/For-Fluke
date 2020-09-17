package th.co.scbprotect;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import th.co.scbprotect.search.SearchActivity;
import th.co.scbprotect.util.VoiceRecorder;

public class ConsentDialog extends Dialog implements View.OnClickListener {
    private SearchActivity activity = null;
    private EditText phone = null;
    private MediaPlayer player = null;
    private VoiceRecorder recorder = null;

    public ConsentDialog(@NonNull SearchActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_search_consent);

        Button yes = (Button) findViewById(R.id.dialog_consent_bt_accept);
        Button no = (Button) findViewById(R.id.dialog_consent_bt_decline);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        phone = (EditText) findViewById(R.id.dialog_consent_et_phone);
        player = MediaPlayer.create(activity, R.raw.consent_sound_effect);
        player.start();

        recorder = new VoiceRecorder(new VoiceHandler());
        recorder.start();
    }

    @Override
    public void onClick(View view) {
        player.stop();
        recorder.stop();
        switch(view.getId()) {
            case R.id.dialog_consent_bt_accept:
                if(TextUtils.isEmpty(phone.getText())) {
                    phone.setError("Phone number required");
                    phone.requestFocus();
                    return;
                }
                if(phone.getText().length() != 10 || phone.getText().charAt(0) != '0') {
                    phone.setError("Phone not valid");
                    phone.requestFocus();
                    return;
                }
                activity.setPhoneNumber(phone.getText().toString());
                activity.setEnableVoice(true);
                break;
            case R.id.dialog_consent_bt_decline:
                activity.setEnableVoice(false);
                break;
        }
        dismiss();
    }

    private class VoiceHandler extends VoiceRecorder.Callback
    {
        private ByteArrayOutputStream baos = null;
        private int seq = 0;

        @Override
        public void onVoiceStart() {
            baos = new ByteArrayOutputStream();
        }
        @Override
        public void onVoice(byte [] data, int size) {
            try {
                //System.out.println("on consent data");
                baos.write(data);
            }
            catch (IOException ioe) {
            }
        }
        @Override
        public void onVoiceEnd() {
            byte [] buffer = baos.toByteArray();
            String encoded = Base64.getEncoder().encodeToString(buffer);
            seq += 1;
            try {
                // TODO: post content here
                // TODO: each time we send the data, audio packet timeout will be added. this will prevent the audio to be kept forever
                throw new IOException();
            }
            catch (IOException ioe) {
            }
            baos = null;
        }
    }
}
