package payment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;

import java.lang.reflect.Method;
import java.util.List;

import app.trieuphumobi.SplashActivity;

/**
 * Created by quang on 5/15/16.
 * <p/>
 * Reference from internet
 */
public class SMSHelper {
    public static final String TAG = "SMSHelper";
    public static final String SUCCESS = "Success";
    public static final String ERR_NOT_PEM = "ERR_NOT_PEM";
    private static final String MODEL_PHILIPS_T939 = "Philips T939";
    private static BroadcastReceiver smsSentReceiver = null;
    // CONTENT SMS
    private static final String MOBISTAR_CONTENT_VITTEL = "MW %d000 OT4 NAP ml-altp-allmobi";
    private static final String MOBISTAR_CONTENT_VINA_MOBI = "MW OT4 NAP%d ml-altp-allmobi";
    private static final String COMSOFT_CONTENT_VITTEL = "MW %d000 OT7 NAP ml-altp-vtgroup";
    private static final String COMSOFT_CONTENT_VINA_MOBI = "MW OT7 NAP%d ml-altp-vtgroup";
    // Sim Operator
    public static final String SO_Viettel = "45204";
    public static final String SO_Vina = "45201";
    public static final String SO_Mobi = "45202";

    public static final String VENDOR_VIETTEL = "viettel";
    public static final String VENDOR_VINAPHONE = "vinaphone";
    public static final String VENDOR_MOBIFONE = "mobifone";
    public static final String VENDOR_UNKNOWN = "unknown vendor";

    public enum Distributor {
        Amb,        // mobiistar
        Vgroup      // comsoft
    }

    public static final Distributor DISTRIBUTOR = Distributor.Amb;


    public static final String PHONE_NUMBER_9029 = "9029";

    // Lấy nhà mạng sim đang dùng
    public static String getCarrierCode(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simOperator = telephonyManager.getSimOperator();

        if (simOperator == null) {
            return null;
        }

        switch (simOperator) {
            case SO_Viettel:
                return VENDOR_VIETTEL;
            case SO_Vina:
                return VENDOR_VINAPHONE;
            case SO_Mobi:
                return VENDOR_MOBIFONE;
            default:
                return null;
        }
    }

    public static String getSMSContent(Context context) {
        switch (DISTRIBUTOR) {
            case Amb:
                return getSMSContentMobiistar(context);
            case Vgroup:
                return getSMSContentComsoft(context);
            default:
                return null;
        }
    }

    public static final int MONEY_PER_SMS = 20;    // 20K VND
    public static final int DIAMOND_NUM_EACH_SMS = 30;

    private static String getSMSContentMobiistar(Context context) {
        String content;
        String carrier = getCarrierCode(context);
        if (carrier == null) {
            return null;
        }
        switch (carrier) {
            case VENDOR_VIETTEL:
//                content = String.format("MW %d000 OT4 NAP THONGTINKHAC", MONEY_PER_SMS);
                content = String.format(MOBISTAR_CONTENT_VITTEL, MONEY_PER_SMS);
                Log.d("contentSMS", content);
                break;
            case VENDOR_VINAPHONE:
            case VENDOR_MOBIFONE:
//                content = String.format("MW OT4 NAP%d THONGTINKHAC", MONEY_PER_SMS);
                content = String.format(MOBISTAR_CONTENT_VINA_MOBI, MONEY_PER_SMS);
                break;
            default:
                content = null;
        }
        return content;
    }

    public static boolean smsAvailable(Context context) {
        return getCarrierCode(context) != null;
    }

    private static String getSMSContentComsoft(Context context) {
        String content;
        String carrier = getCarrierCode(context);
        if (carrier == null) {
            return null;
        }
        switch (carrier) {
            case VENDOR_VIETTEL:
//                content = String.format("MW %d000 OT7 NAP THONGTINKHAC", MONEY_PER_SMS);
                content = String.format(COMSOFT_CONTENT_VITTEL, MONEY_PER_SMS);
                break;
            case VENDOR_VINAPHONE:
            case VENDOR_MOBIFONE:
//                content = String.format("MW OT7 NAP%d THONGTINKHAC", MONEY_PER_SMS);
                content = String.format(COMSOFT_CONTENT_VINA_MOBI, MONEY_PER_SMS);
                break;
            default:
                content = null;
        }
        return content;
    }

    private static SparseArray<String> smsManagerErr;

    static {
        smsManagerErr = new SparseArray<>();
        smsManagerErr.put(SmsManager.RESULT_ERROR_GENERIC_FAILURE, "Transmission failed");
        smsManagerErr.put(SmsManager.RESULT_ERROR_RADIO_OFF, "Radio off");
        smsManagerErr.put(SmsManager.RESULT_ERROR_NULL_PDU, "No PDU defined");
        smsManagerErr.put(SmsManager.RESULT_ERROR_NO_SERVICE, "No service");
    }

