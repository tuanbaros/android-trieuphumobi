package app.trieuphumobi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.HexagonImageView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import data.DataQuestion;
import data.LinkData;
import data.SeriaDataQuestion;
import helper.CustomCountDownTimer;
import helper.Download;


/**
 * Created by Dang Truong on 28/03/2016.
 */
public class PlayActivity extends Activity {
    SeriaDataQuestion seria_DataQuestion, seria_DataQuestion2;
    RelativeLayout Rlayout_answerA, Rlayout_answerB, Rlayout_answerC, Rlayout_answerD,
            Rlayout_tuvan, Rlayout_goidien, Rlayout_Close, Rlayout_true, Rlayout_false, Rlayout_D, Rlayout_S,
            Rlayout_CloseKhangia, rl_ykienkhangia, rl_Atrue, rl_Btrue, rl_Ctrue, rl_Dtrue;
    ImageView img1, img2, img3, img4, imgAvaTuvan;
    TextView tvTime, tvNumQuestion, tvContentQuestion, tvAnswerA, tvAnswerB,
            tvAnswerC, tvAnswerD, tvDiem, tvTuvan, tvNameTuvan, tv_Answer, tvTen_Nguoichoi;
    int checkPos;
    String answer_true, question_audio, answer_1_audio, answer_2_audio, answer_3_audio, answer_4_audio;
    ImageButton btn5050, btnGoidien, btnKhangia, btnDoicauhoi, btnStop;
    MediaPlayer mp_answer_true, mp_answer, mp_lose, mp_soundtrack, mp_question,
            mp_trogiup, mp_question_audio, mp_out_of_time, mp_win;
    ArrayList<DataQuestion> listQuestion = new ArrayList<DataQuestion>();
    ArrayList<DataQuestion> listTrogiupDoicauhoi = new ArrayList<DataQuestion>();
    ArrayList<DataQuestion> listTrogiupLv1 = new ArrayList<DataQuestion>();
    ArrayList<DataQuestion> listTrogiupLv2 = new ArrayList<DataQuestion>();
    ArrayList<DataQuestion> listTrogiupLv3 = new ArrayList<DataQuestion>();
    ArrayList<DataQuestion> listTrogiupLv4 = new ArrayList<DataQuestion>();

    ArrayList<String> listNameFile = new ArrayList<String>();

    ArrayList<String> arrayListAudio = new ArrayList<String>();

    ArrayList<String> arrayListAudioTrogiup = new ArrayList<String>();

    private Animation animShow, animHide;
    int numberQues;
    int numberDownLoad = 2;
    private Dialog dialog, dialog_stop, dialogAnswer, dialogKhangia;
    ArrayList<String> Diem = new ArrayList<String>();
    Boolean check = true, check1 = true, check2 = true, check3 = true;

    private CustomCountDownTimer cdt;

    ProgressBar barTimer;
    String money = "";

    private int choseAnser = 0;
    private long total = 60000;
    String user_name = "", user_url = "";
    HexagonImageView imgAvatar_Play;

    Boolean check_login = false;
    private boolean blPasue = false, blClick = false;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Integer[] strInt = {20000, 20000, 20000, 400000, 1000000,
            2000000, 3000000, 40000000, 4000000, 8000000,
            8000000, 10000000, 20000000, 25000000, 70000000};
    private int diemso = 0;
private int timeAnser=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_play);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Lấy dữ liệu từ Activity Loading
        seria_DataQuestion = (SeriaDataQuestion) getIntent().getSerializableExtra("dataArray");
        listQuestion = seria_DataQuestion.getData();
        seria_DataQuestion2 = (SeriaDataQuestion) getIntent().getSerializableExtra("dataTrogiup");
        listTrogiupDoicauhoi = seria_DataQuestion2.getData();

        // get intent
        Intent intent = getIntent();
        user_name = intent.getStringExtra("Name");
        user_url = intent.getStringExtra("Avatar");
        check_login = intent.getBooleanExtra("Login", false);
        // load audio question 3
        dowloadAudioQues(2);
        // init view in activity
        Rlayout_answerA = (RelativeLayout) findViewById(R.id.Rlayout_A);
        Rlayout_answerB = (RelativeLayout) findViewById(R.id.Rlayout_B);
        Rlayout_answerC = (RelativeLayout) findViewById(R.id.Rlayout_C);
        Rlayout_answerD = (RelativeLayout) findViewById(R.id.Rlayout_D);

        tvTime = (TextView) findViewById(R.id.tvTime);
        tvNumQuestion = (TextView) findViewById(R.id.tvNumQuestion);
        tvContentQuestion = (TextView) findViewById(R.id.tvContentQuestion);
        tvAnswerA = (TextView) findViewById(R.id.tvAnswerA);
        tvAnswerB = (TextView) findViewById(R.id.tvAnswerB);
        tvAnswerC = (TextView) findViewById(R.id.tvAnswerC);
        tvAnswerD = (TextView) findViewById(R.id.tvAnswerD);
        tvDiem = (TextView) findViewById(R.id.tvDiem);
        tvTen_Nguoichoi = (TextView) findViewById(R.id.tvTen_Nguoichoi);
        imgAvatar_Play = (HexagonImageView) findViewById(R.id.imgAvatar_Play);

        btn5050 = (ImageButton) findViewById(R.id.btn5050);
        btnGoidien = (ImageButton) findViewById(R.id.btnGoidien);
        btnKhangia = (ImageButton) findViewById(R.id.btnKhangia);
        btnDoicauhoi = (ImageButton) findViewById(R.id.btnDoicauhoi);
        btnStop = (ImageButton) findViewById(R.id.btnStop);

        if (user_name == "") {
            tvTen_Nguoichoi.setText("Khách");
        } else {
            tvTen_Nguoichoi.setText(user_name);
        }
        if (user_url.equals("") || user_url.length() == 0 || user_url.equals(null)) {
            Picasso.with(getApplication()).load(R.drawable.ava)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.ava)
                    .fit()
                    .into(imgAvatar_Play);
        } else {
            Picasso.with(getApplication()).load(user_url)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.ava)
                    .fit()
                    .into(imgAvatar_Play);
        }
// create progress bar
        barTimer = (ProgressBar) findViewById(R.id.barTimer);
        barTimer.setMax(60);

// create media finish time question
        mp_out_of_time = new MediaPlayer();
        mp_out_of_time = MediaPlayer.create(PlayActivity.this, R.raw.out_of_time);

// create countdowntimer for a question
        cdt = new CustomCountDownTimer(total, 1000) {
            public void onTick(long millisUntilFinished) {
                tvTime.setText("" + millisUntilFinished / 1000);
                barTimer.setProgress((int) (millisUntilFinished / 1000));

            }

            public void onFinish() {
                tvTime.setText("0");
                barTimer.setProgress(0);
                mp_out_of_time.start();
                money = tvDiem.getText().toString();
                Intent intent = new Intent(PlayActivity.this, EndGameActivity.class);
                intent.putExtra("diemso", ""+diemso);
                intent.putExtra("NumStop", String.valueOf(numberQues + 1));
                if (numberQues < 4) {
                    intent.putExtra("Money", tvDiem.getText().toString());
                }
                if (numberQues > 4 && numberQues < 9) {
                    intent.putExtra("Money", "2,000,000");
                }
                if (numberQues > 9 && numberQues < 14) {
                    intent.putExtra("Money", "22,000,000");
                }
                if (numberQues == 14) {
                    intent.putExtra("Money", "150,000,000");
                }
                intent.putExtra("Name", user_name);
                intent.putExtra("Avatar", user_url);
                intent.putExtra("Login", check_login);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.no_animation);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "EndGame" + user_name + ": " + numberQues);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        };
        cdt.start();
