package com.wiyixiao.lzone.views;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wiyixiao.lzone.MyApplication;
import com.wiyixiao.lzone.R;
import com.wiyixiao.lzone.adapter.KeysAdapter;
import com.wiyixiao.lzone.bean.KeyInfoBean;
import com.wiyixiao.lzone.interfaces.IKeyEditListener;
import com.wiyixiao.lzone.interfaces.IKeyPadListener;
import com.wiyixiao.lzone.utils.DisplayUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class KeyPadView extends LinearLayout {

    @BindView(R.id.recycleViewKeys)
    RecyclerView keysRv;
    @BindView(R.id.seekBarKeys)
    SeekBar keysSeekBar;
    @BindView(R.id.key_init_btn)
    Button keyInitBtn;

    private Context mContext;
    private MyApplication myApplication;
    private Unbinder unbinder;

    //是否开启按键配置模式
    private boolean cfgMode = false;
    //是否开启发送定时模式
    private boolean timingMode = false;

    //按键
    private KeysAdapter<KeyInfoBean> keysAdapter;
    private ArrayList<KeyInfoBean> keyArrayList;

    private IKeyPadListener keyPadListener;
    private KeyEditDialog keyEditDialog;

    private String ip;

    private IKeyEditListener iKeyEditListener = new IKeyEditListener() {
        @Override
        public void add(KeyInfoBean bean) {

            //非配置模式且不包含则添加、否则更新
            if(!cfgMode && !keyArrayList.contains(bean)){
                //获取当前按键数量
                int count = keyArrayList.size();

                //自增按键索引
                bean.setIndex(count);
                keyArrayList.add(bean);

                //添加数据库

            }else{
                //更新数据库

            }

            keysAdapter.notifyDataSetChanged();
            keyEditDialog.dismissDialog();
        }

        @Override
        public void remove(KeyInfoBean bean) {
            //包含该按键并且为配置模式，则进行按键移除操作
            if(cfgMode && keyArrayList.contains(bean)){
                keyArrayList.remove(bean);
                keysAdapter.notifyDataSetChanged();

                keyEditDialog.dismissDialog();
            }
        }
    };

    public KeyPadView(Context context) {
        super(context);
        //keyEditView = KeyEditView.getInstance(context);
        myApplication = (MyApplication)context.getApplicationContext();
        viewInit();
    }

    public KeyPadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        keyEditDialog = KeyEditDialog.getInstance(context, iKeyEditListener);
        myApplication = (MyApplication)context.getApplicationContext();
        viewInit();
    }

    public void keySetListener(Context context, IKeyPadListener listener){
        this.mContext = context;
        this.keyPadListener = listener;
    }

    public void keyShowEditDialog(){
        if(!cfgMode){
            keyEditDialog.showDialog(null);
        }
    }

    public void keyClear(){
        if(keyArrayList.size() > 0){
            keyArrayList.clear();
            keysAdapter.notifyDataSetChanged();

            //更新数据库

        }
    }

    private void viewInit(){
        View inflate = inflate(getContext(), R.layout.view_keypad, this);
        unbinder = ButterKnife.bind(this, inflate);

        initKeysView();
        initKeysSeekBar();
    }

    @Override
    protected  void finalize() throws Throwable{

        Log.e(myApplication.getTAG(), "按键控件资源释放");
        unbinder.unbind();
        keyEditDialog.destoryView();

        super.finalize();
    }

    /*****************************************GET/SET*****************************************/
    public boolean isCfgMode() {
        return cfgMode;
    }

    public void setCfgMode(boolean cfgMode) {
        this.cfgMode = cfgMode;
    }

    public boolean isTimingMode() {
        return timingMode;
    }

    public void setTimingMode(boolean timingMode) {
        this.timingMode = timingMode;
    }

    /*****************************************初始化*****************************************/
    private void initKeysView(){
        //读取数据库初始化按键数据
        keyArrayList = new ArrayList<KeyInfoBean>();

        //根据IP查询其对应的自定义按键数量，数量为0时显示恢复默认设置按钮
        if(false){
            keyInitBtn.setVisibility(View.VISIBLE);
        }

        for(int i=0;i<myApplication.keyDdefaultCount;i++){
            KeyInfoBean bean = new KeyInfoBean();
            bean.setName(String.format("前进%s", i));
            bean.setIndex(i);
            keyArrayList.add(bean);
        }

        //初始化适配器
        keysRv.post(new Runnable() {
            @Override
            public void run() {
                int height = keysRv.getMeasuredHeight();
                int width = keysRv.getMeasuredWidth();

                Log.e(myApplication.getTAG(), String.format("width, height: %s, %s", width, height));
                keySzieSet(width, height);
            }
        });
    }

    /**
     * 根据区域宽高计算按键尺寸，自适应
     * @param parent_width
     * @param parent_height
     */
    private void keySzieSet(int parent_width, int parent_height){
        final int key_w = parent_width / myApplication.keyPadCol;
        final int key_h = parent_height / myApplication.keyPadRow;

        keysAdapter = new KeysAdapter<KeyInfoBean>(R.layout.item_key, keyArrayList) {
            @Override
            public void setViewData(BaseViewHolder holder, KeyInfoBean item, int position) {
                Button btn = holder.getView(R.id.key_btn);
                btn.setText(item.getName());
                btn.setWidth(key_w);
                btn.setHeight(key_h);
            }

            @Override
            public void setEvent(BaseViewHolder holder, KeyInfoBean item, int position) {
                holder.getView(R.id.key_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(cfgMode){
                            keyEditDialog.showDialog(item);
                        }else {
                            DisplayUtils.showMsg(mContext, item.getName() + "," + item.getIndex());
                        }
                    }
                });
            }
        };

        //添加控件
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,
                myApplication.keyPadRow, LinearLayoutManager.HORIZONTAL, false);
        keysRv.setLayoutManager(gridLayoutManager);
        keysRv.setAdapter(keysAdapter);
    }

    private void initKeysSeekBar() {
        //设置底部导航条
        keysSeekBar.setPadding(0, 0, 0, 0);
        keysSeekBar.setThumbOffset(0);
        keysRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //显示区域的高度。
                int extent = keysRv.computeHorizontalScrollExtent();
                //整体的高度，注意是整体，包括在显示区域之外的。
                int range = keysRv.computeHorizontalScrollRange();
                //已经向下滚动的距离，为0时表示已处于顶部。
                int offset = keysRv.computeHorizontalScrollOffset();
                //Log.i("dx------", range + "****" + extent + "****" + offset);
                //此处获取seekbar的getThumb，就是可以滑动的小的滚动游标
                GradientDrawable gradientDrawable = (GradientDrawable) keysSeekBar.getThumb();
                //根据列表的个数，动态设置游标的大小，设置游标的时候，progress进度的颜色设置为和seekbar的颜色设置的一样的，
                // 所以就不显示进度了。
                int size;
                try {
                    size = extent / (keysAdapter.getData().size() / 2);
                }catch (Exception e){
                    size = 0;
                }
                gradientDrawable.setSize(size, 5);
                //设置可滚动区域
                keysSeekBar.setMax((int) (range - extent));
                if (dx == 0) {
                    keysSeekBar.setProgress(0);
                } else if (dx > 0) {
                    //Log.i("dx------", "右滑");
                    keysSeekBar.setProgress(offset);
                } else if (dx < 0) {
                    //Log.i("dx------", "左滑");
                    keysSeekBar.setProgress(offset);
                }

            }

        });
    }


}
