package com.starrtc.staravdemo.demo.voip;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.BaseActivity;
import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.staravdemo.demo.database.CoreDB;
import com.starrtc.staravdemo.demo.database.HistoryBean;
import com.starrtc.staravdemo.demo.ui.CircularCoverView;
import com.starrtc.staravdemo.utils.ColorUtils;
import com.starrtc.staravdemo.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

public class VoipListActivity extends BaseActivity {

    private EditText vEditText;
    private String mTargetId;
    private List<HistoryBean> mHistoryList;
    private ListView vHistoryList;
    private MyListAdapter myListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voip_list);

        ((TextView)findViewById(R.id.title_text)).setText("VOIP会话列表");
        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.create_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VoipListActivity.this,VoipCreateActivity.class));
            }
        });

        mHistoryList = new ArrayList<>();

        myListAdapter = new MyListAdapter();
        vHistoryList = (ListView) findViewById(R.id.list);
        vHistoryList.setAdapter(myListAdapter);
        vHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTargetId = (String) mHistoryList.get(position).getConversationId();
                MLOC.saveVoipUserId(VoipListActivity.this,mTargetId);
                Intent intent = new Intent(VoipListActivity.this,VoipActivity.class);
                intent.putExtra("targetId",mTargetId);
                intent.putExtra(VoipActivity.ACTION,VoipActivity.CALLING);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        MLOC.hasNewVoipMsg = false;
        mHistoryList.clear();

        List<HistoryBean> list = MLOC.getHistoryList(CoreDB.HISTORY_TYPE_VOIP);
        if(list!=null&&list.size()>0){
            mHistoryList.addAll(list);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myListAdapter.notifyDataSetChanged();
            }
        });
    }

    public class MyListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public MyListAdapter(){
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if(mHistoryList!=null)
                return mHistoryList.size();
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if(mHistoryList ==null)
                return null;
            return mHistoryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            if(mHistoryList ==null)
                return 0;
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder itemSelfHolder;
            if(convertView == null){
                itemSelfHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_voip_list,null);
                itemSelfHolder.vUserId = (TextView) convertView.findViewById(R.id.item_id);
                itemSelfHolder.vTime = (TextView) convertView.findViewById(R.id.item_time);
                itemSelfHolder.vCount = (TextView) convertView.findViewById(R.id.item_count);
                itemSelfHolder.vHeadBg =  convertView.findViewById(R.id.head_bg);
                itemSelfHolder.vHeadImage = (ImageView) convertView.findViewById(R.id.head_img);
                itemSelfHolder.vHeadCover = (CircularCoverView) convertView.findViewById(R.id.head_cover);
                convertView.setTag(itemSelfHolder);
            }else{
                itemSelfHolder = (ViewHolder)convertView.getTag();
            }
            String userId = mHistoryList.get(position).getConversationId();
            itemSelfHolder.vUserId.setText(userId);
            itemSelfHolder.vTime.setText(mHistoryList.get(position).getLastTime());
            itemSelfHolder.vHeadBg.setBackgroundColor(ColorUtils.getColor(VoipListActivity.this,userId));
            itemSelfHolder.vHeadCover.setCoverColor(Color.parseColor("#FFFFFF"));
            int cint = DensityUtils.dip2px(VoipListActivity.this,28);
            itemSelfHolder.vHeadCover.setRadians(cint, cint, cint, cint,0);
            itemSelfHolder.vHeadImage.setImageResource(R.drawable.starfox_50);

            if(mHistoryList.get(position).getNewMsgCount()==0){
                itemSelfHolder.vCount.setVisibility(View.INVISIBLE);
            }else{
                itemSelfHolder.vCount.setText(""+mHistoryList.get(position).getNewMsgCount());
                itemSelfHolder.vCount.setVisibility(View.VISIBLE);
            }

            return convertView;
        }
    }

    public class ViewHolder{
        public TextView vUserId;
        public TextView vTime;
        public TextView vCount;
        public View vHeadBg;
        public CircularCoverView vHeadCover;
        public ImageView vHeadImage;
    }

}