// create media
        mp_soundtrack = new MediaPlayer();
        mp_soundtrack = MediaPlayer.create(PlayActivity.this, R.raw.moc1);
        mp_soundtrack.start();
        mp_soundtrack.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp_soundtrack.start();
            }
        });

        initAnimation();
        showQuestionAnni();

        Rlayout_answerA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choseAnser == 0) {
                    Rlayout_answerA.setBackgroundResource(R.drawable.img_answeraselected);
                    checkPos = 1;
                    dialogAnswer = new Dialog(PlayActivity.this, R.style.DialogCustomTheme);
                    dialogAnswer.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogAnswer.setContentView(R.layout.dialog_answer);
//                dialogAnswer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(0, 255, 255, 255)));
                    getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT);
                    tv_Answer = (TextView) dialogAnswer.findViewById(R.id.tv_Answer);
                    Rlayout_D = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_D);
                    Rlayout_S = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_S);
                    tv_Answer.setText("Đáp án cuối cùng của bạn là A?");
                    Rlayout_D.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            timeAnser=Integer.parseInt(tvTime.getText().toString());
                            cdt.cancel();
                            blClick = true;
                            choseAnser = 1;
                            if (mp_question_audio != null && mp_question_audio.isPlaying()) {
                                mp_question_audio.stop();
                            }

                            dialogAnswer.cancel();
                            Rlayout_answerA.setBackgroundResource(R.drawable.img_answeraselected);
                            Rlayout_answerB.setBackgroundResource(R.drawable.img_answerb);
                            Rlayout_answerC.setBackgroundResource(R.drawable.img_answerc);
                            Rlayout_answerD.setBackgroundResource(R.drawable.img_answerd);
                            mp_answer = MediaPlayer.create(PlayActivity.this, R.raw.ans_a);
                            mp_answer.start();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    checkAnswerTrue();
                                }
                            }, 6000);

                        }
                    });
                    Rlayout_S.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Rlayout_answerA.setBackgroundResource(R.drawable.img_answera);
                            dialogAnswer.cancel();
                        }
                    });
                    dialogAnswer.setCanceledOnTouchOutside(false);
                    dialogAnswer.show();
                }
            }
        });

        Rlayout_answerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choseAnser == 0) {
                    Rlayout_answerB.setBackgroundResource(R.drawable.img_answerbselected);
                    checkPos = 2;
                    dialogAnswer = new Dialog(PlayActivity.this, R.style.DialogCustomTheme);
                    dialogAnswer.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogAnswer.setContentView(R.layout.dialog_answer);
//                dialogAnswer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(25, 25, 255, 255)));
                    getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT);
                    tv_Answer = (TextView) dialogAnswer.findViewById(R.id.tv_Answer);
                    Rlayout_D = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_D);
                    Rlayout_S = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_S);
                    tv_Answer.setText("Đáp án cuối cùng của bạn là B?");
                    Rlayout_D.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            timeAnser=Integer.parseInt(tvTime.getText().toString());
                            cdt.cancel();

                            blClick = true;
                            if (mp_question_audio != null && mp_question_audio.isPlaying()) {
                                mp_question_audio.stop();
                            }

                            choseAnser = 1;
                            dialogAnswer.cancel();
                            Rlayout_answerA.setBackgroundResource(R.drawable.img_answera);
                            Rlayout_answerB.setBackgroundResource(R.drawable.img_answerbselected);
                            Rlayout_answerC.setBackgroundResource(R.drawable.img_answerc);
                            Rlayout_answerD.setBackgroundResource(R.drawable.img_answerd);
                            mp_answer = MediaPlayer.create(PlayActivity.this, R.raw.ans_b);
                            mp_answer.start();
                            if (!blPasue) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkAnswerTrue();
                                    }
                                }, 6000);
                            }
                        }
                    });
                    Rlayout_S.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Rlayout_answerB.setBackgroundResource(R.drawable.img_answerb);
                            dialogAnswer.cancel();
                        }
                    });
                    dialogAnswer.setCanceledOnTouchOutside(false);
                    dialogAnswer.show();
                }


            }
        });

        Rlayout_answerC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choseAnser == 0) {
                    Rlayout_answerC.setBackgroundResource(R.drawable.img_answercselected);
                    checkPos = 3;
                    dialogAnswer = new Dialog(PlayActivity.this, R.style.DialogCustomTheme);
                    dialogAnswer.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogAnswer.setContentView(R.layout.dialog_answer);
//                dialogAnswer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(0, 0, 255, 255)));
                    getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT);
                    tv_Answer = (TextView) dialogAnswer.findViewById(R.id.tv_Answer);
                    Rlayout_D = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_D);
                    Rlayout_S = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_S);
                    tv_Answer.setText("Đáp án cuối cùng của bạn là C?");
                    Rlayout_D.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            timeAnser=Integer.parseInt(tvTime.getText().toString());
                            cdt.cancel();
                            blClick = true;
                            if (mp_question_audio != null && mp_question_audio.isPlaying()) {
                                mp_question_audio.stop();
                            }
                            choseAnser = 1;
                            dialogAnswer.cancel();
                            Rlayout_answerA.setBackgroundResource(R.drawable.img_answera);
                            Rlayout_answerB.setBackgroundResource(R.drawable.img_answerb);
                            Rlayout_answerC.setBackgroundResource(R.drawable.img_answercselected);
                            Rlayout_answerD.setBackgroundResource(R.drawable.img_answerd);
                            mp_answer = MediaPlayer.create(PlayActivity.this, R.raw.ans_c);
                            mp_answer.start();
                            if (!blPasue) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkAnswerTrue();
                                    }
                                }, 6000);
                            }
                        }
                    });
                    Rlayout_S.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Rlayout_answerC.setBackgroundResource(R.drawable.img_answerc);
                            dialogAnswer.cancel();
                        }
                    });
                    dialogAnswer.setCanceledOnTouchOutside(false);
                    dialogAnswer.show();
                }


            }
        });

        Rlayout_answerD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choseAnser == 0) {
                    Rlayout_answerD.setBackgroundResource(R.drawable.img_answerdselected);
                    checkPos = 4;
                    dialogAnswer = new Dialog(PlayActivity.this, R.style.DialogCustomTheme);
                    dialogAnswer.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogAnswer.setContentView(R.layout.dialog_answer);
                    getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT);
                    tv_Answer = (TextView) dialogAnswer.findViewById(R.id.tv_Answer);
                    Rlayout_D = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_D);
                    Rlayout_S = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_S);
                    tv_Answer.setText("Đáp án cuối cùng của bạn là D?");
                    Rlayout_D.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            timeAnser=Integer.parseInt(tvTime.getText().toString());
                            cdt.cancel();
                            blClick = true;
                            if (mp_question_audio != null && mp_question_audio.isPlaying()) {
                                mp_question_audio.stop();
                            }
                            choseAnser = 1;
                            dialogAnswer.cancel();
                            Rlayout_answerA.setBackgroundResource(R.drawable.img_answera);
                            Rlayout_answerB.setBackgroundResource(R.drawable.img_answerb);
                            Rlayout_answerC.setBackgroundResource(R.drawable.img_answerc);
                            Rlayout_answerD.setBackgroundResource(R.drawable.img_answerdselected);
                            mp_answer = MediaPlayer.create(PlayActivity.this, R.raw.ans_d);
                            mp_answer.start();
                            if (!blPasue) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkAnswerTrue();
                                    }
                                }, 6000);
                            }

                        }
                    });
                    Rlayout_S.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Rlayout_answerD.setBackgroundResource(R.drawable.img_answerd);
                            dialogAnswer.cancel();
                        }
                    });
                    dialogAnswer.setCanceledOnTouchOutside(false);
                    dialogAnswer.show();
                }

            }
        });


        Diem.add("0");
        Diem.add("200,000");
        Diem.add("400,000");
        Diem.add("600,000");
        Diem.add("1,000,000");
        Diem.add("2,000,000");
        Diem.add("3,000,000");
        Diem.add("6,000,000");
        Diem.add("10,000,000");
        Diem.add("14,000,000");
        Diem.add("22,000,000");
        Diem.add("30,000,000");
        Diem.add("40,000,000");
        Diem.add("60,000,000");
        Diem.add("85,000,000");
        Diem.add("150,000,000");

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choseAnser == 0) {
                    if (mp_question_audio != null && mp_question_audio.isPlaying()) {
                        mp_question_audio.stop();
                    }
                    cdt.cancel();
                    blClick = false;
//                    btnStop.setBackgroundResource(R.drawable.btn_stop);
                    dungCuocChoi();
                }
            }
        });

        btnDoicauhoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choseAnser == 0) {
                    if (mp_question_audio != null && mp_question_audio.isPlaying()) {
                        mp_question_audio.pause();
                    }
                    if (check == true) {
                        dialogAnswer = new Dialog(PlayActivity.this, R.style.DialogCustomTheme);
                        dialogAnswer.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialogAnswer.setContentView(R.layout.dialog_answer);
                        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.MATCH_PARENT);
                        tv_Answer = (TextView) dialogAnswer.findViewById(R.id.tv_Answer);
                        Rlayout_D = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_D);
                        Rlayout_S = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_S);
                        tv_Answer.setText("Bạn muốn đổi câu hỏi?");
                        Rlayout_D.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogAnswer.cancel();
                                btnDoicauhoi.setBackgroundResource(R.drawable.btn_change_dis);
                                doiCauhoi(numberQues);
                                check = false;
                            }
                        });
                        Rlayout_S.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogAnswer.cancel();
                                check = true;
                            }
                        });
                        dialogAnswer.setCanceledOnTouchOutside(false);
                        dialogAnswer.show();
                    } else
                        Toast.makeText(PlayActivity.this, "Bạn chỉ được sử dụng trợ giúp đổi câu hỏi một lần!", Toast.LENGTH_LONG).show();
                }

            }
        });

        btn5050.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (choseAnser == 0) {
                    if (mp_question_audio != null && mp_question_audio.isPlaying()) {
                        mp_question_audio.pause();
                    }
                    if (check1 == true) {

                        dialogAnswer = new Dialog(PlayActivity.this, R.style.DialogCustomTheme);
                        dialogAnswer.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialogAnswer.setContentView(R.layout.dialog_answer);
                        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.MATCH_PARENT);
                        tv_Answer = (TextView) dialogAnswer.findViewById(R.id.tv_Answer);
                        Rlayout_D = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_D);
                        Rlayout_S = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_S);
                        tv_Answer.setText("Bạn muốn sử dụng quyền trợ giúp 50 50?");
                        Rlayout_D.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogAnswer.cancel();
                                mp_trogiup = MediaPlayer.create(PlayActivity.this, R.raw.sound5050);
                                mp_trogiup.start();
                                mp_trogiup.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        troGiup5050(answer_true);
                                    }
                                });
                                btn5050.setBackgroundResource(R.drawable.btn_5050_dis);
                                check1 = false;
                            }
                        });
                        Rlayout_S.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogAnswer.cancel();
