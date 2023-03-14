package com.abapp.soundplay.Helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.abapp.soundplay.R;

public class BasicMain {

    Context context;

    String userName = "amitbodaliya";
    String appName;
    String packageName;


    String youtubeLink = "https://www.youtube.com/channel/UCtQwaEXV7xQAPfz-kVnVHpg?sub_confirmation=1";
    String instagramLink = "https://www.instagram.com/" + userName;
    String facebookLink = "https://www.facebook.com/" + userName;
    String appStoreLinkWithoutPackageName = "https://play.google.com/store/apps/details?id=" ;
    String developerPageLink = "https://play.google.com/store/apps/dev?id=8441030182648980706";
    String privacyPolicyLink = "https://amitbodaliyaid.wixsite.com/tttgamepp";
    String appStoreLink;
    String shareText;



    public BasicMain(Context context) {
        this.context = context;
        packageName = context.getPackageName();
        appName = context.getString(R.string.app_name);


        appStoreLink = "https://play.google.com/store/apps/details?id=" + packageName;
        shareText = "Check Out this Awesome " + appName + " App It's FREE \n \n PlayStore Link :-  " +
                " https://play.google.com/store/apps/details?id=" + packageName;
    }



    public void openYoutube() { openLink(youtubeLink);}
    public void openFaceBook() { openLink(facebookLink);}
    public void openInstagram() { openLink(instagramLink);}
    public void openAppInPlay() {openLink(appStoreLink);}
    public void openAppInPlay(String appPackageName) {openLink(appStoreLinkWithoutPackageName + "" + appPackageName);}
    public void openMoreByDev() { openLink(developerPageLink);}
    public void shareAppLink() {shareApp(shareText);}
    public void privacyPolicy() {openLink(privacyPolicyLink);}

    void openLink(String link){
        Uri uri = Uri.parse("" + link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public void shareApp(String text){

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plane");
        String shareSub = context.getString(R.string.app_name);
        intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, "Share Using..."));

    }

    public  void sendMail(){
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse("mailto:"));
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"amitbodaliyaid@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "" + appName);
        i.putExtra(Intent.EXTRA_TEXT   , "");
        try {
            context.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context , "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }


}
