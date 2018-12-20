package com.android.csndmobileplay.pager;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.csndmobileplay.R;
import com.android.csndmobileplay.base.BasePager;
import com.android.csndmobileplay.bean.SoundInfo;
import com.android.csndmobileplay.mod.ModLab;

import java.util.List;

/**
 * Created by 长春 on 2017/9/15.
 */

public class BeatBoxPager extends BasePager {
    private RecyclerView mRecyclerView;

    public BeatBoxPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View v = View.inflate(mContext, R.layout.fragment_beat_box_pager,null);
        mRecyclerView = v.findViewById(R.id.fragment_beat_box_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext,3));
        mRecyclerView.setAdapter(new SoundAdapter(ModLab.get(mContext).getSoundList()));
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
        ModLab.get(mContext).reLoadSoundPool();
    }

    @Override
    public void releaseDate() {
        ModLab.get(mContext).releaseSounds();
        isInitDate = false;
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