    private static void listenSmsReceiving(Context context, final InternalListener internalListener) {
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String result;
                String phoneNum = intent.getStringExtra("phone_number");
                String content = intent.getStringExtra("sms_text");
                int resultCode = getResultCode();
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        result = SUCCESS;
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        result = smsManagerErr.get(resultCode);
                        break;
                    default:
                        result = "Unknown error";
                }
                context.unregisterReceiver(smsSentReceiver);
                smsSentReceiver = null;
                internalListener.done(resultCode == Activity.RESULT_OK, phoneNum, content, result);
            }
        };
        context.registerReceiver(smsSentReceiver, new IntentFilter("SMS_SENT"));
    }


    public interface SendSMSListener {
        void done(String phoneNumber, String content, String result);
    }

    public static void sendSMS(final Context context, final String toNum, final String smsText, final SendSMSListener smsListener) {
        if (Build.VERSION.SDK_INT < 23) {
            sendSmsBelow23(context, 0, toNum, smsText, smsListener);
        } else {
            sendSms23(context, toNum, smsText, smsListener);
        }
    }

    private interface InternalListener {
        void done(boolean success, String phoneNumber, String content, String errMsg);
    }

    @SuppressWarnings("ConstantConditions")
    private static void sendSmsBelow23(final Context context, final int simID, final String toNum, final String smsText, final SendSMSListener smsListener) {
        Intent sentIntent = new Intent("SMS_SENT");
        sentIntent.putExtra("phone_number", toNum);
        sentIntent.putExtra("sms_text", smsText);
        PendingIntent piSent = PendingIntent.getBroadcast(context, 0, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        listenSmsReceiving(context, new InternalListener() {
            @Override
            public void done(boolean success, String phoneNumber, String content, String errMsg) {
                if (success) {
                    smsListener.done(phoneNumber, content, errMsg);
                } else {
                    if (simID == 0) {
                        sendSmsBelow23(context, 1, toNum, smsText, smsListener);
                    } else {
                        smsListener.done(phoneNumber, content, errMsg);
                    }
                }
            }
        });

        String centerNum = null;
        PendingIntent deliveryIntent = null;
        String name;
        try {
            if (simID == 0) {
                name = Build.MODEL.equals(MODEL_PHILIPS_T939) ? "isms0" : "isms";
            } else if (simID == 1) {
                name = Build.MODEL.equals(MODEL_PHILIPS_T939) ? "isms1" : "isms2";
            } else {
                throw new Exception("can not get service which for sim '" + simID + "', only 0,1 accepted as values");
            }
            Method method = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
            method.setAccessible(true);
            Object param = method.invoke(null, name);
            method = Class.forName("com.android.internal.telephony.ISms$Stub").getDeclaredMethod("asInterface", IBinder.class);
            method.setAccessible(true);
            Object stubObj = method.invoke(null, param);
            if (Build.VERSION.SDK_INT < 18) {
                method = stubObj.getClass().getMethod("sendText", String.class, String.class, String.class, PendingIntent.class, PendingIntent.class);
                method.invoke(stubObj, toNum, centerNum, smsText, piSent, deliveryIntent);
            } else {
                method = stubObj.getClass().getMethod("sendText", String.class, String.class, String.class, String.class, PendingIntent.class, PendingIntent.class);
                method.invoke(stubObj, context.getPackageName(), toNum, centerNum, smsText, piSent, deliveryIntent);
            }
        } catch (Exception e) {
            smsListener.done(toNum, smsText, "Error");
        }
    }

    private static List<SubscriptionInfo> listSubscriptionInfos;
    private static final String PEM_SEND_SMS = Manifest.permission.SEND_SMS;
    private static final String PEM_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;

    private static void sendSms23(final Context context, final String phoneNumber, final String content, final SendSMSListener smsListener) {
        checkPermissionIfNeeded(context, phoneNumber, content, smsListener);
    }

    @TargetApi(23)
    private static void checkPermissionIfNeeded(final Context context, final String phoneNumber, final String content, final SendSMSListener smsListener) {
        int pem1 = ActivityCompat.checkSelfPermission(context, PEM_PHONE_STATE);
        int pem2 = ActivityCompat.checkSelfPermission(context, PEM_SEND_SMS);
        if (pem1 != PackageManager.PERMISSION_GRANTED || pem2 != PackageManager.PERMISSION_GRANTED) {
            SplashActivity act = (SplashActivity) context;
            act.addRequestPermissionsListener(new SplashActivity.RequestPermissionListener() {
                @Override
                public void done(String[] permissions, int[] grantResults) {
                    for (int pem : grantResults) {
                        if (pem != PackageManager.PERMISSION_GRANTED) {
                            smsListener.done(phoneNumber, content, ERR_NOT_PEM);
                            return;
                        }
                    }
                    smsPermissionGranted(context, phoneNumber, content, smsListener);
                }
            }, SplashActivity.REQUEST_CODE_PERMISSION);
            act.requestPermissions(new String[]{PEM_PHONE_STATE, PEM_SEND_SMS}, SplashActivity.REQUEST_CODE_PERMISSION);
        }
    }

    @TargetApi(23)
    private static void smsPermissionGranted(Context context, String phoneNumber, String content, SendSMSListener smsListener) {
        SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
        listSubscriptionInfos = subscriptionManager.getActiveSubscriptionInfoList();
        _sendSms23(context, phoneNumber, content, smsListener);
    }

    @TargetApi(23)
    private static void _sendSms23(final Context context, String phoneNumber, String content, final SendSMSListener smsListener) {
        if (listSubscriptionInfos != null && listSubscriptionInfos.size() > 0) {
            SubscriptionInfo sinfo = listSubscriptionInfos.remove(0);
            int sid = sinfo.getSubscriptionId();
            Intent sentIntent = new Intent("SMS_SENT");
            sentIntent.putExtra("phone_number", phoneNumber);
            sentIntent.putExtra("sms_text", content);
            PendingIntent piSent = PendingIntent.getBroadcast(context, 0, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            listenSmsReceiving(context, new InternalListener() {
                @Override
                public void done(boolean success, String phoneNumber, String content, String errMsg) {
                    if (success) {
                        smsListener.done(phoneNumber, content, errMsg);
                    } else {
                        _sendSms23(context, phoneNumber, content, smsListener);
                    }
                }
            });

            SmsManager sm = SmsManager.getSmsManagerForSubscriptionId(sid);
            sm.sendTextMessage(phoneNumber, null, content, piSent, null);
        } else {
            smsListener.done(phoneNumber, content, "Error");
        }
    }

}
