package app.trieuphumobi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import app.trieuphumobi.offline.getdata.OfflineGetDataActivity;
import data.LinkData;
import payment.SMSHelper;
import payment.ScratchCardHelper;


@SuppressWarnings("ALL")
public class SplashActivity extends Activity {

    public static String Access_Token = null;
    public static String Token_request = null;
    private static Boolean isVisible = false;
    volatile Boolean stopThread;
    private Boolean CanBackPress;

    private Boolean toNotify = false;
    private Boolean login = false;
    private Boolean loading = true;
    private Intent intent;
    private ImageView imgLoading;
    private ArrayList<Drawable> drawables;
    private TextView tvName;
    private Button fb;
    private ImageView imgHelp;
    private String check_push;
    private int clickTrue, clickFalse;
    private boolean loadfn = false;
    private String open_fist;
    private String regid_sent = "";
    public static int width;
    public static int height;
    public static String user_request;
    private ArrayList<String> category = new ArrayList<>();
    private TextView tvHello;
    public boolean check_login;
    private RelativeLayout Rlayout_Choithu;
    private boolean checkInternet;
    private MediaPlayer mp_start;
    private Dialog dialog;
    private RelativeLayout Rlayout_Exit;
    private String userName = "Khách";
    private String userAvatar = "";
    private Dialog dialogExit;
    private TextView tv_Answer;
    private RelativeLayout Rlayout_D;
    private RelativeLayout Rlayout_S;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Integer tpmbTurn = 0;
    private RadioButton rd_viettel, rd_vina, rd_mobi;
    private TextView tvTurn;
    //   private TextView tvHello;
    ////////////////////////////////////////////////////
    // Show hide Progress when add coin
    ////////////////////////////////////////////////////
    private ProgressDialog mProgressDialog;
    private Dialog dialogCardSuccess;
    //facebook
    private LoginButton btnLogin;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private Profile profileLogin;
    public void openProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();
    }

    public void dissmissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private Dialog dialogCoin;
    private Dialog dialogCard;



    public static void clearSharepreference(Context mContext) {
        SharedPreferences pre = mContext.getSharedPreferences("my_data1", MODE_PRIVATE);
        // tạo đối tượng Editor để lưu thay đổi
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("login", false);
        editor.putString("access_token", "");
        editor.putString("token_user", "");
        editor.putString("user_request", "");
        editor.commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clickTrue = clickFalse = 0;
        // Toast.makeText(getApplication(), "Click true-1"+clickTrue, Toast.LENGTH_SHORT).show();
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        //

        CanBackPress = true;
        stopThread = false;
        intent = new Intent(SplashActivity.this, LoadingActivity.class);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_splash);
        callbackManager = CallbackManager.Factory.create();
        Boolean checkFirst = getSharedPreferences("TPMBSHAF", MODE_PRIVATE).getBoolean("tpmbfirst", true);

        if (checkFirst) {
            getSharedPreferences("TPMBSHAF", MODE_PRIVATE).edit().putInt("tpmbTurn", 5).commit();
            tpmbTurn = 5;
            getSharedPreferences("TPMBSHAF", MODE_PRIVATE).edit().putBoolean("tpmbfirst", false).commit();

        } else {
            tpmbTurn = getSharedPreferences("TPMBSHAF", MODE_PRIVATE).getInt("tpmbTurn", 5);
        }


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Mobi Start");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "StartGame");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        imgHelp = (ImageView) findViewById(R.id.imgHelp);
        btnLogin = (LoginButton) findViewById(R.id.login_button);
        tvName = (TextView) findViewById(R.id.tvName);
        tvHello = (TextView) findViewById(R.id.tvHello);
        tvTurn = (TextView) findViewById(R.id.tvTurn);
        fb = (Button) findViewById(R.id.fb);
        if (isLoggedIn()) {
            fb.setBackgroundResource(R.drawable.btn_logout_1);
            tvName.setVisibility(View.VISIBLE);
            tvHello.setVisibility(View.VISIBLE);
            check_login = true;
            userName = getSharedPreferences("TPMBSHAFGG", MODE_PRIVATE).getString("userName", "Khách");
            userAvatar = getSharedPreferences("TPMBSHAFGG", MODE_PRIVATE).getString("userAvatar", "");
            tvName.setText(userName);
        } else {
            fb.setBackgroundResource(R.drawable.select_btn_login);
            tvName.setVisibility(View.GONE);
            tvHello.setVisibility(View.GONE);
            check_login = false;
            userName = "Khách";
            userAvatar = "";
        }
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                Log.d("onCurrentAccessTokenChanged", oldToken + "\n" + newToken);
                if (newToken == null) {
                    fb.setBackgroundResource(R.drawable.btn_login_1);
                    tvName.setVisibility(View.GONE);
                    tvHello.setVisibility(View.GONE);
                    check_login = false;
                    userName = "Khách";
                    userAvatar = "";
                    getSharedPreferences("TPMBSHAFGG", MODE_PRIVATE).edit().putString("userName", userName).commit();
                    getSharedPreferences("TPMBSHAFGG", MODE_PRIVATE).edit().putString("userAvatar", userAvatar).commit();

                }
            }
        };
        btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String access_token = loginResult.getAccessToken().getToken();
                check_login = true;
                fb.setBackgroundResource(R.drawable.btn_logout_1);
                profileLogin = Profile.getCurrentProfile();
                userName = profileLogin.getName();
                userAvatar = "https://graph.facebook.com/"
                        + profileLogin.getId() + "/picture?type=large";
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Vtgroup");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Login FaceBook");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                tvName.setText(userName);
                tvName.setVisibility(View.VISIBLE);
                tvHello.setVisibility(View.VISIBLE);
                getSharedPreferences("TPMBSHAFGG", MODE_PRIVATE).edit().putString("userName", userName).commit();
                getSharedPreferences("TPMBSHAFGG", MODE_PRIVATE).edit().putString("userAvatar", userAvatar).commit();


            }

            @Override
            public void onCancel() {
                fb.setBackgroundResource(R.drawable.select_btn_login);
            }

            @Override
            public void onError(FacebookException error) {
                fb.setBackgroundResource(R.drawable.select_btn_login);
            }
        });
        Rlayout_Choithu = (RelativeLayout) findViewById(R.id.Rlayout_Choithu);
        mp_start = MediaPlayer.create(SplashActivity.this, R.raw.start);
        mp_start.start();
        if (tpmbTurn <= 0) {
            tvTurn.setText("Lượt chơi còn lại : " + 0);
            getSharedPreferences("TPMBSHAF", MODE_PRIVATE).edit().putInt("tpmbTurn", 0).commit();
            dialogCoin(getResources().getString(R.string.coinend));
        }
        tvTurn.setText("Lượt chơi còn lại : " + tpmbTurn);
        tvTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tpmbTurn > 0) {
                    dialogCoin(getResources().getString(R.string.coin));
                } else {
                    dialogCoin(getResources().getString(R.string.coinend));
                }

            }
        });
        imgHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialogHelp = new Dialog(SplashActivity.this, R.style.DialogCustomTheme);
                dialogHelp.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogHelp.setContentView(R.layout.dialog_help);
                getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
                RelativeLayout Rlayout_EXIT = (RelativeLayout) dialogHelp.findViewById(R.id.Rlayout_EXIT);
                RelativeLayout rl_Sms = (RelativeLayout) dialogHelp.findViewById(R.id.rl_Sms);


                Rlayout_EXIT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogHelp.dismiss();
                    }
                });
                rl_Sms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tpmbTurn > 0) {
                            dialogCoin(getResources().getString(R.string.coin));
                        } else {
                            dialogCoin(getResources().getString(R.string.coinend));
                        }

                        dialogHelp.dismiss();
                    }
                });
                dialogHelp.show();
            }
        });

        Rlayout_Choithu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tpmbTurn > 0) {
                    checkInternetConnection();
                    if (checkInternet == true) {
                        if (userName.equals("Khách")) {
                            tvName.setVisibility(View.GONE);
                            tvHello.setVisibility(View.GONE);

                        } else {
                            tvName.setVisibility(View.VISIBLE);
                            tvHello.setVisibility(View.VISIBLE);
                        }
                        Log.d("MainActivity", userAvatar + "\n" + userName);
                        Intent intent = new Intent(SplashActivity.this, LoadingActivity.class);
                        intent.putExtra("Name", userName);
                        intent.putExtra("Avatar", userAvatar);
                        intent.putExtra("Login", check_login);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in, R.anim.no_animation);
                        mp_start.stop();

                        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + LinkData.FOLDER_NAME;
                        File file = new File(file_path);
                        if (file.isDirectory()) {
                            String[] childe = file.list();
                            for (int i = 0; i < childe.length; i++) {
                                new File(file, childe[i]).delete();
                            }
                        }
                    } else
                        Toast.makeText(SplashActivity.this, "Không có kết nối Internet", Toast.LENGTH_LONG).show();

                } else {
                    dialogCoin(getResources().getString(R.string.coinend));
                }

            }
        });

    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
    public void onClick(View v) {
        checkInternetConnection();
        if (checkInternet == true) {
            if (v == fb) {
                btnLogin.performClick();
            }
        } else
            Toast.makeText(this, "Không có kết nối Internet", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
        mp_start.stop();
    }

    @Override
    public void onResume() {

        super.onResume();
        isVisible = true;
        mp_start.start();

    }

    @Override
    public void onPause() {
        super.onPause();
        isVisible = false;
        mp_start.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp_start.stop();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }




    public void saveSharepreference(String accessToken) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        // tạo đối tượng Editor để lưu thay đổi
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("login", true);
        editor.putString("access_token", accessToken);

        editor.commit();
    }

    public void saveShaf(String object, String value) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString(object, value);
        editor.commit();
    }

    public void saveTokenUser(String token_user) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("token_user", token_user);
        editor.commit();
    }

    public void saveFist(String token_user) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("open_fist", token_user);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        dialogExit = new Dialog(SplashActivity.this, R.style.DialogCustomTheme);
        dialogExit.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogExit.setContentView(R.layout.dialog_answer);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        tv_Answer = (TextView) dialogExit.findViewById(R.id.tv_Answer);
        Rlayout_D = (RelativeLayout) dialogExit.findViewById(R.id.Rlayout_D);
        Rlayout_S = (RelativeLayout) dialogExit.findViewById(R.id.Rlayout_S);
        tv_Answer.setText("Bạn muốn dừng cuộc chơi tại đây?");
        Rlayout_D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
        Rlayout_S.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogExit.cancel();
            }
        });
        dialogExit.show();
    }

    public void DialogNotify(final String title, final String message) {
        if (isVisible == false)
            return;

        final AlertDialog.Builder dlg;
        dlg = new AlertDialog.Builder(this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dlgAlert = dlg.create();
                dlgAlert.setTitle(title);
                dlgAlert.setButton(DialogInterface.BUTTON_POSITIVE,
                        (CharSequence) "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dlgAlert.setMessage(message);
                dlgAlert.setCancelable(false);
                dlgAlert.show();
            }
        });
    }


    public void getSharePref1() {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        open_fist = pre.getString("open_fist", "");

    }

    public void saveSharf(String object, String value) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString(object, value);
        editor.commit();
    }

    public void checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo nifto = cm.getActiveNetworkInfo();
        if (nifto != null && nifto.isConnected()) {
//            Toast.makeText(context,"Đã kết nối",Toast.LENGTH_SHORT).show();
            checkInternet = true;
        } else {
//            Toast.makeText(this, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();
            checkInternet = false;
            dialog = new Dialog(SplashActivity.this, R.style.DialogCustomTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_internet2);
//            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
//                    WindowManager.LayoutParams.MATCH_PARENT);
            Rlayout_Exit = (RelativeLayout) dialog.findViewById(R.id.Rlayout_EXIT);
            Rlayout_Exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.exit(0);
                }
            });
            RelativeLayout relativeLayout = (RelativeLayout)dialog.findViewById(R.id.Rlayout_OFF);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userName.equals("Khách")) {
                        tvName.setVisibility(View.GONE);
                        tvHello.setVisibility(View.GONE);

                    } else {
                        tvName.setVisibility(View.VISIBLE);
                        tvHello.setVisibility(View.VISIBLE);
                    }
                    Log.d("MainActivity", userAvatar + "\n" + userName);
                    Intent intent = new Intent(getBaseContext(), OfflineGetDataActivity.class);
                    intent.putExtra("Name", userName);
                    intent.putExtra("Avatar", userAvatar);
                    intent.putExtra("Login", check_login);
                    overridePendingTransition(R.anim.right_in, R.anim.no_animation);
                    mp_start.stop();
                    dialog.cancel();
                    startActivity(intent);
                }
            });
            dialog.show();
        }
    }

    public void dialogCoin(String str) {
        dialogCoin = new Dialog(SplashActivity.this, R.style.DialogCustomTheme);
        dialogCoin.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCoin.setContentView(R.layout.dialog_coin);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        TextView tvCoin = (TextView) dialogCoin.findViewById(R.id.tvCoin);
        tvCoin.setText(str);
        RelativeLayout rl_C = (RelativeLayout) dialogCoin.findViewById(R.id.rl_C);
        RelativeLayout rl_K = (RelativeLayout) dialogCoin.findViewById(R.id.rl_K);
        RelativeLayout rl_Card = (RelativeLayout) dialogCoin.findViewById(R.id.rl_Card);


        rl_C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sms();

            }
        });
        rl_K.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCoin.dismiss();
            }
        });
        rl_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCard();
                dialogCoin.dismiss();
            }
        });
        dialogCoin.show();
    }

    public void dialogCard() {
        dialogCard = new Dialog(SplashActivity.this, R.style.DialogCustomTheme);
        dialogCard.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCard.setContentView(R.layout.dialog_card);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        RelativeLayout rl_C = (RelativeLayout) dialogCard.findViewById(R.id.rl_C);
        RelativeLayout rl_K = (RelativeLayout) dialogCard.findViewById(R.id.rl_K);
        RelativeLayout rl_Card = (RelativeLayout) dialogCard.findViewById(R.id.rl_Card);
        final EditText edtCode = (EditText) dialogCard.findViewById(R.id.edtCode);
        final EditText edtId = (EditText) dialogCard.findViewById(R.id.edtId);
        rd_mobi = (RadioButton) dialogCard.findViewById(R.id.rb_mobi);
        rd_viettel = (RadioButton) dialogCard.findViewById(R.id.rb_viettel);
        rd_vina = (RadioButton) dialogCard.findViewById(R.id.rb_vina);

        rl_C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vendor = "";
                if (rd_viettel.isChecked()) {
                    vendor = "viettel";
                } else if (rd_vina.isChecked()) {
                    vendor = "vinaphone";
                } else if (rd_mobi.isChecked()) {
                    vendor = "mobifone";
                }
                if (vendor.length() == 0) {
                    Toast.makeText(SplashActivity.this, "Mời bạn chọn loại thẻ nạp.", Toast.LENGTH_SHORT).show();
                } else if (edtCode.getText().toString().length() == 0 || edtId.getText().toString().length() == 0) {
                    Toast.makeText(SplashActivity.this, "Bạn phải nhập đầy đủ mã thẻ và số seri.", Toast.LENGTH_SHORT).show();
                } else {
                    card(vendor, edtCode.getText().toString(), edtId.getText().toString());

                }

            }
        });
        rl_K.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCard.dismiss();
            }
        });
        rl_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tpmbTurn > 0) {
                    dialogCoin(getResources().getString(R.string.coin));
                } else {
                    dialogCoin(getResources().getString(R.string.coinend));
                }

                dialogCard.dismiss();
            }
        });
        dialogCard.show();
    }

    public void dialogCardSuccess(String content) {


        dialogCardSuccess = new Dialog(SplashActivity.this, R.style.DialogCustomTheme);
        dialogCardSuccess.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCardSuccess.setContentView(R.layout.dialog_card_success);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        TextView tvSuccess = (TextView) dialogCardSuccess.findViewById(R.id.tvSuccess);
        RelativeLayout rl_D = (RelativeLayout) dialogCardSuccess.findViewById(R.id.rl_D);
        RelativeLayout rl_Card = (RelativeLayout) dialogCardSuccess.findViewById(R.id.rl_Card);
        tvSuccess.setText(content);
        rl_D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCardSuccess.dismiss();
            }
        });
        rl_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCard();
                dialogCardSuccess.dismiss();
            }
        });
        dialogCardSuccess.show();
    }

    ////////////////////////////////////////////////////
    // Payment by SMS
    ////////////////////////////////////////////////////
    private SMSHelper.SendSMSListener smsListener = new SMSHelper.SendSMSListener() {
        @Override
        public void done(String phoneNumber, String content, String result) {
            if (result.equals(SMSHelper.SUCCESS)) {
                smsDone(true, null);
            } else {
                smsDone(false, result);
            }
        }
    };

    private void sms() {
        String content = SMSHelper.getSMSContent(this);
        Log.d("contentsms1", content);
        if (content == null) {
            return;
        }
        openProgressDialog();
        SMSHelper.sendSMS(this, SMSHelper.PHONE_NUMBER_9029, content, smsListener);
    }

    private void smsDone(boolean success, String erMsg) {
        dissmissProgressDialog();
        String msg = success ? "Payment SMS success" : ("Payment SMS failed " + erMsg);
        if (success) {
            tpmbTurn = getSharedPreferences("TPMBSHAF", MODE_PRIVATE).getInt("tpmbTurn", 5);
            tpmbTurn = tpmbTurn + 30;
            getSharedPreferences("TPMBSHAF", MODE_PRIVATE).edit().putInt("tpmbTurn", tpmbTurn).commit();
            tvTurn.setText("Lượt chơi còn lại : " + tpmbTurn);
            Toast.makeText(SplashActivity.this, "Bạn có " + tpmbTurn + " lượt chơi.", Toast.LENGTH_SHORT).show();
            dialogCoin.dismiss();
        } else {
            Toast.makeText(SplashActivity.this, "Rất tiếc, gửi tin không thành công.", Toast.LENGTH_SHORT).show();
        }
    }


    ////////////////////////////////////////////////////
    // Payment by Card
    ////////////////////////////////////////////////////


    private ScratchCardHelper.Listener cardListener = new ScratchCardHelper.Listener() {
        @Override
        public void onFinished(ScratchCardHelper.ModelResponse model) {
            if (model == null || model.responseCode != 200) {
                Log.d("sentsms", "false1");
                cardDone(false, -1, model == null ? "error" : model.toString());
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(model.responseJson);
                int status = jsonObject.getInt("status");
                String description = jsonObject.getString("description");
                double amount = jsonObject.getDouble("amount");
                if (status == 0) {
                    Log.d("sentsms", "true");
                    cardDone(true, (int) amount, null);
                } else {
                    Log.d("sentsms", "false2");
                    cardDone(false, -1, description);
                }
            } catch (Exception e) {
                Log.d("sentsms", "false3");
                cardDone(false, -1, e.getClass().getSimpleName());
            }
        }
    };

    private void card(String vendor, String code, String serial) {
        ScratchCardHelper card = ScratchCardHelper.getInstance(this, cardListener);
        openProgressDialog();
        card.requestPayment(vendor, code, serial);

    }

    private void cardDone(boolean success, int amount, String erMsg) {
        Log.d("AddCard",success+" "+amount+" "+erMsg);
        dissmissProgressDialog();
        if (success) {

            String content = converCard(amount);
            dialogCard.dismiss();
            dialogCardSuccess(content);
        } else {
            final Dialog dialog = new Dialog(SplashActivity.this, R.style.DialogCustomTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_card_error);
            RelativeLayout relativeLayout = (RelativeLayout)dialog.findViewById(R.id.Rlayout_EXIT);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            Toast.makeText(SplashActivity.this, "Nạp thẻ thất bại, xin vui lòng kiểm tra lại thông tin", Toast.LENGTH_SHORT).show();
        }

    }


    ////////////////////////////////////////////////////
    // Handle permission for Android 6
    ////////////////////////////////////////////////////
    public static final int REQUEST_CODE_PERMISSION = 100;
    private SparseArray<RequestPermissionListener> reqPemListsners;

    public interface RequestPermissionListener {
        void done(String[] permissions, int[] grantResults);
    }

    public void addRequestPermissionsListener(RequestPermissionListener l, int code) {
        if (reqPemListsners == null) {
            reqPemListsners = new SparseArray<>();
        }
        reqPemListsners.put(code, l);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        RequestPermissionListener listener = reqPemListsners.get(requestCode);
        if (listener != null) {
            listener.done(permissions, grantResults);
            reqPemListsners.remove(requestCode);
        }
    }

    public String converCard(int amt) {

        final int AMT_500K = 500000;
        final int AMT_200K = 200000;
        final int AMT_100K = 100000;
        final int AMT_50K = 50000;
        final int AMT_20K = 20000;
        final int AMT_10K = 10000;
        double num = (amt / (double) AMT_10K) * 15.0f;
        Log.d("convertcard0", num + "");
        double percent;
        if (amt < AMT_20K) {
            percent = 0;
        } else if (amt >= AMT_20K && amt < AMT_50K) {
            percent = 0.1f;
        } else if (amt >= AMT_50K && amt < AMT_100K) {
            percent = 0.15f;
        } else if (amt >= AMT_100K && amt < AMT_200K) {
            percent = 0.2f;
        } else if (amt >= AMT_200K && amt < AMT_500K) {
            percent = 0.25f;
        } else {
            percent = 0.3f;
        }
        num = num + num * percent;
        num = Math.ceil(num);
        Double d = new Double(num);
        int i = d.intValue();
        tpmbTurn = getSharedPreferences("TPMBSHAF", MODE_PRIVATE).getInt("tpmbTurn", 0);
        tpmbTurn = tpmbTurn + i;
        getSharedPreferences("TPMBSHAF", MODE_PRIVATE).edit().putInt("tpmbTurn", tpmbTurn).commit();
        tvTurn.setText("Lượt chơi còn lại : " + tpmbTurn);
        String price = new DecimalFormat("##,### VNĐ").format(amt);
        String msg = "Bạn đã mua thêm " + String.format("%.0f", num) + " lượt chơi bằng thẻ cào mệnh giá " + price;
        if (percent > 0) {
            int percent1 = (int) (percent * 100);
            msg += ", khuyến mãi " + percent1 + "%";
        }
        msg += ".\nHiện tại bạn có " + tpmbTurn + " lượt chơi.";
        msg += ".\nXin chúc mừng ^^";
        msg += ".\nBạn có muốn mua thêm lươt chơi bằng thẻ cào không?";
        return msg;
    }
}
