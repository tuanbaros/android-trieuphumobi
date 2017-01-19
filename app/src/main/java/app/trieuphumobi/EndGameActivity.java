package app.trieuphumobi;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.siyamed.shapeimageview.HexagonImageView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import app.trieuphumobi.offline.getdata.OfflineGetDataActivity;
import payment.SMSHelper;
import payment.SMSHelperCompare;

/**
 * Created by Dang Truong on 09/05/2016.
 */
public class EndGameActivity extends Activity {
    RelativeLayout Rlayout_choilai, Rlayout_menu;
    TextView tvNguoichoi, tvNumStop, tvMoney, tvDn_dethachdau,tvScore;
    String numEnd, money;
    MediaPlayer mp_lose;
    Dialog dialog;
    TextView tv_Answer;
    RelativeLayout Rlayout_D, Rlayout_S;
    String user_name = "", user_url = "";
    HexagonImageView imgNguoichoi;
    Boolean check_login = false;
    private FirebaseAnalytics mFirebaseAnalytics;
    private ProgressDialog mProgressDialog;
    private Dialog dialogHelp;
    private RelativeLayout rl_share;
    private String url = "https://www.smarturl.it/ailatrieuphu";
    private String diemso;
    private ShareDialog shareDialog;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("choi lai", "choi lai");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_endgame);
        shareDialog=new ShareDialog(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Rlayout_choilai = (RelativeLayout) findViewById(R.id.Rlayout_choilai);
        Rlayout_menu = (RelativeLayout) findViewById(R.id.Rlayout_menu);
        rl_share = (RelativeLayout) findViewById(R.id.rl_share);
        tvScore= (TextView) findViewById(R.id.tvScore);
        tvNguoichoi = (TextView) findViewById(R.id.tvNguoichoi);
        tvNumStop = (TextView) findViewById(R.id.tvNumStop);
        tvMoney = (TextView) findViewById(R.id.tvMoney);
        tvDn_dethachdau = (TextView) findViewById(R.id.tvDn_dethachdau);
        imgNguoichoi = (HexagonImageView) findViewById(R.id.imgAvatar_End);

        Intent intent = getIntent();
        user_name = intent.getStringExtra("Name");
        user_url = intent.getStringExtra("Avatar");
        check_login = intent.getBooleanExtra("Login", false);
        diemso = intent.getStringExtra("diemso");
        numEnd = getIntent().getStringExtra("NumStop");
        money = getIntent().getStringExtra("Money");
        dialogCompare();
        saveHighlight(Integer.parseInt(numEnd), Integer.parseInt(money.replaceAll(",","")),Integer.parseInt(diemso));

        if (user_name != "") {
            tvNguoichoi.setText(user_name);
        } else tvNguoichoi.setText("Khách");

        if (user_url.equals("") || user_name.length() == 0 || user_name.equals(null)) {
            Picasso.with(getApplication()).load(R.drawable.ava)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.ava)
                    .fit()
                    .into(imgNguoichoi);
        } else {
            Picasso.with(getApplication()).load(user_url)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.ava)
                    .fit()
                    .into(imgNguoichoi);
        }



        tvNumStop.setText("Bạn đã dừng cuộc chơi ở câu hỏi số " + numEnd);
        tvScore.setText("Điểm số : "+diemso);
        tvMoney.setText(money);

        Rlayout_choilai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tpmbTurn = getSharedPreferences("TPMBSHAF", MODE_PRIVATE).getInt("tpmbTurn", 5);
                if (tpmbTurn > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Mobi Star");
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Chơi lại");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    Intent intent;
                    if (getIntent().getBooleanExtra("offline", false))
                        intent = new Intent(EndGameActivity.this, OfflineGetDataActivity.class);
                    else
                        intent = new Intent(EndGameActivity.this, LoadingActivity.class);
                    intent.putExtra("Name", user_name);
                    intent.putExtra("Avatar", user_url);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.no_animation);
                    mp_lose.stop();
                } else {
                    Intent intent = new Intent(EndGameActivity.this, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.no_animation);
                    mp_lose.stop();
                }

            }
        });

        Rlayout_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Mobi Star");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Trở lại Menu");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                Intent intent = new Intent(EndGameActivity.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("Name", user_name);
//                intent.putExtra("Avatar", user_url);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.no_animation);
                mp_lose.stop();
            }
        });
        rl_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareLinkContent content1 = new ShareLinkContent.Builder()
                        .setContentTitle("Triệu Phú Mobile")
                        .setContentDescription("Mình được " + money + " tiền và " + diemso + " điểm khi chơi game Triệu Phú Mobile - Âm thanh sống động.")
                        .setContentUrl(Uri.parse(url))
                        .setImageUrl(Uri.parse("http://d1f5d68hjoxbw7.cloudfront.net/image/content/content/banner/storegameandroid/147106809457aeb7beaa465.png"))
                        .build();
                shareDialog.show(content1);
            }
        });
        mp_lose = MediaPlayer.create(EndGameActivity.this, R.raw.lose);
        mp_lose.start();

    }

    @Override
    public void onBackPressed() {
        if (mp_lose != null) {
            mp_lose.stop();
        }
        dialog = new Dialog(EndGameActivity.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_answer);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        tv_Answer = (TextView) dialog.findViewById(R.id.tv_Answer);
        Rlayout_D = (RelativeLayout) dialog.findViewById(R.id.Rlayout_D);
        Rlayout_S = (RelativeLayout) dialog.findViewById(R.id.Rlayout_S);
        tv_Answer.setText("Bạn muốn trở về menu chính?");
        Rlayout_D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EndGameActivity.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.no_animation);
                mp_lose.stop();
            }
        });
        Rlayout_S.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();

    }

    public void dialogCompare() {
        dialogHelp = new Dialog(EndGameActivity.this, R.style.DialogCustomTheme);
        dialogHelp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogHelp.setContentView(R.layout.dialog_dodiem);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        RelativeLayout rl_C = (RelativeLayout) dialogHelp.findViewById(R.id.rl_C);
        RelativeLayout rl_K = (RelativeLayout) dialogHelp.findViewById(R.id.rl_K);
        TextView tvThongbao = (TextView) dialogHelp.findViewById(R.id.tvThongbao);
        tvThongbao.setText("Xin chúc mừng, bạn được "+diemso+" điểm! Bạn có muốn đọ điểm với cộng đồng không. Giá 5.000đ/SMS");

        rl_C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sms(Integer.parseInt(numEnd), Integer.parseInt(diemso));
            }
        });
        rl_K.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogHelp.dismiss();
            }
        });
        dialogHelp.show();
    }

    public void dialogSuccess() {
        final Dialog dialogSuccess = new Dialog(EndGameActivity.this, R.style.DialogCustomTheme);
        dialogSuccess.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSuccess.setContentView(R.layout.dialog_ketqua);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        RelativeLayout rl_C = (RelativeLayout) dialogSuccess.findViewById(R.id.rl_C);


        rl_C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSuccess.dismiss();
            }
        });

        dialogSuccess.show();
    }

    ////////////////////////////////////////////////////
    // Handle permission for Android 6
    ////////////////////////////////////////////////////
    public static final int REQUEST_CODE_PERMISSION = 100;
    private SparseArray<RequestPermissionListener> reqPemListsners;

    private void sms(int lv, int diem) {
        String content = SMSHelperCompare.getSMSContent(this, lv, diem);
//        Log.d("contentCompare", content);
        if (content == null) {
            return;
        }
        openProgressDialog();
        SMSHelperCompare.sendSMS(this, SMSHelper.PHONE_NUMBER_9029, content, smsListener);
    }

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

    private SMSHelperCompare.SendSMSListener smsListener = new SMSHelperCompare.SendSMSListener() {
        @Override
        public void done(String phoneNumber, String content, String result) {
            if (result.equals(SMSHelper.SUCCESS)) {
                smsDone(true, null);
            } else {
                smsDone(false, result);
            }
        }
    };

    private void smsDone(boolean success, String erMsg) {
        dissmissProgressDialog();
        String msg = success ? "Payment SMS success" : ("Payment SMS failed " + erMsg);
        Log.d("smsDone",msg);
        if (success) {
            dialogHelp.dismiss();
            dialogSuccess();
        } else {
            dialogHelp.dismiss();
            Toast.makeText(EndGameActivity.this, "Rất tiếc, gửi tin không thành công.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareFb(Context context, String url, String title) {


        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        // intent.putExtra(Intent.EXTRA_SUBJECT, "Foo bar"); // NB: has no effect!
        intent.putExtra(Intent.EXTRA_TEXT, title + "\n" + url);

        // See if official Facebook app is found
        boolean facebookAppFound = false;
        List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                intent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }

        // As fallback, launch sharer.php in a browser
        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + url;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }

        context.startActivity(intent);

    }
    public void saveHighlight(int newLv,int newMoney,int newScore){

        int oldLv=getSharedPreferences("TPMBSHAF", MODE_PRIVATE).getInt("tpmbLvhl", 0);

        if(newLv>oldLv){
//            getSharedPreferences("TPMBSHAF", MODE_PRIVATE).edit().putString("tpmbNamehl", newName);
            getSharedPreferences("TPMBSHAF", MODE_PRIVATE).edit().putInt("tpmbLvhl", newLv).commit();
            getSharedPreferences("TPMBSHAF", MODE_PRIVATE).edit().putInt("tpmbMoneyhl", newMoney).commit();
            getSharedPreferences("TPMBSHAF", MODE_PRIVATE).edit().putInt("tpmbScorehl", newScore).commit();
        }
    }
}
