package com.android.puccmobileplay.pager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.puccmobileplay.R;
import com.android.puccmobileplay.Util.HttpUtil;
import com.android.puccmobileplay.Util.Utility;
import com.android.puccmobileplay.activity.WeatherActivity;
import com.android.puccmobileplay.activity.WeatherMainActivity;
import com.android.puccmobileplay.model.db.City;
import com.android.puccmobileplay.model.db.County;
import com.android.puccmobileplay.model.db.Province;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 长春 on 2017/7/5.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private TextView textViewTitle;
    private Button buttonBack;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        //加载碎片布局到外层View
        View view = inflater.inflate(R.layout.choose_fragment,container,false);
        textViewTitle = (TextView)view.findViewById(R.id.title_text_chooseFragment);
        buttonBack = (Button)view.findViewById(R.id.back_button_chooseFragment);
        listView = (ListView)view.findViewById(R.id.list_view_fragment);
        //做好ListView的准备
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    //在对应Activity完成时，绑定监听事件
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(i);
                    queryCity();
                }else if (currentLevel==LEVEL_CITY) {
                    selectedCity=cityList.get(i);
                    queryCounty();
                }else if (currentLevel == LEVEL_COUNTY){
                    //传送天气数据，并跳转到天气界面
                    String weatherId = countyList.get(i).getWeatherId();
                    if (getActivity() instanceof WeatherMainActivity){
                        Intent intent = new Intent(getActivity(),WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity)getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
             ;
                }
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCity();
                }else if (currentLevel == LEVEL_CITY){
                    queryProvince();
                }
            }
        });

        queryProvince();

    }

    /**
     * 把省级的数据赋值给dataList，用于显示
     */
    private void queryProvince(){
        //设置title
        textViewTitle.setText("选择省份");
        buttonBack.setVisibility(View.GONE);
        //查询本地数据库
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else{
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }

    }
    private void queryCity(){
        textViewTitle.setText(selectedProvince.getProvinceName());
        buttonBack.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getId()))
                .find(City.class);
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                currentLevel=LEVEL_CITY;
            }
        }else {
            String address = "http://guolin.tech/api/china/"+selectedProvince.getProvinceCode();
            queryFromServer(address,"city");
        }
    }
    private void queryCounty(){
        textViewTitle.setText(selectedCity.getCityName());
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId()))
                .find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for (County county:countyList){
                dataList.add(county.getCountyName());
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                currentLevel=LEVEL_COUNTY;
            }
        }else {
            String address = "http://guolin.tech/api/china/"+selectedProvince.getProvinceCode()
                    +"/"+selectedCity.getCityCode();
            queryFromServer(address,"county");
        }
    }

    /**
     * 访问服务器，保存数据到本地
     */
    private void queryFromServer(String address,final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"访问失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if("province".equals(type)){
                    result=Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result=Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if("county".equals(type)){
                    result=Utility.handelCountyResponse(responseText,selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvince();
                            }else if("city".equals(type)){
                                queryCity();
                            }else if("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }

            }
        });
    }

    /**
     *
     */
    private void closeProgressDialog() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /**
     * 访问服务器时，显示进度条
     */
    private void showProgressDialog() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }
}
