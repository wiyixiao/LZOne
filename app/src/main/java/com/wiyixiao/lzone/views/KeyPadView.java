package com.wiyixiao.lzone.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wiyixiao.lzone.BuildConfig;
import com.wiyixiao.lzone.MyApplication;
import com.wiyixiao.lzone.R;
import com.wiyixiao.lzone.adapter.MyAdapter;
import com.wiyixiao.lzone.bean.KeyInfoBean;
import com.wiyixiao.lzone.core.LocalThreadPools;
import com.wiyixiao.lzone.core.PriorityRunnable;
import com.wiyixiao.lzone.core.PriorityType;
import com.wiyixiao.lzone.data.Constants;
import com.wiyixiao.lzone.interfaces.IKeyEditListener;
import com.wiyixiao.lzone.interfaces.IKeyPadListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    //按键
    private MyAdapter<KeyInfoBean> keysAdapter;
    private ArrayList<KeyInfoBean> keyArrayList;

    private IKeyPadListener keyPadListener;
    private KeyEditDialog keyEditDialog;

    private String ip;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case Constants.KEY_LONG_PRESS:
                    keyPadListener.long_press(msg.obj.toString());
                    break;
                default:
                    break;
            }

            return false;
        }
    });

    private IKeyEditListener iKeyEditListener = new IKeyEditListener() {
        @Override
        public void add(KeyInfoBean bean) {

            int keyCount = 0;
            int index = 0;

            //非配置模式且不包含则添加、否则更新
            if(!cfgMode && !keyArrayList.contains(bean)){
                //获取当前按键数量
                keyCount = keyArrayList.size();
                index = keyCount;

                //自增按键索引
                bean.setIndex(keyCount);
                keyArrayList.add(bean);

                //添加数据库

            }else{
                index = keyArrayList.indexOf(bean);
                //更新数据库

            }

            keyCount = keyArrayList.size();

            if(keyCount > 0){
                keyInitBtn.setVisibility(GONE);
            }

            keysAdapter.notifyItemRangeChanged(index, 1);
            keyEditDialog.dismissDialog();
        }

        @Override
        public void remove(KeyInfoBean bean) {
            //包含该按键并且为配置模式，则进行按键移除操作
            if(cfgMode && keyArrayList.contains(bean)){
                keyArrayList.remove(bean);
                keysAdapter.notifyDataSetChanged();

                //更新数据库

                keyEditDialog.dismissDialog();
            }

            if(keyArrayList.size() <= 0){
                keyInitBtn.setVisibility(VISIBLE);
            }
        }


    };

    @OnClick({R.id.key_init_btn})
    public void onClick(View v){
        int id = v.getId();
        if(id == R.id.key_init_btn){
            keysReset();
            keysAdapter.notifyDataSetChanged();
            keyInitBtn.setVisibility(GONE);
        }
    }

    public KeyPadView(Context context) {
        super(context);
        myApplication = (MyApplication)context.getApplicationContext();
        initView();
    }

    public KeyPadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        keyEditDialog = KeyEditDialog.getInstance(context, iKeyEditListener);
        myApplication = (MyApplication)context.getApplicationContext();
        initView();
    }

    public void keySetListener(Context context, IKeyPadListener listener, String ip){
        this.mContext = context;
        this.ip = ip;
        this.keyPadListener = listener;

        this.keyLongListener = new KeyLongListener(this);
        this.keyTouchListener = new KeyTouchListener(this);
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

            //根据IP，更新数据库

            //显示初始化按钮
            keyInitBtn.setVisibility(VISIBLE);
        }
    }

    private void initView(){
        View inflate = inflate(getContext(), R.layout.view_keypad, this);
        unbinder = ButterKnife.bind(this, inflate);

        initKeysView();
        initKeysSeekBar();
    }

    public void close(){
        longPress = false;
        unbinder.unbind();
        keyEditDialog.destoryView();

        Log.e(myApplication.getTAG(), "按键控件资源释放");
    }

    @Override
    protected  void finalize() throws Throwable{
        super.finalize();
    }

    /*****************************************GET/SET*****************************************/
    public boolean isCfgMode() {
        return cfgMode;
    }

    public void setCfgMode(boolean cfgMode) {
        this.cfgMode = cfgMode;
    }

    /*****************************************按键事件*****************************************/

    private boolean longPress = false;
    private boolean longPressFlag = false;
    private KeyInfoBean longPressKey = null;

    private KeyTouchListener keyTouchListener = null;
    private KeyLongListener keyLongListener = null;

    private static class KeyLongListener implements OnLongClickListener{

        private KeyInfoBean bean;
        private KeyPadView pad;

        private void work(){

            pad.longPress = true; //默认开启长按线程
            if(BuildConfig.DEBUG){
                System.out.println("**********key long press init**********");
            }

            PriorityRunnable longPressRunnable = new PriorityRunnable(PriorityType.NORMAL, new Runnable() {
                @Override
                public void run() {
                    while (pad.longPress){
                        try {

                            if(!pad.longPressFlag){
                                //无长按按键，防止线程卡死
                                Thread.sleep(2);

                            }else{
                                //检测各长按按键定时时间
                                Message msg = new Message();
                                msg.what = Constants.KEY_LONG_PRESS;
                                msg.obj = bean.getTxt_lclick();
                                pad.handler.sendMessage(msg);
                                Thread.sleep(Integer.parseInt(bean.getTime()));
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    if(BuildConfig.DEBUG){
                        System.out.println("KeyLongPressThread Exit!");
                    }
                }
            });

            LocalThreadPools.getInstance().getExecutorService().execute(longPressRunnable);
        }

        private KeyLongListener(KeyPadView pad){
            this.pad = pad;
            work();
        }

        @Override
        public boolean onLongClick(View v) {

            //配置模式 或者 长按模式已经触发 则不执行长按
            if(pad.cfgMode || pad.longPressFlag){
                if(BuildConfig.DEBUG){
                    System.out.println("**********key long pressing**********");
                }
                return false;
            }

            Button btn = (Button)v;
            btn.setTextColor(Color.RED);
            bean = (KeyInfoBean)v.getTag();
            pad.longPressFlag = true;
            pad.longPressKey = bean;

            return true;
        }

    }

    private static class KeyTouchListener implements OnTouchListener{

        private KeyInfoBean bean;
        private KeyPadView pad;

        private KeyTouchListener(KeyPadView pad){
            this.pad = pad;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if(pad.cfgMode){
                return false;
            }

            bean = (KeyInfoBean)v.getTag();

            switch (event.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
                    if(!bean.equals(pad.longPressKey)){
                        pad.keyPadListener.short_press(bean.getTxt_click());
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if(bean.equals(pad.longPressKey)){
                        //只有处于长按状态的按键释放才退出长按
                        //保证长按的时候，其他按键可以发送短按和释放命令
                        Button btn = (Button)v;
                        btn.setTextColor(Color.BLACK);
                        pad.longPressFlag = false;
                        pad.longPressKey = null;
                    }
                    pad.keyPadListener.release_press(bean.getTxt_release());

                    break;
                default:
                    break;
            }
            return false;
        }
    }

    /*****************************************初始化*****************************************/
    private void initKeysView(){
        //读取数据库初始化按键数据
        keyArrayList = new ArrayList<KeyInfoBean>();

        //根据IP查询其对应的自定义按键数量，数量为0时显示恢复默认设置按钮
        int keyCount = 0;
        if(keyCount == 0){
            keysReset();
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

        keysAdapter = new MyAdapter<KeyInfoBean>(R.layout.item_key, keyArrayList) {
            @Override
            public void setViewData(BaseViewHolder holder, KeyInfoBean item, int position) {
                Button btn = holder.getView(R.id.key_btn);
                btn.setText(item.getName());
                btn.setWidth(key_w);
                btn.setHeight(key_h);
                btn.setTag(item);    //设置索引
            }

            @Override
            public void setEvent(BaseViewHolder holder, KeyInfoBean item, int position) {

                //设置点击
                holder.getView(R.id.key_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(cfgMode){
                            keyEditDialog.showDialog(item);
                        }
                    }
                });

                //设置触摸
                holder.getView(R.id.key_btn).setOnTouchListener(keyTouchListener);

                //设置长按
                holder.getView(R.id.key_btn).setOnLongClickListener(keyLongListener);
            }
        };

        //添加控件
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,
                myApplication.keyPadRow, LinearLayoutManager.VERTICAL, false);
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
                int extent = keysRv.computeVerticalScrollExtent();
                //整体的高度，注意是整体，包括在显示区域之外的。
                int range = keysRv.computeVerticalScrollRange();
                //已经向下滚动的距离，为0时表示已处于顶部。
                int offset = keysRv.computeVerticalScrollOffset();
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
                if (dy == 0) {
                    keysSeekBar.setProgress(0);
                } else if (dy > 0) {
                    //Log.i("dx------", "右滑");
                    keysSeekBar.setProgress(offset);
                } else if (dy < 0) {
                    //Log.i("dx------", "左滑");
                    keysSeekBar.setProgress(offset);
                }

            }

        });
    }

    private void keysReset(){
        for(int i=0;i<myApplication.keyDdefaultCount;i++){
            KeyInfoBean bean = new KeyInfoBean();
            bean.setName(String.format("Run%s", i));
            bean.setIndex(i);
            keyArrayList.add(bean);
        }

        //更新数据库
        
    }
















}
