package com.gu.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.gu.test.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {
    private static final int GO_FOR_BAIDU_MAP = 1;
    @InjectView(R.id.lng_tv)
    TextView lngTv;
    @InjectView(R.id.lat_tv)
    TextView latTv;
    @InjectView(R.id.address_name_tv)
    TextView addressNameTv;
    @InjectView(R.id.address_detail_tv)
    TextView addressDetailTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        // 百度地图初始化
        SDKInitializer.initialize(getApplicationContext());

        findViewById(R.id.go_in_location_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                startActivityForResult(intent, GO_FOR_BAIDU_MAP);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == GO_FOR_BAIDU_MAP
                && data != null && data.getExtras() != null) {
            lngTv.setText("经度："+data.getStringExtra("Ing"));
            latTv.setText("维度："+data.getStringExtra("Iat"));
            addressNameTv.setText("地址名："+data.getStringExtra("Address"));
            addressDetailTv.setText("详细地址："+data.getStringExtra("DetailedAddress"));
        }
    }
}
