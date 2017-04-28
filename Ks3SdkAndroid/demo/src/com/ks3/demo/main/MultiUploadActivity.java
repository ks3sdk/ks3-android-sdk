package com.ks3.demo.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ksyun.ks3.services.Ks3Client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MultiUploadActivity extends Activity {

    private List<MultiUploader> fileInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_upload);
        getFileInfo();
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ListViewAdapter(fileInfos));
    }

    private void getFileInfo(){
        fileInfos = new ArrayList<MultiUploader>();
        Ks3Client client = Ks3ClientFactory.getDefaultClient(MultiUploadActivity.this);
        fileInfos.add(new MultiUploader(client, Constants.TEST_MULTIUPLOAD_BUCKET, "11.txt",
                new File(Constants.TEST_MULTIUPLOAD_FILE)));//, "bb20153e438047b3bc3212f37e25fd9c"));
        fileInfos.add(new MultiUploader(client, Constants.TEST_MULTIUPLOAD_BUCKET, "11.txt",
                new File(Constants.TEST_MULTIUPLOAD_FILE), "8b67c9679fae436ba754b4bafc29ca37"));
    }

    public class ListViewAdapter extends BaseAdapter {
        View[] itemViews;

        public ListViewAdapter(List<MultiUploader> list) {
            itemViews = new View[list.size()];
            for(int i=0;i<list.size();i++){
                MultiUploader uploader = list.get(i);
                itemViews[i]=makeItemView(uploader.getKey(), uploader);
            }
        }

        private View makeItemView(String key, final MultiUploader uploader) {
            LayoutInflater inflater = (LayoutInflater) MultiUploadActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 使用View的对象itemView与R.layout.item关联
            View itemView = inflater.inflate(R.layout.multiupload_item, null);
            TextView fileNameView = (TextView) itemView.findViewById(R.id.file_name);
            fileNameView.setText(key);
            Button btn = (Button) itemView.findViewById(R.id.uploadBtn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(uploader.getUploadId()==null)
                        uploader.upload();
                    else
                        uploader.reUpload();
                }
            });
            return itemView;
        }

        @Override
        public int getCount() {
            return itemViews.length;
        }

        @Override
        public Object getItem(int i) {
            return itemViews[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null)
                return itemViews[i];
            return view;
        }
    }
}
