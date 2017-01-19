package app.trieuphumobi.offline.getdata;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.HexagonImageView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.trieuphumobi.R;
import app.trieuphumobi.offline.play.OfflineMoneyActivity;
import data.LinkData;

public class OfflineGetDataActivity extends AppCompatActivity implements OfflineGetDataView{

    ImageView imgLoad;
    private Animation anim;
    HexagonImageView imgAvatar;
    ImageView imgKhach;
    TextView tvNameUser;
    private ArrayList<String> category = new ArrayList<>();
    String user_id = "", user_name = "", user_url = "", user_gender = "";
    Boolean check_login = false;
    private int hlLv = 0;
    private int hlMoney = 0;
    private int hlScore = 0;
    private TextView tvMoney, tvLv, tvScore;
    private FirebaseAnalytics mFirebaseAnalytics;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    private OfflineGetDataPresenter mOfflineGetDataPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_load);
//        settings = getSharedPreferences(SplashActivity.PREFS, MODE_PRIVATE);
//        editor = settings.edit();
        disableNotification();

//        showAdsVmax();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "GooglePlay");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Loading data");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        hlLv = getSharedPreferences("TPMBSHAFGG", MODE_PRIVATE).getInt("tpmbLvhl", 0);
        hlMoney = getSharedPreferences("TPMBSHAFGG", MODE_PRIVATE).getInt("tpmbMoneyhl", 0);
        hlScore = getSharedPreferences("TPMBSHAFGG", MODE_PRIVATE).getInt("tpmbScorehl", 0);

        category.add(LinkData.QUESTION1);
        category.add(LinkData.QUESTION2);
        category.add(LinkData.QUESTION3);
        category.add(LinkData.QUESTION4);

        imgLoad = (ImageView) findViewById(R.id.imgLoad);
        anim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        imgLoad.startAnimation(anim);
        Intent intent = getIntent();
        user_name = intent.getStringExtra("Name");
        user_url = intent.getStringExtra("Avatar");
        check_login = intent.getBooleanExtra("Login", false);


        imgAvatar = (HexagonImageView) findViewById(R.id.imgAvatar);
        tvNameUser = (TextView) findViewById(R.id.tvNameUser);
        tvMoney = (TextView) findViewById(R.id.tvMoney);
        tvLv = (TextView) findViewById(R.id.tvLv);
        tvScore = (TextView) findViewById(R.id.tvScore);
        if (hlLv > 0) {
            tvMoney.setText(hlMoney + " Vnđ");
            tvScore.setText(hlScore + " đ");
            tvLv.setText("Lv." + hlLv);
        }
        if (user_url.equals("") || user_url.equals(null) || user_url.length() == 0) {
            Picasso.with(getApplication()).load(R.drawable.ava)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.ava)
                    .fit()
                    .into(imgAvatar);
        } else {
            Picasso.with(getApplication()).load(user_url)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.ava)
                    .fit()
                    .into(imgAvatar);
        }
        if (user_name != "") {
            tvNameUser.setText(user_name);
        } else {
            tvNameUser.setText("Khách");
        }

        mOfflineGetDataPresenter = new OfflineGetDataPresenter(this, this);
        mOfflineGetDataPresenter.getDataOffline();

    }


    public void enableNotification() {
//        Log.d("ALTPMB", "enabled");
//        editor.putLong("lastRun", System.currentTimeMillis());
//        editor.putBoolean("enabled", true);
//        editor.commit();

    }

    public void disableNotification() {
//        Log.d("ALTPMB", "disenable");
//        editor.putBoolean("enabled", false);
//        editor.commit();
//        ;
    }

    @Override
    protected void onUserLeaveHint() {
        enableNotification();
        super.onUserLeaveHint();
    }

    @Override
    public void getOfflineDataSuccess() {
        int tpmbTurn = getSharedPreferences("TPMBSHAF", MODE_PRIVATE).getInt("tpmbTurn", 5);
        tpmbTurn = tpmbTurn - 1;
        getSharedPreferences("TPMBSHAF", MODE_PRIVATE).edit().putInt("tpmbTurn", tpmbTurn).commit();
        Intent intent = new Intent(this, OfflineMoneyActivity.class);
        intent.putExtra("Name", user_name);
        intent.putExtra("Avatar", user_url);
        intent.putExtra("Login", check_login);
        startActivity(intent);
        finish();
    }
}

