package payment;

/**
 * Created by quang on 6/8/16.
 * <p/>
 * Reference from http://developers.1pay.vn/http-apis/card-charging
 */

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import app.trieuphumobi.R;


public class ScratchCardHelper {
    private static final String USER_AGENT = "Mozilla/5.0";
    //mobiistar
    private static final String REF = "ml-altp-allmobi";

    //vtgroup
//    private static final String REF = "ml-altp-vtgroup";


    private String access_key;
    private String secretKey;
    private String url;
    private Listener listener;

    public interface Listener {
        void onFinished(ModelResponse model);
    }

    public class ModelResponse {
        public int responseCode;
        public String responseJson;

        public String toString() {
            return String.format("Response code: %d, JSON: %s", responseCode, responseJson);
        }
    }

    private static ScratchCardHelper instance;

    private ScratchCardHelper(Context context) {
        url = context.getString(R.string.CARD_CHARGING_URL_1PAY);
        switch (SMSHelper.DISTRIBUTOR) {
            case Amb:
                access_key = context.getString(R.string.access_key_amb);
                secretKey = context.getString(R.string.secret_amb);
                break;

            case Vgroup:
                access_key = context.getString(R.string.access_key_vgroup);
                secretKey = context.getString(R.string.secret_vgroup);
                break;

            default:
                throw new IllegalStateException("Unknown distributor " + SMSHelper.DISTRIBUTOR);
        }
    }

    public static ScratchCardHelper getInstance(Context context, Listener listener) {
        if (instance == null) {
            instance = new ScratchCardHelper(context);
        }
        instance.listener = listener;
        return instance;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void requestPayment(String type, String pin, String serial) {
        new AsyncTask<String, String, ModelResponse>() {
            @Override
            protected ModelResponse doInBackground(String... params) {
                String type = params[0];
                String pin = params[1];
                String serial = params[2];
//                String transRef = randomString();
                String transRef = REF;
                try {
                    return sendPost(type, pin, serial, transRef);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ModelResponse response) {
                if (listener != null) {
                    listener.onFinished(response);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, type, pin, serial);
    }

    private String randomString() {
        final String RAND = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            builder.append(RAND.charAt(random.nextInt(RAND.length())));
        }
        return builder.toString();
    }

    private ModelResponse sendPost(String type, String pin, String serial, String transRef) throws Exception {
        String signature = generateSignature(access_key, type, pin, serial, transRef, secretKey);
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setConnectTimeout(70000);
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        String urlParameters = "access_key=%access_key%&type=%type%&pin=%pin%&serial=%serial%&transRef=%transRef%&signature=%signature%";
        urlParameters = urlParameters.replaceFirst("%access_key%", access_key); //access key do 1 pay cung cap
        urlParameters = urlParameters.replaceFirst("%type%", type);
        urlParameters = urlParameters.replaceFirst("%pin%", pin);
        urlParameters = urlParameters.replaceFirst("%serial%", serial);
        urlParameters = urlParameters.replaceFirst("%transRef%", transRef); //Mã giao dịch do MC tự sinh
        urlParameters = urlParameters.replaceFirst("%signature%", signature);

        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        ModelResponse model = new ModelResponse();
        model.responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        model.responseJson = response.toString();
        return model;
    }

    public String generateSignature(String access_key, String type, String pin, String serial, String transRef, String secret) {
        String signature = "";
        if ((access_key != null) && (type != null) && (pin != null) && (serial != null) && (transRef != null)) {
            String urlParameters = "access_key=%access_key%&pin=%pin%&serial=%serial%&transRef=%transRef%&type=%type%";
            urlParameters = urlParameters.replaceFirst("%access_key%", access_key);
            urlParameters = urlParameters.replaceFirst("%type%", type);
            urlParameters = urlParameters.replaceFirst("%pin%", pin);
            urlParameters = urlParameters.replaceFirst("%serial%", serial);
            urlParameters = urlParameters.replaceFirst("%transRef%", transRef);
            signature = hmacDigest(urlParameters, secret, "HmacSHA256");
        }
        return signature;
    }

    public static String hmacDigest(String msg, String keyString, String algo) {
        String digest = "";
        try {
            if (keyString != null && keyString.length() > 0) {
                SecretKeySpec key = new SecretKeySpec(
                        (keyString).getBytes("UTF-8"), algo);
                Mac mac = Mac.getInstance(algo);
                mac.init(key);
                byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));
                StringBuilder hash = new StringBuilder();
                for (byte b : bytes) {
                    String hex = Integer.toHexString(0xFF & b);
                    if (hex.length() == 1) {
                        hash.append('0');
                    }
                    hash.append(hex);
                }
                digest = hash.toString();
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return digest;
    }

    //{"transId": "0", "transRef": "mQnbto9a05", "serial": "sss", "amount":0, "status": "14", "description":"Thẻ không tồn tại"}
    //{"transId": "0011456796", "transRef": "kI9IIAxTqB", "serial": "36166100266683", "amount":20000, "status": "00", "description":"Giao dịch thành công."}


    private static final int AMT_500K = 500000;
    private static final int AMT_200K = 200000;
    private static final int AMT_100K = 100000;
    private static final int AMT_50K = 50000;
    private static final int AMT_20K = 20000;
    private static final int AMT_10K = 10000;

    public static TransactionInfo getTransactionInfo(double amt) {
        double num = (amt / (double) AMT_10K) * 15.0f;
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
        TransactionInfo info = new TransactionInfo();
        info.amt = (int) amt;
        info.percentDiscount = percent;
        info.diamondNum = (int) num;
        return info;
    }

    public static class TransactionInfo {
        public int amt;
        public double percentDiscount;
        public int diamondNum;

        public String toString() {
            String price = new DecimalFormat("##,### VNĐ").format(amt);
            String msg = "Bạn đã mua thêm " + diamondNum + " lượt chơi bằng thẻ cào mệnh giá " + price;
            if (percentDiscount > 0) {
                int percent = (int) (percentDiscount * 100);
                msg += ", khuyến mãi " + percent + "%";
            }
            msg += ".\nXin chúc mừng ^^";
            return msg;
        }
    }
}