//                                check1 = true;
                            }
                        });
                        dialogAnswer.setCanceledOnTouchOutside(false);
                        dialogAnswer.show();

                    } else
                        Toast.makeText(PlayActivity.this, "Bạn chỉ được sử dụng trợ giúp 50 50 một lần!", Toast.LENGTH_LONG).show();
                }

            }
        });
        btnGoidien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choseAnser == 0) {
                    if (mp_question_audio != null && mp_question_audio.isPlaying()) {
                        mp_question_audio.pause();
                    }
                    if (check2 == true) {
                        dialogAnswer = new Dialog(PlayActivity.this, R.style.DialogCustomTheme);
                        dialogAnswer.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialogAnswer.setContentView(R.layout.dialog_answer);
                        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.MATCH_PARENT);
                        tv_Answer = (TextView) dialogAnswer.findViewById(R.id.tv_Answer);
                        Rlayout_D = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_D);
                        Rlayout_S = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_S);
                        tv_Answer.setText("Bạn muốn sử dụng quyền trợ giúp Gọi điện cho người thân?");
                        Rlayout_D.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogAnswer.cancel();
                                mp_trogiup = MediaPlayer.create(PlayActivity.this, R.raw.call);
                                mp_trogiup.start();
                                goiDien(answer_true);
                                btnGoidien.setBackgroundResource(R.drawable.btn_call_dis);
                                check2 = false;
                            }
                        });
                        Rlayout_S.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogAnswer.cancel();
                                check2 = true;
                            }
                        });
                        dialogAnswer.setCanceledOnTouchOutside(false);
                        dialogAnswer.show();

                    } else
                        Toast.makeText(PlayActivity.this, "Bạn chỉ được sử dụng trợ giúp gọi điện cho người thân một lần!", Toast.LENGTH_LONG).show();
                }

            }
        });
        btnKhangia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choseAnser == 0) {
                    if (mp_question_audio != null && mp_question_audio.isPlaying()) {
                        mp_question_audio.pause();
                    }
                    if (check3 == true) {
                        dialogAnswer = new Dialog(PlayActivity.this, R.style.DialogCustomTheme);
                        dialogAnswer.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialogAnswer.setContentView(R.layout.dialog_answer);
                        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.MATCH_PARENT);
                        tv_Answer = (TextView) dialogAnswer.findViewById(R.id.tv_Answer);
                        Rlayout_D = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_D);
                        Rlayout_S = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_S);
                        tv_Answer.setText("Bạn muốn sử dụng quyền trợ giúp của Khán giả?");
                        Rlayout_D.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogAnswer.cancel();
                                btnKhangia.setBackgroundResource(R.drawable.btn_audien_dis);
                                mp_trogiup = MediaPlayer.create(PlayActivity.this, R.raw.khan_gia);
                                mp_trogiup.start();
                                mp_trogiup.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        khanGia(answer_true);
                                    }
                                });
                                check3 = false;
                            }
                        });
                        Rlayout_S.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogAnswer.cancel();
                                check3 = true;
                            }
                        });
                        dialogAnswer.setCanceledOnTouchOutside(false);
                        dialogAnswer.show();

                    } else
                        Toast.makeText(PlayActivity.this, "Bạn chỉ được sử dụng trợ giúp của khán giả một lần!", Toast.LENGTH_LONG).show();
                }

            }
        });

        nextQuestion(listQuestion, numberQues, Diem);

        for (int i = 0; i < listTrogiupDoicauhoi.size(); i++) {
            arrayListAudioTrogiup.add(listTrogiupDoicauhoi.get(i).getQuestion_audio());
            arrayListAudioTrogiup.add(listTrogiupDoicauhoi.get(i).getAnswer_1_audio());
            arrayListAudioTrogiup.add(listTrogiupDoicauhoi.get(i).getAnswer_2_audio());
            arrayListAudioTrogiup.add(listTrogiupDoicauhoi.get(i).getAnswer_3_audio());
            arrayListAudioTrogiup.add(listTrogiupDoicauhoi.get(i).getAnswer_4_audio());

            listTrogiupLv1.add(listTrogiupDoicauhoi.get(0));
            listTrogiupLv2.add(listTrogiupDoicauhoi.get(1));
            listTrogiupLv3.add(listTrogiupDoicauhoi.get(2));
            listTrogiupLv4.add(listTrogiupDoicauhoi.get(3));
        }
        Download downloadAu = new Download(arrayListAudioTrogiup);
        downloadAu.execute();

    }

    public void checkAnswerTrue() {

        if (checkPos == Integer.parseInt(answer_true)) {


            diemso=diemso+Math.round(strInt[numberQues]*timeAnser/60);
            Log.d("numberQues", numberQues + "\n"+strInt[numberQues]+"\n"+Math.round(strInt[numberQues]*timeAnser/60)+"\n"+timeAnser+"\n"+diemso);
            showAnsTrue(Integer.parseInt(answer_true));
            if (numberQues - 1 < listQuestion.size()) {
                numberQues = numberQues + 1;
                numberDownLoad = numberDownLoad + 1;
                dowloadAudioQues(numberDownLoad);
//                Log.d("numberDownLoad", "" + numberDownLoad);
                checkWin(numberQues - 1);


            }

        } else {

            showAnsFalse(Integer.parseInt(answer_true));
//            cdt.onFinish();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    money = tvDiem.getText().toString();
//                    Log.d("MONEY", money);
                    Intent intent = new Intent(PlayActivity.this, EndGameActivity.class);
                    intent.putExtra("diemso", ""+diemso);
                    intent.putExtra("NumStop", String.valueOf(numberQues + 1));
                    if (numberQues < 4) {
                        intent.putExtra("Money", tvDiem.getText().toString());
                    }
                    if (numberQues > 4 && numberQues < 9) {
                        intent.putExtra("Money", "2,000,000");
                    }
                    if (numberQues > 9 && numberQues < 14) {
                        intent.putExtra("Money", "22,000,000");
                    }
                    if (numberQues == 14) {
                        intent.putExtra("Money", "150,000,000");
                    }
                    intent.putExtra("Name", user_name);
                    intent.putExtra("Avatar", user_url);
                    intent.putExtra("Login", check_login);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.no_animation);
                }
            }, 2000);


        }
