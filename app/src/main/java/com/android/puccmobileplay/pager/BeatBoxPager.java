package com.android.puccmobileplay.pager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.puccmobileplay.R;
import com.android.puccmobileplay.base.BasePager;
import com.android.puccmobileplay.bean.SoundInfo;
import com.android.puccmobileplay.mod.ModLab;

import java.util.List;

/**
 * Created by 长春 on 2017/9/15.
 */

public class BeatBoxPager extends BasePager {
    private static final int INIT_DATA_COMPLETE = 1;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private Handler mHandler;
    public BeatBoxPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View v = View.inflate(mContext, R.layout.fragment_beat_box_pager,null);
        mRecyclerView = v.findViewById(R.id.fragment_beat_box_recycler_view);
        mProgressBar = v.findViewById(R.id.fragment_beat_box_progress_bar);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext,3));
        mRecyclerView.setAdapter(new SoundAdapter(ModLab.get(mContext).getSoundList()));
        mHandler = new BeatBoxHandle();
        return v;
    }

    /**
     * 由外部调用---在需要加载的时候初始化
     * SoundPool.load 是同步IO
     * 改成异步
     */
    @Override
    public void initDate() {
        super.initDate();
        ModLab.get(mContext).getExecutor().execute(()->{
            ModLab.get(mContext).reLoadSoundPool();
            mHandler.sendEmptyMessage(INIT_DATA_COMPLETE);
        });

    }

    @Override
    public void releaseDate() {
        ModLab.get(mContext).releaseSounds();
        mProgressBar.setVisibility(View.VISIBLE);
        isInitDate = false;
    }


    private class BeatBoxHandle extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case INIT_DATA_COMPLETE:
                    if (isInitDate) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                    break;
                default:
            }
        }
    }

    private class SoundHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Button mButton;
        private SoundInfo mSoundInfo;

        public SoundHolder(View itemView) {
            super(itemView);
            mButton = itemView.findViewById(R.id.list_item_sound_button);
            mButton.setOnClickListener(this);
        }

        public void BindView(SoundInfo soundInfo){
            mSoundInfo = soundInfo;
            mButton.setText(mSoundInfo.getName());
        }

        @Override
        public void onClick(View v) {
            if (v == mButton){
                ModLab.get(mContext).playSound(mSoundInfo);
            }
        }
    }

    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder>{

        List<SoundInfo> mSoundInfos;

        public SoundAdapter(List<SoundInfo> soundInfos) {
            mSoundInfos = soundInfos;
        }

        @Override
        public SoundHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SoundHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.list_item_beat_box_pager,parent,false));
        }

        @Override
        public void onBindViewHolder(SoundHolder holder, int position) {
            holder.BindView(mSoundInfos.get(position));
        }

        @Override
        public int getItemCount() {
            return mSoundInfos.size();
        }
    }
}
