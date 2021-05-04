package com.wiyixiao.lzone.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wiyixiao.lzone.R;
import com.wiyixiao.lzone.adapter.ShareItemAdapter;
import com.wiyixiao.lzone.data.Constants;
import com.wiyixiao.lzone.interfaces.IZipStateCallback;
import com.wiyixiao.lzone.utils.FileUtil;
import com.wiyixiao.lzone.utils.Utils;
import com.wiyixiao.lzone.utils.ZipHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;
import gdut.bsx.share2.ShareModeType;

public class ShareFileView extends Dialog {

    private final Context mContext;
    private ListView mListView;

    private Window mWindow;

    private Share2.Builder builder;
    private String mPath = "";
    private String mName;
    private String mUnZipFilePath = ""; //压缩完成的可解压文件(压缩包)路径

    private ShareItemAdapter mShareListViewAdapter;
    private ArrayList<String> mItemList = new ArrayList<>();
    private List<Integer> mIndexList = new ArrayList<>();
    private ArrayList<String> mSelectFileList = new ArrayList<>();

    public String getmUnZipFilePath(){
        return this.mUnZipFilePath;
    }

    public ShareFileView(Context context, String path, String name, ArrayList list) {
        super(context);
        this.mContext = context;
        this.mItemList = list;
        this.mPath = path;
        this.mName = name;

        initView();
        initListView();
    }

    private void initView() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View contentView = View.inflate(mContext, R.layout.dialog_share_listview, null);
        mListView = (ListView) contentView.findViewById(R.id.dialogLv);
        TextView mTextView = (TextView) contentView.findViewById(R.id.dialogTV);
        setContentView(contentView);

        mTextView.setText(R.string.NAL_share_title);

        mWindow = getWindow();
        assert mWindow != null;
        android.view.WindowManager.LayoutParams p = mWindow.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindow.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        p.height = (int) (displayMetrics.heightPixels * 0.8);
        p.width = (int) (displayMetrics.widthPixels * 0.85);
        mWindow.setAttributes(p);

        builder = new Share2.Builder((Activity)mContext);

        Button mShareBtn = (Button) findViewById(R.id.share_btn);
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mSelectFileList.size() <= 0){
                    Toast.makeText(mContext, "Please choose a file", Toast.LENGTH_LONG).show();
                    return;
                }else{

                    builder.setContentType(ShareContentType.FILE);
                    builder.setOnActivityResult(Constants.REQUEST_SHARE_FILE_CODE);
                    builder.setTitle(mContext.getResources().getString(R.string.NAL_share_title));

                    if(checkUseZip()){
                        //压缩文件列表
                        zipFileList();
                    }else{
                        builder.setModeType(ShareModeType.MULTI);

                        if(mSelectFileList.size() == 1){
                            if(FileUtil.isImageFile(mSelectFileList.get(0))) {
                                builder.setContentType(ShareContentType.IMAGE);
                            } else {
                                builder.setContentType(ShareContentType.FILE);
                            }
                        }

                        builder.setShareFileUriList(
                                gdut.bsx.share2.FileUtil.getFileUriList(mContext, mSelectFileList));
                        builder.build().shareBySystem();
                    }

                }
            }
        });

        Button mDeleteBtn = (Button) findViewById(R.id.delete_btn);
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //删除选中文件
                int deleteCount = mIndexList.size();

//                Collections.sort(mIndexList, Collections.reverseOrder()); //从大到小排序
                Collections.sort(mIndexList); //从小到大排序

                if(mShareListViewAdapter.getCount() > 0 && deleteCount > 0){
                    for(int i=deleteCount-1;i>=0;i--){
                        int index = mIndexList.get(i);
                        FileUtil.deleteSingleFile(mShareListViewAdapter.getItemTxt(index));
                        mShareListViewAdapter.removeItem(index);
                    }

                    initSelect();
                    initChecked();

                }else{
                    Toast.makeText(mContext, "Please choose a file", Toast.LENGTH_LONG).show();
                }
            }
        });

        TextView mCancelTV = (TextView) findViewById(R.id.cancel_tv);
        mCancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initListView() {
        mShareListViewAdapter = new ShareItemAdapter(mContext, mItemList);
        mListView.setAdapter(mShareListViewAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                boolean state = !mShareListViewAdapter.getSelect(position);

                if(state){
                    mIndexList.add(position);
                    mSelectFileList.add(mShareListViewAdapter.getItemTxt(position));
                }else {
                    mIndexList.remove(Integer.valueOf(position));
                    mSelectFileList.remove(mShareListViewAdapter.getItemTxt(position));
                }

                mShareListViewAdapter.setSelect(position, state);
                mShareListViewAdapter.notifyDataSetChanged();
            }
        });

    }

    private void initSelect(){
        mIndexList.clear();
        mSelectFileList.clear();
        mShareListViewAdapter.notifyDataSetChanged();
    }

    //重置复选框选中状态
    private void initChecked(){
        int count = mShareListViewAdapter.getCount();

        if(count <= 0) {
            return;
        }

        for(int i=0;i<count;i++){
            mShareListViewAdapter.setSelect(i, false);
        }

        mShareListViewAdapter.notifyDataSetChanged();
    }

    //检测是否需要合并压缩
    private boolean checkUseZip(){
        int binaryCount = 0;

        if(mSelectFileList.size() > 1){
            for (String s: mSelectFileList
                 ) {

                if(!FileUtil.isImageFile(s)){
                    binaryCount++;
                }

                if(binaryCount >= 1) {
                    return true;
                }
            }
        }else{
            return false;
        }

        return false;
    }

    //压缩文件列表
    private void zipFileList(){
        mUnZipFilePath = "";
        //压缩文件以当前时间命名
        if(!TextUtils.isEmpty(mName)) {
            mName += '-';
        }
        mUnZipFilePath = mPath + mName + Utils.getSysTime() + ".zip";
        //使用zip压缩合并多个文件
        ZipHelper.zip(mContext, mUnZipFilePath, mSelectFileList, new IZipStateCallback() {
            @Override
            public void onFailed() {
                Toast.makeText(mContext, "File compression failed", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess() {
                //获取压缩文件Uri
                builder.setModeType(ShareModeType.SINGLE);
                builder.setShareFileUri(
                        gdut.bsx.share2.FileUtil.getFileUri(
                                mContext,
                                ShareContentType.FILE,
                                new File(mUnZipFilePath)));
                builder.build().shareBySystem();
            }
        });
    }
    /*+++++++++++++++++++++++++++++++++++set height++++++++++++++++++++++++++++++++++++++++*/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            return;
        }
//        Utils.setHeight(mWindow, mScale);
    }
}