//        Log.d("choseAnser", choseAnser + "check");
    }

    private void initAnimation() {
        animShow = AnimationUtils.loadAnimation(this, R.anim.right_slide);
        animHide = AnimationUtils.loadAnimation(this, R.anim.left_slide);
    }

    public void nextQuestion(ArrayList<DataQuestion> DataQuestions, int position, ArrayList<String> diem) {
        hideFrame();
        choseAnser = 0;

        tvNumQuestion.setText("CÂU HỎI " + (position + 1));
        tvContentQuestion.setText(DataQuestions.get(position).getQuestion());
        tvAnswerA.setText(DataQuestions.get(position).getAnswer_1());
        tvAnswerB.setText(DataQuestions.get(position).getAnswer_2());
        tvAnswerC.setText(DataQuestions.get(position).getAnswer_3());
        tvAnswerD.setText(DataQuestions.get(position).getAnswer_4());
        answer_true = String.valueOf(DataQuestions.get(position).getAnswer_true());

        String au_Ques = "", au_1 = "", au_2 = "", au_3 = "", au_4 = "";

        au_Ques = DataQuestions.get(position).getQuestion_audio();
        au_1 = DataQuestions.get(position).getAnswer_1_audio();
        au_2 = DataQuestions.get(position).getAnswer_2_audio();
        au_3 = DataQuestions.get(position).getAnswer_3_audio();
        au_4 = DataQuestions.get(position).getAnswer_4_audio();

        playSoundQues(position);
        tvDiem.setText(diem.get(position));

        question_audio = getAudioFromFile(au_Ques);
        answer_1_audio = getAudioFromFile(au_1);
        answer_2_audio = getAudioFromFile(au_2);
        answer_3_audio = getAudioFromFile(au_3);
        answer_4_audio = getAudioFromFile(au_4);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                play_Audio();
            }
        }, 1500);

    }

    public void showQuestionAnni() {
        tvNumQuestion.setVisibility(View.VISIBLE);
        tvNumQuestion.startAnimation(animShow);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvContentQuestion.setVisibility(View.VISIBLE);
                tvContentQuestion.startAnimation(animShow);
            }
        }, 1000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Rlayout_answerA.setVisibility(View.VISIBLE);
                Rlayout_answerA.startAnimation(animShow);
            }
        }, 3000);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Rlayout_answerB.setVisibility(View.VISIBLE);
                Rlayout_answerB.startAnimation(animShow);
            }
        }, 3000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Rlayout_answerC.setVisibility(View.VISIBLE);
                Rlayout_answerC.startAnimation(animShow);
            }
        }, 3000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Rlayout_answerD.setVisibility(View.VISIBLE);
                Rlayout_answerD.startAnimation(animShow);
            }
        }, 3000);
    }

    public void hideQuestionAni() {
//        tvNumQuestion.startAnimation( animHide );
        tvNumQuestion.setVisibility(View.GONE);
//        tvContentQuestion.startAnimation(animHide);
        tvContentQuestion.setVisibility(View.GONE);
//        Rlayout_answerA.startAnimation( animHide );
        Rlayout_answerA.setVisibility(View.GONE);
//        Rlayout_answerB.startAnimation( animHide );
        Rlayout_answerB.setVisibility(View.GONE);
//        Rlayout_answerC.startAnimation( animHide );
        Rlayout_answerC.setVisibility(View.GONE);
//        Rlayout_answerD.startAnimation( animHide );
        Rlayout_answerD.setVisibility(View.GONE);
    }

    public void showAnsTrue(int ans_true) {
        showFrame();
        switch (ans_true) {
            case 1:
                mp_answer_true = MediaPlayer.create(PlayActivity.this, R.raw.true_a);
                mp_answer_true.start();
                Rlayout_answerA.setBackgroundResource(R.drawable.true_a);
                AnimationDrawable pro = (AnimationDrawable) Rlayout_answerA.getBackground();
                pro.start();
                break;
            case 2:
                mp_answer_true = MediaPlayer.create(PlayActivity.this, R.raw.true_b);
                mp_answer_true.start();
                Rlayout_answerB.setBackgroundResource(R.drawable.true_b);
                AnimationDrawable pro1 = (AnimationDrawable) Rlayout_answerB.getBackground();
                pro1.start();
                break;
            case 3:
                mp_answer_true = MediaPlayer.create(PlayActivity.this, R.raw.true_c);
                mp_answer_true.start();
                Rlayout_answerC.setBackgroundResource(R.drawable.true_c);
                AnimationDrawable pro2 = (AnimationDrawable) Rlayout_answerC.getBackground();
                pro2.start();
                break;
            case 4:
                mp_answer_true = MediaPlayer.create(PlayActivity.this, R.raw.true_d);
                mp_answer_true.start();
                Rlayout_answerD.setBackgroundResource(R.drawable.true_d);
                AnimationDrawable pro3 = (AnimationDrawable) Rlayout_answerD.getBackground();
                pro3.start();
                break;
            default:
                break;
        }
    }

    public void showAnsFalse(int ans_true) {
        showFrame();
        switch (ans_true) {
            case 1:
                mp_lose = MediaPlayer.create(PlayActivity.this, R.raw.lose_a);
                mp_lose.start();
                Rlayout_answerA.setBackgroundResource(R.drawable.true_a);
                AnimationDrawable pro = (AnimationDrawable) Rlayout_answerA.getBackground();
                pro.start();
                break;
            case 2:
                mp_lose = MediaPlayer.create(PlayActivity.this, R.raw.lose_b);
                mp_lose.start();
                Rlayout_answerB.setBackgroundResource(R.drawable.true_b);
                AnimationDrawable pro1 = (AnimationDrawable) Rlayout_answerB.getBackground();
                pro1.start();
                break;
            case 3:
                mp_lose = MediaPlayer.create(PlayActivity.this, R.raw.lose_c);
                mp_lose.start();
                Rlayout_answerC.setBackgroundResource(R.drawable.true_c);
                AnimationDrawable pro2 = (AnimationDrawable) Rlayout_answerC.getBackground();
                pro2.start();
                break;
            case 4:
                mp_lose = MediaPlayer.create(PlayActivity.this, R.raw.lose_d);
                mp_lose.start();
                Rlayout_answerD.setBackgroundResource(R.drawable.true_d);
                AnimationDrawable pro3 = (AnimationDrawable) Rlayout_answerD.getBackground();
                pro3.start();
                break;
        }
    }

    public void playSoundQues(int position) {
        switch (position) {
            case 0:
                mp_question = MediaPlayer.create(PlayActivity.this, R.raw.ques01);
                mp_question.start();
                break;
            case 1:
                mp_question = MediaPlayer.create(PlayActivity.this, R.raw.ques02);
                mp_question.start();
                break;
            case 2:
                mp_question = MediaPlayer.create(PlayActivity.this, R.raw.ques03);
                mp_question.start();
                break;
            case 3:
                mp_question = MediaPlayer.create(PlayActivity.this, R.raw.ques04);
                mp_question.start();
                break;
            case 4:
                mp_question = MediaPlayer.create(PlayActivity.this, R.raw.ques05);
                mp_question.start();
                break;
            case 5:
                mp_question = MediaPlayer.create(PlayActivity.this, R.raw.ques06);
                mp_question.start();
                break;
            case 6:
                mp_question = MediaPlayer.create(PlayActivity.this, R.raw.ques07);
                mp_question.start();
                break;
            case 7:
                mp_question = MediaPlayer.create(PlayActivity.this, R.raw.ques08);
                mp_question.start();
                break;
            case 8:
                mp_question = MediaPlayer.create(PlayActivity.this, R.raw.ques09);
                mp_question.start();
                break;
            case 9:
                mp_question = MediaPlayer.create(PlayActivity.this, R.raw.ques10);
                mp_question.start();
                break;
            case 10:
                mp_question = MediaPlayer.create(PlayActivity.this, R.raw.ques11);
                mp_question.start();
                break;
            case 11:
                mp_question = MediaPlayer.create(PlayActivity.this, R.raw.ques12);
                mp_question.start();
                break;
            case 12:
                mp_question = MediaPlayer.create(PlayActivity.this, R.raw.ques13);
                mp_question.start();
                break;
            case 13:
                mp_question = MediaPlayer.create(PlayActivity.this, R.raw.ques14);
                mp_question.start();
                break;
            case 14:
                mp_question = MediaPlayer.create(PlayActivity.this, R.raw.ques15);
                mp_question.start();
                break;

            default:
                break;
        }
    }

    public void troGiup5050(String answer_true) {
        if (Integer.parseInt(answer_true) == 1) {
            tvAnswerB.setVisibility(View.GONE);
//            tvAnswerC.setVisibility(View.GONE);
            tvAnswerD.setVisibility(View.GONE);
        }
        if (Integer.parseInt(answer_true) == 2) {
            tvAnswerA.setVisibility(View.GONE);
//            tvAnswerC.setVisibility(View.GONE);
            tvAnswerD.setVisibility(View.GONE);
        }
        if (Integer.parseInt(answer_true) == 3) {
            tvAnswerB.setVisibility(View.GONE);
//            tvAnswerC.setVisibility(View.GONE);
            tvAnswerD.setVisibility(View.GONE);
        }
        if (Integer.parseInt(answer_true) == 4) {
            tvAnswerA.setVisibility(View.GONE);
//            tvAnswerC.setVisibility(View.GONE);
            tvAnswerC.setVisibility(View.GONE);
        }


    }

    public void dungCuocChoi() {
        dialog_stop = new Dialog(this, R.style.Theme_BlurBackground);
        dialog_stop.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog_stop.setContentView(R.layout.dialog_stopgame);
        dialog_stop.show();
        Rlayout_true = (RelativeLayout) dialog_stop.findViewById(R.id.Rlayout_true);
        Rlayout_false = (RelativeLayout) dialog_stop.findViewById(R.id.Rlayout_false);

        Rlayout_true.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                money = tvDiem.getText().toString();
                Intent intent = new Intent(PlayActivity.this, EndGameActivity.class);
                intent.putExtra("diemso", ""+diemso);
                intent.putExtra("NumStop", String.valueOf(numberQues + 1));
                intent.putExtra("Money", money);
                intent.putExtra("Name", user_name);
                intent.putExtra("Avatar", user_url);
                intent.putExtra("Login", check_login);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.no_animation);
            }
        });
        Rlayout_false.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_stop.dismiss();
            }
        });

    }

    public void goiDien(final String answer_true1) {
        dialog = new Dialog(this, R.style.Theme_BlurBackground);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.dialog_goidien);

        img1 = (ImageView) dialog.findViewById(R.id.imgBacsi);
        img2 = (ImageView) dialog.findViewById(R.id.imgGiaovien);
        img3 = (ImageView) dialog.findViewById(R.id.imgKisu);
        img4 = (ImageView) dialog.findViewById(R.id.imgPhongvien);
        Rlayout_Close = (RelativeLayout) dialog.findViewById(R.id.Rlayout_Close);
        tvTuvan = (TextView) dialog.findViewById(R.id.tvDapanDung);
        Rlayout_tuvan = (RelativeLayout) dialog.findViewById(R.id.Rlayout_tuvan);
        Rlayout_goidien = (RelativeLayout) dialog.findViewById(R.id.Rlayout_goidien);
        imgAvaTuvan = (ImageView) dialog.findViewById(R.id.imgAvaTuvan);
        tvNameTuvan = (TextView) dialog.findViewById(R.id.tvNameTuvan);
