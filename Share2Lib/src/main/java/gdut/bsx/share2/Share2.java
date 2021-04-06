package gdut.bsx.share2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
//import android.support.annotation.NonNull;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Share2
 *
 * @author baishixian
 * @date 2018/3/29 11:00
 */

public class Share2 {

    private static final String TAG = "Share2";

    /**
     * Current activity
     */
    private Activity activity;

    /**
     * Share content type
     */
    private @ShareContentType String contentType;

    /**
     * Share mode type
     */
    private int modeType;

    /**
     * Share title
     */
    private String title;

    /**
     * Share file Uri
     */
    private Uri shareFileUri;

    /**
     * share file Uri list
     */
    private ArrayList<Uri> shareFileUriList;

    /**
     * Share content text
     */
    private String contentText;

    /**
     * Share to special component PackageName
     */
    private String componentPackageName;

    /**
     * Share to special component ClassName
     */
    private String componentClassName;

    /**
     * Share complete onActivityResult requestCode
     */
    private int requestCode;

    /**
     * Forced Use System Chooser
     */
    private boolean forcedUseSystemChooser;

    private Share2(@NonNull Builder builder) {
        this.activity = builder.activity;
        this.contentType = builder.contentType; //分享类型，图片|文本|文件等
        this.modeType = builder.modeType; //分享模式，单文件|多文件
        this.title = builder.title;
        this.shareFileUri = builder.shareFileUri; //单文件Uri
        this.shareFileUriList = builder.shareFileUriList; //多文件Uri列表
        this.contentText = builder.textContent;
        this.componentPackageName = builder.componentPackageName;
        this.componentClassName = builder.componentClassName;
        this.requestCode = builder.requestCode;
        this.forcedUseSystemChooser = builder.forcedUseSystemChooser;
    }

    /**
     * shareBySystem
     */
    public void shareBySystem () {
        if (checkShareParam()) {
            Intent shareIntent = createShareIntent();

            if (shareIntent == null) {
                Log.e(TAG, "shareBySystem cancel.");
                return;
            }

            if (title == null) {
                title = "";
            }

            if (forcedUseSystemChooser) {
                shareIntent = Intent.createChooser(shareIntent, title);
            }

            if (shareIntent.resolveActivity(activity.getPackageManager()) != null) {
                try {
                    if (requestCode != -1) {
                        activity.startActivityForResult(shareIntent, requestCode);
                    } else {
                        activity.startActivity(shareIntent);
                    }
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.addCategory("android.intent.category.DEFAULT");

        if (!TextUtils.isEmpty(this.componentPackageName) && !TextUtils.isEmpty(componentClassName)){
            ComponentName comp = new ComponentName(componentPackageName, componentClassName);
            shareIntent.setComponent(comp);
        }

        switch (contentType) {
            case ShareContentType.TEXT :
                shareIntent.putExtra(Intent.EXTRA_TEXT, contentText);
                shareIntent.setType("text/plain");
                break;
            case ShareContentType.IMAGE :
            case ShareContentType.AUDIO :
            case ShareContentType.VIDEO :
            case ShareContentType.FILE:
                if(modeType == ShareModeType.SINGLE){
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, shareFileUri);
                    Log.d(TAG, "Share uri: " + shareFileUri.toString());
                }else if(modeType == ShareModeType.MULTI){
                    if(shareFileUriList.size() > 1){
                        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, shareFileUriList);
                    }else{
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, shareFileUriList.get(0));
                    }

                }
                shareIntent.addCategory("android.intent.category.DEFAULT");
                shareIntent.setType(contentType);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        activity.grantUriPermission(packageName, shareFileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                }
                break;
            default:
                Log.e(TAG, contentType + " is not support share type.");
                shareIntent = null;
                break;
        }

        return shareIntent;
    }

    private boolean checkShareParam() {
        if (this.activity == null) {
            Log.e(TAG, "activity is null.");
            return false;
        }

        if (TextUtils.isEmpty(this.contentType)) {
            Log.e(TAG, "Share content type is empty.");
            return false;
        }

        if (ShareContentType.TEXT.equals(contentType)) {
            if (TextUtils.isEmpty(contentText)) {
                Log.e(TAG, "Share text context is empty.");
                return false;
            }
        } else {
            if (this.shareFileUri == null && this.shareFileUriList == null) {
                Log.e(TAG, "Share file path is null.");
                return false;
            }
        }

        return true;
    }

    public static class Builder {
        private Activity activity;
        private @ShareContentType String contentType = ShareContentType.FILE;
        private int modeType = ShareModeType.SINGLE;
        private String title;
        private String componentPackageName;
        private String componentClassName;
        private Uri shareFileUri;
        private ArrayList<Uri> shareFileUriList;
        private String textContent;
        private int requestCode = -1;
        private boolean forcedUseSystemChooser = true;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        /**
         * Set Content Type
         * @param contentType {@link ShareContentType}
         * @return Builder
         */
        public Builder setContentType(@ShareContentType String contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         * Set Mode Type
         * @param modeType
         * @return
         */
        public Builder setModeType(int modeType){
            this.modeType = modeType;
            return this;
        }

        /**
         * Set Title
         * @param title title
         * @return Builder
         */
        public Builder setTitle(@NonNull String title) {
            this.title = title;
            return this;
        }

        /**
         * Set share file path
         * @param shareFileUri shareFileUri
         * @return Builder
         */
        public Builder setShareFileUri(Uri shareFileUri) {
            this.shareFileUri = shareFileUri;
            return this;
        }

        /**
         * Set share file path list
         * @param list
         * @return
         */
        public Builder setShareFileUriList(ArrayList<Uri> list){
            this.shareFileUriList = list;
            return this;
        }

        /**
         * Set text content
         * @param textContent  textContent
         * @return Builder
         */
        public Builder setTextContent(String textContent) {
            this.textContent = textContent;
            return this;
        }

        /**
         * Set Share To Component
         * @param componentPackageName componentPackageName
         * @param componentClassName componentPackageName
         * @return Builder
         */
        public Builder setShareToComponent(String componentPackageName, String componentClassName) {
            this.componentPackageName = componentPackageName;
            this.componentClassName = componentClassName;
            return this;
        }

        /**
         * Set onActivityResult requestCode, default value is -1
         * @param requestCode requestCode
         * @return Builder
         */
        public Builder setOnActivityResult (int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        /**
         * Forced Use System Chooser To Share
         * @param enable default is true
         * @return Builder
         */
        public Builder forcedUseSystemChooser (boolean enable) {
            this.forcedUseSystemChooser = enable;
            return this;
        }

        /**
         * build
         * @return Share2
         */
        public Share2 build() {
            return new Share2(this);
        }

    }
}
