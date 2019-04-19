package com.android.puccmobileplay.pager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.puccmobileplay.R;
import com.android.puccmobileplay.Util.CacheUtils;
import com.android.puccmobileplay.Util.Constant;
import com.android.puccmobileplay.activity.VideoPlayer;
import com.android.puccmobileplay.base.BasePager;
import com.android.puccmobileplay.bean.NetVideoInfo;
import com.android.puccmobileplay.mod.ModLab;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;


import static android.view.View.GONE;

/**
 * Created by 长春 on 2017/9/15.
 */

public class NetVideoPager extends BasePager {
    private static final String NET_VIDEO_JSON = "net_video_json";
    RecyclerView mNetVideoRecycler;
    private static final String TAG = "NetVideoPager";
    private ProgressBar mBufferingProgress;
    private NetVideoAdapter netVideoAdapter;
    private ImageOptions imageOptions;
    private SwipeRefreshLayout mSwipeRefresh;
    /**
     * 是否刷新UI
     */
    private boolean isUpdate;

    public NetVideoPager(Context context) {
        super(context);
    }

    /**
     * 返回fragment所需的View
     *
     * @return
     */
    @Override
    public View initView() {
        View v = View.inflate(mContext, R.layout.fragment_net_video_pager, null);
        mNetVideoRecycler = (RecyclerView) v.findViewById(R.id.fragment_net_video_pager_recycler);
        mBufferingProgress =(ProgressBar) v.findViewById(R.id.fragment_net_video_pager_progress_bar);
        mSwipeRefresh = (SwipeRefreshLayout)v.findViewById(R.id.fragment_net_video_swipe_refresh);
        mNetVideoRecycler.setLayoutManager(new GridLayoutManager(mContext,1));
        return v;
    }

    /**
     * 在MainActivity调用
     * 只会初始化一次
     */
    @Override
    public void initDate() {
        super.initDate();
        String netJson = CacheUtils.getString(mContext,NET_VIDEO_JSON);
        if (netJson.equals("")){
            getJson();
        } else {
            mBufferingProgress.setVisibility(GONE);
            parseJson(netJson);
        }

        initListener();
    }

    private void initListener() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getJson();
            }
        });
    }


    private void getJson() {
        RequestParams params = new RequestParams(Constant.NET_VIDEO_JSON_URI);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                //请求成功会更新RecyclerView
                CacheUtils.putString(mContext, NET_VIDEO_JSON,result);
                parseJson(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //隐藏加载框，提示错误
                if (mSwipeRefresh.isRefreshing()){
                    mSwipeRefresh.setRefreshing(false);
                }
                mBufferingProgress.setVisibility(GONE);
                showErrorDialog(ex.getMessage());

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

/*        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(Constant.NET_VIDEO_JSON_URI)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //隐藏加载框，提示错误
                if (mSwipeRefresh.isRefreshing()){
                    mSwipeRefresh.setRefreshing(false);
                }
                mBufferingProgress.setVisibility(GONE);
                showErrorDialog(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //请求成功会更新RecyclerView
                CacheUtils.putString(mContext, NET_VIDEO_JSON,response.body().string());
                parseJson(response.body().string());
            }
        });*/
    }

    private void updateUI() {
        /**
         * 初始化xUtil ImageOptions
         */
        if (!isUpdate) {
            imageOptions = new ImageOptions.Builder()
                    .setFadeIn(true)
                    .build();

            mBufferingProgress.setVisibility(GONE);
            netVideoAdapter = new NetVideoAdapter(ModLab.get(mContext).getNetVideoList());
            mNetVideoRecycler.setAdapter(netVideoAdapter);
            isUpdate = true;
        }else {
            /**
             * 更新数据
             */
            netVideoAdapter.notifyDataSetChanged();
            mSwipeRefresh.setRefreshing(false);

        }
    }

    private void parseJson(String json) {
        try {
            Log.e(TAG, "parseJson: "+json );
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    NetVideoInfo netVideoInfo = new NetVideoInfo();
                    netVideoInfo.setCoverImgUrl(object.optString("coverImg"));
                    netVideoInfo.setHeightUrl(object.optString("hightUrl"));
                    netVideoInfo.setSummary(object.optString("summary"));
                    netVideoInfo.setVideoName(object.optString("movieName"));
                    netVideoInfo.setVideoLength(object.optString("videoLength"));
                    ModLab.get(mContext).addNetVideoInfo(netVideoInfo);
                }
            }
            updateUI();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showErrorDialog(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("错误提示")
                .setMessage(error)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    /**
     * ViewHolder
     * 保存每个项的View
     */
    private class NetVideoHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewSummuary;
        private TextView mTextViewName;
        private TextView mTextViewVideoLength;
        private ImageView mImageViewCoverImg;
        private ImageView mImageViewPlay;

        private NetVideoInfo mNetVideoInfo;

        public NetVideoHolder(View itemView) {
            super(itemView);
            mTextViewVideoLength = (TextView) itemView.findViewById(R.id.list_net_video_duration);
            mTextViewSummuary = (TextView) itemView.findViewById(R.id.list_net_video_summary);
            mTextViewName = (TextView) itemView.findViewById(R.id.list_net_video_name);
            mImageViewCoverImg = (ImageView) itemView.findViewById(R.id.list_net_video_pic);
            mImageViewPlay = (ImageView) itemView.findViewById(R.id.list_net_play_image);
            initListener();

        }

        private void initListener() {
            mImageViewCoverImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,VideoPlayer.class);
                    intent.setDataAndType(Uri.parse(mNetVideoInfo.getHeightUrl()),"Video/*");
                    mContext.startActivity(intent);
                }
            });
        }

        /**
         * 数据与试图绑定
         *
         * @param position List的位置
         */
        public void bindView(int position) {
            mNetVideoInfo = ModLab.get(mContext).getNetVideoInfo(position);
            x.image().bind(mImageViewCoverImg, mNetVideoInfo.getCoverImgUrl(),imageOptions);
            mTextViewName.setText(mNetVideoInfo.getVideoName());
            mTextViewSummuary.setText(mNetVideoInfo.getSummary());
            mTextViewVideoLength.setText(mNetVideoInfo.getVideoLength()+"s");
        }

    }

    /**
     * Adapter
     * 持有Holder，提供给Recycler
     */
    private class NetVideoAdapter extends RecyclerView.Adapter<NetVideoHolder> {
        private List<NetVideoInfo> mList;

        public NetVideoAdapter(List<NetVideoInfo> mList) {
            this.mList = mList;
        }

        @Override
        public NetVideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.list_item_net_video_pager, parent, false);
            return new NetVideoHolder(v);
        }

        @Override
        public void onBindViewHolder(NetVideoHolder holder, int position) {
            holder.bindView(position);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }
}
