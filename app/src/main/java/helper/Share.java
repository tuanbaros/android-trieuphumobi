package helper;

import java.util.List;


import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;




public class Share {
	private Context mcon;
	public static void shareFb(Context context, String url,String title) {

		
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

	public static void shareMail(Context context, String url, String title) {

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setClassName("com.google.android.gm",
				"com.google.android.gm.ComposeActivityGmail");
		intent.setType("plain/text");
		intent.putExtra(Intent.EXTRA_EMAIL, "emailaddress@emailaddress.com");
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		intent.putExtra(Intent.EXTRA_TEXT, url);

		context.startActivity(Intent.createChooser(intent, "Send Email..."));
	}

	public static void shareTw(Context context,final String url,final String title) {
		Intent tweetIntent = new Intent(Intent.ACTION_SEND);
		tweetIntent.putExtra(Intent.EXTRA_TEXT, title + "\n" + url);
		tweetIntent.setType("text/plain");

		PackageManager packManager = context.getPackageManager();
		List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(
				tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

		boolean resolved = false;
		for (ResolveInfo resolveInfo : resolvedInfoList) {
			if (resolveInfo.activityInfo.packageName
					.startsWith("com.twitter.android")) {
				tweetIntent.setClassName(resolveInfo.activityInfo.packageName,
						resolveInfo.activityInfo.name);
				resolved = true;
				break;
			}
		}
		if (resolved) {
			context.startActivity(tweetIntent);
		} else {
			Intent i = new Intent();
			i.putExtra(Intent.EXTRA_TEXT, "http://google.com");
			i.setAction(Intent.ACTION_VIEW);
			String str=title+"\n"+url;
			Intent intent = i.setData(Uri
					.parse("https://twitter.com/intent/tweet?text="+str+"&via=Bizlive"));
			context.startActivity(i);

		}

	}

	public static void shareSMS(Context contenxt, String url,String title) {

		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		sendIntent.putExtra("sms_body", title+"\n"+url);
		sendIntent.setType("vnd.android-dir/mms-sms");
		contenxt.startActivity(sendIntent);
	}

//	public static void shareGg(Context context, String url,String title) {
//		Intent shareIntent = new PlusShare.Builder(context)
//				.setType("text/plain").setText(title+"\n"+url)
//				.setContentUrl(Uri.parse("https://developers.google.com/+/"))
//				.getIntent();
//
//		context.startActivity(shareIntent);
//	}

	public static void shareWeb(Context context, String url) {

		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		context.startActivity(i);
	}
	public static void shareKhac(Context context,String url,String title){
		Intent share = new Intent(Intent.ACTION_SEND);
	    share.setType("text/plain");
	    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	 
	    // Add data to the intent, the receiving app will decide
	    // what to do with it.
	    share.putExtra(Intent.EXTRA_SUBJECT, title);
	    share.putExtra(Intent.EXTRA_TEXT, url);
	 
	    context.startActivity(Intent.createChooser(share, "Share link!"));
	}
	public static void copyLink(Context context, String url) {
		((ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE))
				.setText(url);

	}
}