//
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tuVanDapan(answer_true1);
                imgAvaTuvan.setImageResource(R.drawable.icon_bacsi_2);
                tvNameTuvan.setText("Bác sĩ");
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tuVanDapan(answer_true1);
                imgAvaTuvan.setImageResource(R.drawable.icon_giaovien_2);
                tvNameTuvan.setText("Giáo viên");
            }
        });
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tuVanDapan(answer_true1);
                imgAvaTuvan.setImageResource(R.drawable.icon_kisu_2);
                tvNameTuvan.setText("Kĩ sư");
            }
        });
        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tuVanDapan(answer_true1);
                imgAvaTuvan.setImageResource(R.drawable.icon_phongvien_2);
                tvNameTuvan.setText("Phóng viên");
            }
        });
        Rlayout_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                btnGoidien.setImageResource(R.drawable.btn_call);
            }
        });

        dialog.show();
    }

    public void tuVanDapan(String answer_true2) {
        if (answer_true2.equals("1")) {
            Rlayout_goidien.setVisibility(View.GONE);
            Rlayout_tuvan.setVisibility(View.VISIBLE);
            tvTuvan.setText("A");
        }
        if (answer_true2.equals("2")) {
            Rlayout_goidien.setVisibility(View.GONE);
            Rlayout_tuvan.setVisibility(View.VISIBLE);
            tvTuvan.setText("B");
        }
        if (answer_true2.equals("3")) {
            Rlayout_goidien.setVisibility(View.GONE);
            Rlayout_tuvan.setVisibility(View.VISIBLE);
            tvTuvan.setText("C");
        }
        if (answer_true2.equals("4")) {
            Rlayout_goidien.setVisibility(View.GONE);
            Rlayout_tuvan.setVisibility(View.VISIBLE);
            tvTuvan.setText("D");
        }
    }

    public void doiCauhoi(int num) {
        cdt.start();
        if (num < 5) {

            tvNumQuestion.setText("CÂU HỎI " + (num + 1));
            tvContentQuestion.setText(listTrogiupLv1.get(0).getQuestion());
            tvAnswerA.setText(listTrogiupLv1.get(0).getAnswer_1());
            tvAnswerB.setText(listTrogiupLv1.get(0).getAnswer_2());
            tvAnswerC.setText(listTrogiupLv1.get(0).getAnswer_3());
            tvAnswerD.setText(listTrogiupLv1.get(0).getAnswer_4());
            answer_true = String.valueOf(listTrogiupLv1.get(0).getAnswer_true());

            String au_Ques = "", au_1 = "", au_2 = "", au_3 = "", au_4 = "";

            au_Ques = listTrogiupLv1.get(0).getQuestion_audio();
            au_1 = listTrogiupLv1.get(0).getAnswer_1_audio();
            au_2 = listTrogiupLv1.get(0).getAnswer_2_audio();
            au_3 = listTrogiupLv1.get(0).getAnswer_3_audio();
            au_4 = listTrogiupLv1.get(0).getAnswer_4_audio();

            playSoundQues(num);
            tvDiem.setText(Diem.get(num));

            question_audio = getAudioFromFile(au_Ques);
            answer_1_audio = getAudioFromFile(au_1);
            answer_2_audio = getAudioFromFile(au_2);
            answer_3_audio = getAudioFromFile(au_3);
            answer_4_audio = getAudioFromFile(au_4);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    play_Audio();
                }
            }, 2000);
        }
        if (num >= 5 && num < 10) {
            tvNumQuestion.setText("CÂU HỎI " + (num + 1));
            tvContentQuestion.setText(listTrogiupLv2.get(0).getQuestion());
            tvAnswerA.setText(listTrogiupLv2.get(0).getAnswer_1());
            tvAnswerB.setText(listTrogiupLv2.get(0).getAnswer_2());
            tvAnswerC.setText(listTrogiupLv2.get(0).getAnswer_3());
            tvAnswerD.setText(listTrogiupLv2.get(0).getAnswer_4());
            answer_true = String.valueOf(listTrogiupLv2.get(0).getAnswer_true());

            String au_Ques = "", au_1 = "", au_2 = "", au_3 = "", au_4 = "";

            au_Ques = listTrogiupLv2.get(0).getQuestion_audio();
            au_1 = listTrogiupLv2.get(0).getAnswer_1_audio();
            au_2 = listTrogiupLv2.get(0).getAnswer_2_audio();
            au_3 = listTrogiupLv2.get(0).getAnswer_3_audio();
            au_4 = listTrogiupLv2.get(0).getAnswer_4_audio();

            playSoundQues(num);
            tvDiem.setText(Diem.get(num));

            question_audio = getAudioFromFile(au_Ques);
            answer_1_audio = getAudioFromFile(au_1);
            answer_2_audio = getAudioFromFile(au_2);
            answer_3_audio = getAudioFromFile(au_3);
            answer_4_audio = getAudioFromFile(au_4);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    play_Audio();
                }
            }, 2000);


        }
        if (num >= 10 && num < 14) {
            tvNumQuestion.setText("CÂU HỎI " + (num + 1));
            tvContentQuestion.setText(listTrogiupLv3.get(0).getQuestion());
            tvAnswerA.setText(listTrogiupLv3.get(0).getAnswer_1());
            tvAnswerB.setText(listTrogiupLv3.get(0).getAnswer_2());
            tvAnswerC.setText(listTrogiupLv3.get(0).getAnswer_3());
            tvAnswerD.setText(listTrogiupLv3.get(0).getAnswer_4());
            answer_true = String.valueOf(listTrogiupLv3.get(0).getAnswer_true());

            String au_Ques = "", au_1 = "", au_2 = "", au_3 = "", au_4 = "";

            au_Ques = listTrogiupLv3.get(0).getQuestion_audio();
            au_1 = listTrogiupLv3.get(0).getAnswer_1_audio();
            au_2 = listTrogiupLv3.get(0).getAnswer_2_audio();
            au_3 = listTrogiupLv3.get(0).getAnswer_3_audio();
            au_4 = listTrogiupLv3.get(0).getAnswer_4_audio();

            playSoundQues(num);
            tvDiem.setText(Diem.get(num));

            question_audio = getAudioFromFile(au_Ques);
            answer_1_audio = getAudioFromFile(au_1);
            answer_2_audio = getAudioFromFile(au_2);
            answer_3_audio = getAudioFromFile(au_3);
            answer_4_audio = getAudioFromFile(au_4);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    play_Audio();
                }
            }, 2000);
        }
        if (num == 14) {
            tvNumQuestion.setText("CÂU HỎI " + (num + 1));
            tvContentQuestion.setText(listTrogiupLv4.get(0).getQuestion());
            tvAnswerA.setText(listTrogiupLv4.get(0).getAnswer_1());
            tvAnswerB.setText(listTrogiupLv4.get(0).getAnswer_2());
            tvAnswerC.setText(listTrogiupLv4.get(0).getAnswer_3());
            tvAnswerD.setText(listTrogiupLv4.get(0).getAnswer_4());
            answer_true = String.valueOf(listTrogiupLv4.get(0).getAnswer_true());

            String au_Ques = "", au_1 = "", au_2 = "", au_3 = "", au_4 = "";

            au_Ques = listTrogiupLv4.get(0).getQuestion_audio();
            au_1 = listTrogiupLv4.get(0).getAnswer_1_audio();
            au_2 = listTrogiupLv4.get(0).getAnswer_2_audio();
            au_3 = listTrogiupLv4.get(0).getAnswer_3_audio();
            au_4 = listTrogiupLv4.get(0).getAnswer_4_audio();

            playSoundQues(num);
            tvDiem.setText(Diem.get(num));

            question_audio = getAudioFromFile(au_Ques);
            answer_1_audio = getAudioFromFile(au_1);
            answer_2_audio = getAudioFromFile(au_2);
            answer_3_audio = getAudioFromFile(au_3);
            answer_4_audio = getAudioFromFile(au_4);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    play_Audio();
                }
            }, 2000);
        }
    }

    public void khanGia(final String answer1) {
        dialogKhangia = new Dialog(this, R.style.Theme_BlurBackground);
        dialogKhangia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialogKhangia.setContentView(R.layout.dialog_khangia1);

        Rlayout_CloseKhangia = (RelativeLayout) dialogKhangia.findViewById(R.id.Rlayout_CloseKG);
        rl_Atrue = (RelativeLayout) dialogKhangia.findViewById(R.id.rl_Atrue);
        rl_Btrue = (RelativeLayout) dialogKhangia.findViewById(R.id.rl_Btrue);
        rl_Ctrue = (RelativeLayout) dialogKhangia.findViewById(R.id.rl_Ctrue);
        rl_Dtrue = (RelativeLayout) dialogKhangia.findViewById(R.id.rl_Dtrue);
        rl_ykienkhangia = (RelativeLayout) dialogKhangia.findViewById(R.id.rl_ykienkhangia);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                audien(answer1);
            }
        }, 2000);

        Rlayout_CloseKhangia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogKhangia.dismiss();
            }
        });
        dialogKhangia.show();
    }

    public void audien(String answer) {
        if (answer.equals("1")) {
            rl_ykienkhangia.setVisibility(View.GONE);
            rl_Atrue.setVisibility(View.VISIBLE);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        }
        if (answer.equals("2")) {
            rl_ykienkhangia.setVisibility(View.GONE);
            rl_Btrue.setVisibility(View.VISIBLE);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        if (answer.equals("3")) {
            rl_ykienkhangia.setVisibility(View.GONE);
            rl_Ctrue.setVisibility(View.VISIBLE);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        if (answer.equals("4")) {
            rl_ykienkhangia.setVisibility(View.GONE);
            rl_Dtrue.setVisibility(View.VISIBLE);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

    public void play_Audio() {

        mp_question_audio = new MediaPlayer();
        try {
            mp_question_audio.setDataSource(question_audio);
            mp_question_audio.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp_question_audio.start();
        mp_question_audio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp_question_audio.release();
                mp_question_audio = new MediaPlayer();
                mp_question_audio = MediaPlayer.create(PlayActivity.this, R.raw.dapan_a);
                mp_question_audio.start();
                mp_question_audio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        try {
                            mp_question_audio.release();
                            mp_question_audio = new MediaPlayer();
//                            Log.d("audiofile2", answer_1_audio);
                            mp_question_audio.setDataSource(answer_1_audio);
                            mp_question_audio.prepare();
                            mp_question_audio.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        mp_question_audio.start();
                        mp_question_audio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp_question_audio.release();
                                mp_question_audio = new MediaPlayer();
                                mp_question_audio = MediaPlayer.create(PlayActivity.this, R.raw.dapan_b);
                                mp_question_audio.start();
                                mp_question_audio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        try {
                                            mp_question_audio.release();
                                            mp_question_audio = new MediaPlayer();
//                                            Log.d("audiofile2", answer_2_audio);
                                            mp_question_audio.setDataSource(answer_2_audio);
                                            mp_question_audio.prepare();
                                            mp_question_audio.start();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
//                                        mp_question_audio.start();
                                        mp_question_audio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                mp_question_audio.release();
                                                mp_question_audio = new MediaPlayer();
                                                mp_question_audio = MediaPlayer.create(PlayActivity.this, R.raw.dapan_c);
                                                mp_question_audio.start();
                                                mp_question_audio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                    @Override
                                                    public void onCompletion(MediaPlayer mp) {
                                                        try {
                                                            mp_question_audio.release();
                                                            mp_question_audio = new MediaPlayer();
//                                                            mp_question_audio.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                                                            Log.d("audiofile2", answer_3_audio);
                                                            mp_question_audio.setDataSource(answer_3_audio);
                                                            mp_question_audio.prepare();
                                                            mp_question_audio.start();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
//                                                        mp_question_audio.start();
                                                        mp_question_audio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                            @Override
                                                            public void onCompletion(MediaPlayer mp) {
                                                                mp_question_audio.release();
                                                                mp_question_audio = new MediaPlayer();
                                                                mp_question_audio = MediaPlayer.create(PlayActivity.this, R.raw.dapan_d);
                                                                mp_question_audio.start();
                                                                mp_question_audio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                                    @Override
                                                                    public void onCompletion(MediaPlayer mp) {
                                                                        try {
                                                                            mp_question_audio.release();
                                                                            mp_question_audio = new MediaPlayer();
//                                                                            mp_question_audio.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                                                                            Log.d("audiofile2", answer_4_audio);
                                                                            mp_question_audio.setDataSource(answer_4_audio);
                                                                            mp_question_audio.prepare();
                                                                            mp_question_audio.start();
                                                                        } catch (IOException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
//        }

    }

    public void dowloadAudioQues(int position) {
        if (position < listQuestion.size()) {
//            Log.d("audiofile next", listQuestion.get(position).getQuestion() + "\n" + position + ": " + listQuestion.get(position).getQuestion_audio() +
//                    "\nTrue: " + listQuestion.get(position).getAnswer_true() +
//                    "\nA: " + listQuestion.get(position).getAnswer_1_audio() +
//                    "\nB:" + listQuestion.get(position).getAnswer_2_audio() +
//                    " \nC:" + listQuestion.get(position).getAnswer_3_audio() +
//                    "\nD: " + listQuestion.get(position).getAnswer_4_audio());
            if (listQuestion.get(position).getQuestion_audio().length() > 0)
                arrayListAudio.add(listQuestion.get(position).getQuestion_audio());
            if (listQuestion.get(position).getAnswer_1_audio().length() > 0)
                arrayListAudio.add(listQuestion.get(position).getAnswer_1_audio());
            if (listQuestion.get(position).getAnswer_2_audio().length() > 0)
                arrayListAudio.add(listQuestion.get(position).getAnswer_2_audio());
            if (listQuestion.get(position).getAnswer_3_audio().length() > 0)
                arrayListAudio.add(listQuestion.get(position).getAnswer_3_audio());
            if (listQuestion.get(position).getAnswer_4_audio().length() > 0)
                arrayListAudio.add(listQuestion.get(position).getAnswer_4_audio());

            Download download = new Download(arrayListAudio);
            download.execute();
        } else {
//            Log.d("audiofile", "load finish");
        }

//        }

//        getAudioFromFile();
    }

    public String getAudioFromFile(String url) {
        String audio_name = extractFilename(url);
        String url_out = "";
//        Log.d("AUDIO_NAME", audio_name);
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + LinkData.FOLDER_NAME;
        File file = new File(file_path);
        if (file.isDirectory()) {
            String[] childe = file.list();
            for (int i = 0; i < childe.length; i++) {
                String name = new File(file, childe[i]).getName();
                if (audio_name.equals(name)) {
                    url_out = file_path + "/" + new File(file, childe[i]).getName();
                }
//                Log.d("Name Name", name + "\n" + url_out);
                listNameFile.add(url_out);
            }

        }
        return url_out;
    }

    private String extractFilename(String urlDownloadLink) {
        if (urlDownloadLink.equals("")) {
            return "";
        }
        String newFilename = "";
        if (urlDownloadLink.contains("/")) {
            int dotPosition = urlDownloadLink.lastIndexOf("/");
            newFilename = urlDownloadLink.substring(dotPosition + 1, urlDownloadLink.length());
        } else {
            newFilename = urlDownloadLink;
        }
        return newFilename;
    }

    public void checkWin(final int num) {

        if (num == 14) {
            mp_win = new MediaPlayer();
            mp_win = MediaPlayer.create(PlayActivity.this, R.raw.best_player);
            mp_win.start();
            mp_win.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Intent intent = new Intent(PlayActivity.this, EndGameActivity.class);
                    intent.putExtra("diemso", ""+diemso);
                    intent.putExtra("NumStop", "15");
                    intent.putExtra("Money", "150,000,000");
                    intent.putExtra("Name", user_name);
                    intent.putExtra("Avatar", user_url);
                    intent.putExtra("Login", check_login);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.no_animation);
                }
            });
        } else {
            nextQuestion1();

        }
    }

    public void nextQuestion1() {
        choseAnser = 0;
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideQuestionAni();
                if (!blPasue) {
                    nextQuestion(listQuestion, numberQues, Diem);
                    showQuestionAnni();
                    Rlayout_answerA.setBackgroundResource(R.drawable.img_answera);
                    Rlayout_answerB.setBackgroundResource(R.drawable.img_answerb);
                    Rlayout_answerC.setBackgroundResource(R.drawable.img_answerc);
                    Rlayout_answerD.setBackgroundResource(R.drawable.img_answerd);
                    tvAnswerA.setVisibility(View.VISIBLE);
                    tvAnswerB.setVisibility(View.VISIBLE);
                    tvAnswerC.setVisibility(View.VISIBLE);
                    tvAnswerD.setVisibility(View.VISIBLE);
                    cdt.start();
                    blClick = false;
                }


            }
        }, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.d("activity", "onDestroy");
        mp_soundtrack.stop();
        if (mp_question_audio != null) {
            mp_question_audio.stop();
            mp_question_audio.release();
        }
        if (mp_answer_true != null) {
            mp_answer_true.stop();
            mp_answer_true.release();
        }
        if (mp_answer != null) {
            mp_answer.stop();
            mp_answer.release();
        }
        if (mp_question != null) {
            mp_question.stop();
            mp_question.release();
        }
        if (mp_trogiup != null) {
            mp_trogiup.stop();
            mp_trogiup.release();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (blClick) {
            blPasue = false;
            nextQuestion1();
        } else {
            blPasue = false;
            cdt.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();


        cdt.pause();
        if (mp_question_audio != null && mp_question_audio.isPlaying()) {
            mp_question_audio.pause();
        }

        if (mp_answer_true != null) {
            if (mp_answer_true.isPlaying()) {
                mp_answer_true.pause();
            }
        }
        if (mp_answer != null) {
            if (mp_answer.isPlaying()) {
                mp_answer.pause();
            }
        }
        if (mp_question != null) {
            if (mp_question.isPlaying()) {
                mp_question.pause();
            }
        }
        if (mp_trogiup != null) {
            if (mp_trogiup.isPlaying()) {
                mp_trogiup.pause();
            }
        }
        mp_soundtrack.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if(blPasue){
//            if(!blClick){
//
//            }
//        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        deleteFile();
//        Log.d("activity", "onstop" + choseAnser);
    }


    @Override
    public void onBackPressed() {
        if (mp_question_audio != null) {
            mp_question_audio.stop();
        }
        dialogAnswer = new Dialog(PlayActivity.this, R.style.DialogCustomTheme);
        dialogAnswer.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAnswer.setContentView(R.layout.dialog_answer);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        tv_Answer = (TextView) dialogAnswer.findViewById(R.id.tv_Answer);
        Rlayout_D = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_D);
        Rlayout_S = (RelativeLayout) dialogAnswer.findViewById(R.id.Rlayout_S);
        tv_Answer.setText("Bạn muốn dừng cuộc chơi tại đây?");
        Rlayout_D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdt.cancel();
                blClick = true;
                money = tvDiem.getText().toString();
                Intent intent = new Intent(PlayActivity.this, EndGameActivity.class);
                intent.putExtra("diemso", ""+diemso);
                intent.putExtra("NumStop", String.valueOf(numberQues + 1));
                intent.putExtra("Money", money);
                intent.putExtra("Name", user_name);
                intent.putExtra("Avatar", user_url);
                intent.putExtra("Login", check_login);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.no_animation);
            }
        });
        Rlayout_S.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAnswer.cancel();
            }
        });
        dialogAnswer.setCanceledOnTouchOutside(false);
        dialogAnswer.show();

    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        blPasue = true;
//        Log.d("activity", "onUserLeaveHint");
    }


    private void deleteFile(){
        File dir = new File(Environment.getExternalStorageDirectory()+"/Ailatrieuphu");
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
        }
    }

    public void showFrame(){
        RelativeLayout frame = (RelativeLayout)findViewById(R.id.frame);
        frame.setVisibility(View.VISIBLE);
        frame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    public void hideFrame(){
        RelativeLayout frame = (RelativeLayout)findViewById(R.id.frame);
        frame.setVisibility(View.GONE);
        frame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }
}

