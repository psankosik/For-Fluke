package th.co.scbprotect.search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

import th.co.scbprotect.ConsentDialog;
import th.co.scbprotect.HomeActivity;
import th.co.scbprotect.R;
import th.co.scbprotect.util.Configuration;
import th.co.scbprotect.util.GetConnector;
import th.co.scbprotect.util.PostConnector;
import th.co.scbprotect.util.SwipeListener;
import th.co.scbprotect.util.VoiceRecorder;

public class SearchActivity extends AppCompatActivity {
    AnimationDrawable micAnimation;


    private VoiceRecorder recorder = null;
    private SpeechDataHandler speechHandler = null;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<SearchResultItem> searchResultList = new ArrayList<>();
    private ArrayList<SearchResultItem> currentSearchResultList = new ArrayList<>();

    private Long tsLong = System.currentTimeMillis();
    public String oldList = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final EditText et_search = findViewById(R.id.search_et_to_search);

        speechHandler = new SpeechDataHandler();

        mRecyclerView = findViewById(R.id.search_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        //mAdapter = new SearchRecyclerAdapter(generateDummyList(20));
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mRecyclerView.setAdapter(mAdapter);

        SwipeListener listener = new SwipeListener(this) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                recorder.stop();
            }
        };
        findViewById(R.id.search_linear_layout).setOnTouchListener((View.OnTouchListener) (listener));
        findViewById(R.id.search_recycler_view).setOnTouchListener((View.OnTouchListener) (listener));

        final ConsentDialog dialog = new ConsentDialog(this);
        dialog.setCancelable(false);
        dialog.show();

        findViewById(R.id.search_bt_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search_text = et_search.getText().toString();
                ArrayList<SearchResultItem> searchResultList = new ArrayList<>();
                mRecyclerView.setAdapter(new SearchRecyclerAdapter(searchResultList, SearchActivity.this));
                findViewById(R.id.search_progress_bar).setVisibility(View.VISIBLE);
                PostConnector test_connector = new PostConnector(
                        Configuration.getDevChatService()
                        , String.format("{\"word_search\":\"%s\"}", search_text)
                        , ""
                        , new SearchDataHandler());
                test_connector.execute();
            }
        });

        findViewById(R.id.search_bt_keyboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                et_search.requestFocus();
            }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Configuration.GENGCHAN_PREF, MODE_PRIVATE);
        String email = pref.getString(Configuration.Pref.EMAIL, null);
        String token = pref.getString(Configuration.Pref.ACCESS_TOKEN, null);
        GetConnector connector = new GetConnector(Configuration.getUserId(email), token, new UserIdDataHandler());
        connector.execute();

        final CardView state1_cv = findViewById(R.id.search_cv_state1);
        final TextView state1_tv = findViewById(R.id.search_tv_state1);
        final CardView state2_cv = findViewById(R.id.search_cv_state2);
        final TextView state2_tv = findViewById(R.id.search_tv_state2);
        final CardView state3_cv = findViewById(R.id.search_cv_state3);
        final TextView state3_tv = findViewById(R.id.search_tv_state3);

        final CardView[] stateCardViews = {state1_cv, state2_cv, state3_cv};
        final TextView[] stateTextViews = {state1_tv, state2_tv, state3_tv};
        final boolean[][] stateButton = {{false, false, false}};

        final CardView pdf_cv = findViewById(R.id.search_cv_pdf);
        final TextView pdf_tv = findViewById(R.id.search_tv_pdf);
        final CardView img_cv = findViewById(R.id.search_cv_img);
        final TextView img_tv = findViewById(R.id.search_tv_img);
        final CardView vdo_cv = findViewById(R.id.search_cv_vdo);
        final TextView vdo_tv = findViewById(R.id.search_tv_vdo);

        final CardView[] typeCardViews = {pdf_cv, img_cv, vdo_cv};
        final TextView[] typeTextViews = {pdf_tv, img_tv, vdo_tv};
        final boolean[][] typeButton = {{false, false, false}};

        final boolean[] filterStatus = {false, false};
        final String[] filterValue = {"", ""};

        findViewById(R.id.search_bt_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
                mRecyclerView.setAdapter(new SearchRecyclerAdapter(searchResultList, SearchActivity.this));
                for (int i = 0; i < typeCardViews.length; i++) {
                    stateCardViews[i].setCardBackgroundColor(getResources().getColor(R.color.colorGrey3));
                    stateTextViews[i].setTextColor(getResources().getColor(R.color.colorGrey7));
                    typeCardViews[i].setCardBackgroundColor(getResources().getColor(R.color.colorGrey3));
                    typeTextViews[i].setTextColor(getResources().getColor(R.color.colorGrey7));
                }
            }
        });

        state1_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stateButton[0][0]) {
                    filterStatus[0] = false;
                    filterValue[0] = "";
                    stateButton[0] = toggleFilterUI(stateCardViews, stateTextViews, stateButton[0], state1_cv);
                    filterData(filterValue, filterStatus);
                } else {
                    filterStatus[0] = true;
                    filterValue[0] = "crime";
                    stateButton[0] = toggleFilterUI(stateCardViews, stateTextViews, stateButton[0], state1_cv);
                    filterData(filterValue, filterStatus);
                }
            }
        });

        state2_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stateButton[0][1]) {
                    filterStatus[0] = false;
                    filterValue[0] = "";
                    stateButton[0] = toggleFilterUI(stateCardViews, stateTextViews, stateButton[0], state2_cv);
                    filterData(filterValue, filterStatus);
                } else {
                    filterStatus[0] = true;
                    filterValue[0] = "politic";
                    stateButton[0] = toggleFilterUI(stateCardViews, stateTextViews, stateButton[0], state2_cv);
                    filterData(filterValue, filterStatus);
                }
            }
        });

        state3_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stateButton[0][2]) {
                    filterStatus[0] = false;
                    filterValue[0] = "";
                    stateButton[0] = toggleFilterUI(stateCardViews, stateTextViews, stateButton[0], state3_cv);
                    filterData(filterValue, filterStatus);
                } else {
                    filterStatus[0] = true;
                    filterValue[0] = "world";
                    stateButton[0] = toggleFilterUI(stateCardViews, stateTextViews, stateButton[0], state3_cv);
                    filterData(filterValue, filterStatus);
                }
            }
        });

        pdf_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typeButton[0][0]) {
                    filterStatus[1] = false;
                    filterValue[1] = "";
                    typeButton[0] = toggleFilterUI(typeCardViews, typeTextViews, typeButton[0], pdf_cv);
                    filterData(filterValue, filterStatus);
                } else {
                    filterStatus[1] = true;
                    filterValue[1] = "PDF";
                    typeButton[0] = toggleFilterUI(typeCardViews, typeTextViews, typeButton[0], pdf_cv);
                    filterData(filterValue, filterStatus);
                }
            }
        });

        img_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typeButton[0][1]) {
                    filterStatus[1] = false;
                    filterValue[1] = "";
                    typeButton[0] = toggleFilterUI(typeCardViews, typeTextViews, typeButton[0], img_cv);
                    filterData(filterValue, filterStatus);
                } else {
                    filterStatus[1] = true;
                    filterValue[1] = "IMG";
                    typeButton[0] = toggleFilterUI(typeCardViews, typeTextViews, typeButton[0], img_cv);
                    filterData(filterValue, filterStatus);
                }
            }
        });

        vdo_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typeButton[0][2]) {
                    filterStatus[1] = false;
                    filterValue[1] = "";
                    typeButton[0] = toggleFilterUI(typeCardViews, typeTextViews, typeButton[0], vdo_cv);
                    filterData(filterValue, filterStatus);
                } else {
                    filterStatus[1] = true;
                    filterValue[1] = "VDO";
                    typeButton[0] = toggleFilterUI(typeCardViews, typeTextViews, typeButton[0], vdo_cv);
                    filterData(filterValue, filterStatus);
                }
            }
        });

        final ImageView mic_button = findViewById(R.id.search_bt_mic);
        mic_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recorder == null) {
                    dialog.setCancelable(false);
                    dialog.show();
                } else {
                    recorder.stop();
                    recorder = null;

                    mic_button.setBackgroundResource(R.drawable.animation);
                    micAnimation = (AnimationDrawable) mic_button.getBackground();
                    micAnimation.stop();

                    mic_button.setImageResource(R.drawable.ic_baseline_mic_none_24);
                    mic_button.setBackgroundResource(R.drawable.ic_baseline_mic_none_24);
                }
            }
        });
    }

    private boolean[] toggleFilterUI(CardView[] cardviewsgroup, TextView[] textviewsgroup, boolean[] buttonstatus, CardView button) {
        for (int i = 0; i < cardviewsgroup.length; i++) {
            if (cardviewsgroup[i] == button) {
                if (buttonstatus[i] == true) {
                    cardviewsgroup[i].setCardBackgroundColor(getResources().getColor(R.color.colorGrey3));
                    textviewsgroup[i].setTextColor(getResources().getColor(R.color.colorGrey7));
                    buttonstatus[i] = false;
                } else {
                    cardviewsgroup[i].setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    textviewsgroup[i].setTextColor(getResources().getColor(R.color.colorWhite));
                    buttonstatus[i] = true;
                }
            } else {
                cardviewsgroup[i].setCardBackgroundColor(getResources().getColor(R.color.colorGrey3));
                textviewsgroup[i].setTextColor(getResources().getColor(R.color.colorGrey7));
                buttonstatus[i] = false;
            }
        }
        return buttonstatus;
    }

    private void filterData(String[] filterValue, boolean[] filterstate) {
        ArrayList<SearchResultItem> filterResult = new ArrayList<>();

        if (filterstate[0] && filterstate[1]) {
            for (int i = 0; i < searchResultList.size(); i += 1) {
                if (searchResultList.get(i).getFileStage().equals(String.format("\"%s\"", filterValue[0])) && searchResultList.get(i).getFileType().equals(String.format("\"%s\"", filterValue[1]))) {
                    filterResult.add(searchResultList.get(i));
                }
            }
            mRecyclerView.setAdapter(new SearchRecyclerAdapter(filterResult, SearchActivity.this));
        } else if (filterstate[0]) {
            for (int i = 0; i < searchResultList.size(); i += 1) {
                if (searchResultList.get(i).getFileStage().equals(String.format("\"%s\"", filterValue[0]))) {
                    filterResult.add(searchResultList.get(i));
                }
            }
            mRecyclerView.setAdapter(new SearchRecyclerAdapter(filterResult, SearchActivity.this));
        } else if (filterstate[1]) {
            for (int i = 0; i < searchResultList.size(); i += 1) {
                if (searchResultList.get(i).getFileType().equals(String.format("\"%s\"", filterValue[1]))) {
                    filterResult.add(searchResultList.get(i));
                }
            }
            mRecyclerView.setAdapter(new SearchRecyclerAdapter(filterResult, SearchActivity.this));
        } else {
            mRecyclerView.setAdapter(new SearchRecyclerAdapter(searchResultList, SearchActivity.this));
        }

    }

    private ArrayList<SearchResultItem> convertData(JsonArray dataArray) {
        ArrayList<SearchResultItem> searchResultList = new ArrayList<>();
        for (int i = 0; i < dataArray.size(); i += 1) {
            JsonObject obj = dataArray.get(i).getAsJsonObject();
            Log.e("PUN", obj.get("title").toString());
            searchResultList.add(new SearchResultItem(obj.get("title").toString(), obj.get("timestamp").toString(), obj.get("content").toString(), obj.get("type").toString(), obj.get("stage").toString(), obj.get("keywords").toString(), obj.get("filepath").toString()));
        }
        return searchResultList;
    }

    private ArrayList<SearchResultItem> generateDummyList(int size) {
        ArrayList<SearchResultItem> searchResultList = new ArrayList<>();

        for (int i = 0; i <= size; i++) {
            if ((i % 3) == 0) {
                searchResultList.add(new SearchResultItem(String.format("Document #%d", i), "24:00:00", "This is recycler view tutorial, and this will be document #$i preview information", "PDF", "Stage#1", "new String[0]", "https//pdf"));
            } else if ((i % 3) == 1) {
                searchResultList.add(new SearchResultItem(String.format("Image #%d", i), "24:00:00", "This is a description of image", "IMG", "Stage#1", "new String[0]", "https//image"));
            } else {
                searchResultList.add(new SearchResultItem(String.format("Video #%d", i), "24:00:00", "This is a description of video", "VDO", "Stage#1", "new String[0]", "https//video"));
            }
        }
        return searchResultList;
    }

    public void setEnableVoice(boolean value) {
        final ImageView mic_button = findViewById(R.id.search_bt_mic);

        if (value) {
            recorder = new VoiceRecorder(new VoiceHandler());
            recorder.start();
            tsLong = System.currentTimeMillis();

            runOnUiThread(new Runnable() {
                public void run() {
                    mic_button.setImageResource(R.drawable.ic_baseline_mic_on_24_2);
                    mic_button.setBackgroundResource(R.drawable.animation);
                    micAnimation = (AnimationDrawable) mic_button.getBackground();
                    micAnimation.start();
                }
            });

        } else {
            Log.e("TAG", "BIG ELSE IF");
            //micAnimation.stop();
            mic_button.setImageResource(R.drawable.ic_outline_mic_off_24);
            mic_button.setBackgroundResource(R.drawable.ic_outline_mic_off_24);
            recorder = null;
        }
        // TODO: Add a delay time before signaling the server to wipe out data or consolidate the pieces of audio data
    }

    public void setPhoneNumber(String phone) {
        Log.d("SAI", phone);
        // TODO: Post phone number to backend
    }

    private class VoiceHandler extends VoiceRecorder.Callback {
        private ByteArrayOutputStream baos = null;

        @Override
        public void onVoiceStart() {
            baos = new ByteArrayOutputStream();
        }

        @Override
        public void onVoice(byte[] data, int size) {
            try {
                baos.write(data);
            } catch (IOException ioe) {
            }
        }

        @Override
        public void onVoiceEnd() {
            SharedPreferences pref = getApplicationContext().getSharedPreferences(Configuration.GENGCHAN_PREF, MODE_PRIVATE);
            final String token = pref.getString(Configuration.Pref.ACCESS_TOKEN, "no token");

            Long currentTime = System.currentTimeMillis() - tsLong;
            String currentTimeString = currentTime.toString();

            byte[] buffer = baos.toByteArray();
            String encoded = Base64.getEncoder().encodeToString(buffer);
            String json = "{\"data\": \"" + encoded + "\", \"timestamp\":\"" + currentTimeString + "\"}";
            //Log.e("TAG", "onVoiceEnd");
            //Log.e("TAG", currentTimeString);
            try {
                //PostConnector connector = new PostConnector(Configuration.getSpeechProcessingPath(), json, token, speechHandler);
                PostConnector connector = new PostConnector(Configuration.getDevSpeechGateway() , json, token, speechHandler);
                connector.execute();
                throw new IOException();
            } catch (IOException ioe) {
            }
            baos = null;
        }
    }

    private class SpeechDataHandler implements PostConnector.DataHandler {
        @Override
        public void processData(String data) {
            final EditText search = findViewById(R.id.search_et_to_search);
            try {
                if (data.length() != 2) {

                    Log.e("TAG", "OLDLIST   " + oldList);
                    Log.e("TAG", "NEWLIST   " + data);

                    String[] a = data.substring(1, data.length() - 1).split(",");

                    for (int i = 0; i < a.length; i++) {

                        // Initial Search
                        if (a[i].equals("\"นะ\"")) {
                            final String keyword = a[i - 1].substring(1, a[i - 1].length() - 1);
                            if (oldList.contains(keyword)) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        search.setText(keyword);
                                    }
                                });
                                PostConnector test_connector = new PostConnector(
                                        Configuration.getDevChatService()
                                        , String.format("{\"word_search\":\"%s\"}", keyword)
                                        , ""
                                        , new SearchDataHandler());
                                test_connector.execute();
                            }
                        }
                        // Add keyword
                        else if (a[i].equals("\"เก่ง\"") && a[i + 1].equals("\"จัง\"")) {
                            final String additional_keyword = a[i + 2].substring(1, a[i + 2].length() - 1);
                            Log.e("TAG", "ADDWORD    " + additional_keyword);
                            Log.e("TAG", "EXISTING    " + search.getText());

                            String existing_keyword = search.getText().toString();
                            final String wordsearch = existing_keyword + " " + additional_keyword;

                            Log.e("TAG", "SEND    " + String.format("{\"word_search\":%s}", wordsearch));

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    search.setText(wordsearch);
                                }
                            });

                            PostConnector add_connector = new PostConnector(
                                    Configuration.getDevChatService()
                                    , String.format("{\"word_search\":\"%s\"}", wordsearch)
                                    , ""
                                    , new SearchDataHandler());
                            add_connector.execute();
                        }
                    }
                    oldList = data;
                }


            } catch (Exception e) {

            }
        }
    }

    private class SearchDataHandler implements PostConnector.DataHandler {
        @Override
        public void processData(String data) {
            JsonObject json = new Gson().fromJson(data, JsonObject.class);

            if (json.get("result").toString().equals("\"Try Searching Again\"")) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        findViewById(R.id.search_progress_bar).setVisibility(View.GONE);
                        findViewById(R.id.search_recycler_view).setVisibility(View.GONE);
                        findViewById(R.id.search_try_again).setVisibility(View.VISIBLE);
                        findViewById(R.id.search_filter_tab).setVisibility(View.GONE);
                    }
                });
            } else {
                final JsonArray dataArray = json.getAsJsonArray("result");
                runOnUiThread(new Runnable() {
                    public void run() {
                        searchResultList = convertData(dataArray);
                        currentSearchResultList = searchResultList;
                        mAdapter = new SearchRecyclerAdapter(searchResultList, SearchActivity.this);
                        mRecyclerView.setAdapter(mAdapter);
                        findViewById(R.id.search_progress_bar).setVisibility(View.GONE);
                        findViewById(R.id.search_recycler_view).setVisibility(View.VISIBLE);
                        findViewById(R.id.search_try_again).setVisibility(View.GONE);
                        findViewById(R.id.search_filter_tab).setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    private class UserIdDataHandler implements GetConnector.DataHandler {
        @Override
        public void processData(String data) {
            try {
                JsonObject json = new Gson().fromJson(data, JsonObject.class);
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Configuration.GENGCHAN_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(Configuration.Pref.USER_ID, json.get("id").getAsString());
                editor.commit();

                final String token = pref.getString(Configuration.Pref.ACCESS_TOKEN, null);
                GetConnector connector = new GetConnector(Configuration.getProfile(json.get("id").getAsString()), token, new UserInfo());
                connector.execute();
            } catch (Exception e) {
            }
        }
    }

    private class UserInfo implements GetConnector.DataHandler {
        @Override
        public void processData(String data) {
            try {
                JsonArray array = new Gson().fromJson(data, JsonArray.class);
                JsonObject json = array.get(0).getAsJsonObject();

                SharedPreferences pref = getApplicationContext().getSharedPreferences(Configuration.GENGCHAN_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(Configuration.Pref.NAME, json.get("name").getAsString());
                editor.commit();
            } catch (Exception e) {
            }
        }
    }
}
