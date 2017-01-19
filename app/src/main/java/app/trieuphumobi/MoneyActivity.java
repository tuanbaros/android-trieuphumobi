package app.trieuphumobi;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import data.DataQuestion;
import data.SeriaDataQuestion;

/**
 * Created by Dang Truong on 12/05/2016.
 */
public class MoneyActivity extends Activity {
    RelativeLayout Rl_lv1, Rl_lv2, Rl_lv3, Rl_lv4, Rl_lv5,
            Rl_lv6, Rl_lv7, Rl_lv8, Rl_lv9, Rl_lv10,
            Rl_lv11, Rl_lv12, Rl_lv13, Rl_lv14, Rl_lv15, Rlayout_Skip;
    ImageView img_Skip;
    int level = 0;
    MediaPlayer mp_luatchoi;

    private Boolean skip = true;

    SeriaDataQuestion seria_dataQuestion, seria_dataQuestion2;
    ArrayList<DataQuestion> listQuestionM = new ArrayList<DataQuestion>();
    ArrayList<DataQuestion> listTGM = new ArrayList<DataQuestion>();

    String user_name ="", user_url = "";
    Boolean check_login = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_money);

        seria_dataQuestion = (SeriaDataQuestion) getIntent().getSerializableExtra("dataArray");
        listQuestionM = seria_dataQuestion.getData();

        seria_dataQuestion2 = (SeriaDataQuestion) getIntent().getSerializableExtra("dataTrogiup");
        listTGM = seria_dataQuestion2.getData();

        Intent intent = getIntent();
        user_name = intent.getStringExtra("Name");
        user_url = intent.getStringExtra("Avatar");
        check_login = intent.getBooleanExtra("Login", false);

        mp_luatchoi = new MediaPlayer();
        mp_luatchoi = MediaPlayer.create(MoneyActivity.this, R.raw.luatchoi);
        mp_luatchoi.start();
        mp_luatchoi.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp_luatchoi.release();
                mp_luatchoi = new MediaPlayer();
                mp_luatchoi = MediaPlayer.create(MoneyActivity.this, R.raw.ready);
                mp_luatchoi.start();
                mp_luatchoi.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp_luatchoi.release();
                        mp_luatchoi = new MediaPlayer();
                        mp_luatchoi = MediaPlayer.create(MoneyActivity.this, R.raw.gofind);
                        mp_luatchoi.start();
                        mp_luatchoi.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if (skip == true) {
                                    Intent intent = new Intent(MoneyActivity.this, PlayActivity.class);
                                    intent.putExtra("dataArray", new SeriaDataQuestion(listQuestionM));
                                    intent.putExtra("dataTrogiup", new SeriaDataQuestion(listTGM));
                                    intent.putExtra("Name", user_name);
                                    intent.putExtra("Avatar", user_url);
                                    intent.putExtra("Login", check_login);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(R.anim.right_in, R.anim.no_animation);
                                }
                            }
                        });
                    }
                });

            }
        });

        Rl_lv1 = (RelativeLayout) findViewById(R.id.Rl_lv1);
        Rl_lv2 = (RelativeLayout) findViewById(R.id.Rl_lv2);
        Rl_lv3 = (RelativeLayout) findViewById(R.id.Rl_lv3);
        Rl_lv4 = (RelativeLayout) findViewById(R.id.Rl_lv4);
        Rl_lv5 = (RelativeLayout) findViewById(R.id.Rl_lv5);
        Rl_lv6 = (RelativeLayout) findViewById(R.id.Rl_lv6);
        Rl_lv7 = (RelativeLayout) findViewById(R.id.Rl_lv7);
        Rl_lv8 = (RelativeLayout) findViewById(R.id.Rl_lv8);
        Rl_lv9 = (RelativeLayout) findViewById(R.id.Rl_lv9);
        Rl_lv10 = (RelativeLayout) findViewById(R.id.Rl_lv10);
        Rl_lv11 = (RelativeLayout) findViewById(R.id.Rl_lv11);
        Rl_lv12 = (RelativeLayout) findViewById(R.id.Rl_lv12);
        Rl_lv13 = (RelativeLayout) findViewById(R.id.Rl_lv13);
        Rl_lv14 = (RelativeLayout) findViewById(R.id.Rl_lv14);
        Rl_lv15 = (RelativeLayout) findViewById(R.id.Rl_lv15);
        Rlayout_Skip = (RelativeLayout) findViewById(R.id.Rlayout_Skip);
//        img_Skip = (ImageView) findViewById(R.id.img_Skip);
        Rlayout_Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp_luatchoi != null) {
                    mp_luatchoi.pause();
                }

                Intent intent = new Intent(MoneyActivity.this, PlayActivity.class);
                intent.putExtra("dataArray", new SeriaDataQuestion(listQuestionM));
                intent.putExtra("dataTrogiup", new SeriaDataQuestion(listTGM));
                intent.putExtra("Name", user_name);
                intent.putExtra("Avatar", user_url);
                intent.putExtra("Login", check_login);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.no_animation);
                skip = false;
            }
        });


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Rl_lv5.setBackgroundResource(R.drawable.icon_question_level);
            }
        }, 4000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Rl_lv5.setBackgroundColor(00000000);
                Rl_lv10.setBackgroundResource(R.drawable.icon_question_level);
            }
        }, 5000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Rl_lv5.setBackgroundColor(00000000);
                Rl_lv10.setBackgroundColor(00000000);
                Rl_lv15.setBackgroundResource(R.drawable.icon_question_level);
            }
        }, 6000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Rl_lv5.setBackgroundResource(R.drawable.icon_question_level);
                Rl_lv10.setBackgroundResource(R.drawable.icon_question_level);
                Rl_lv15.setBackgroundResource(R.drawable.icon_question_level);
            }
        }, 6500);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mp_luatchoi != null) {
            mp_luatchoi.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp_luatchoi != null) {
            mp_luatchoi.stop();
            mp_luatchoi.release();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp_luatchoi != null) {
            mp_luatchoi.pause();
        }
    }


}
