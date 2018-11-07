package com.example.kaixuan.diyphotowall;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BigPictureActivity extends AppCompatActivity {
    private List<View> images = new LinkedList<View>();
    private String[] urls = null;
    private PhotoWallAdapter mImageLoader;

    public static final int data = 1;
    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private int position;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bigpicture);

        Intent intent = getIntent();
        Bundle b = this.getIntent().getExtras();
        urls = b.getStringArray("imageUrls");
        position = intent.getIntExtra("position",0);
        getBitmap(urls);

    }
    public void init(){
        for(int i = 0;i<urls.length;i++){
            View view = LayoutInflater.from(this).inflate(R.layout.store_imagefragment,null);
            ImageView imageView = (ImageView) view.findViewById(R.id.image_fragment);
            imageView.setImageBitmap(bitmaps.get(i));
            images.add(view);
        }
        ViewPager mViewPaper = (ViewPager) findViewById(R.id.vp);
        textView = (TextView) findViewById(R.id.text);
        ViewPagerAdapter adapter = new ViewPagerAdapter(images);
        mViewPaper.setAdapter(adapter);
        int currentItem = position;
        mViewPaper.setCurrentItem(currentItem);
        textView.setText((currentItem+1)+"/"+urls.length);
        mViewPaper.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                textView.setText((position+1)+"/"+urls.length);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            init();
        }
    };


    private class ViewPagerAdapter extends PagerAdapter {
        private List<View> list_view;

        public ViewPagerAdapter(List<View> view){
            list_view = view;
        }

        @Override
        public int getCount() {
            return urls.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list_view.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list_view.get(position));
            return list_view.get(position);
        }
    }


    private void getBitmap(final String[] urls){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap=null;
                HttpURLConnection con =null;
                for (int i = 0; i < urls.length; i++) {

                    URL url = null;
                    try {
                        url = new URL(urls[i]);
                        con =(HttpURLConnection) url.openConnection();
                        con.connect();
                        con.setConnectTimeout(5*1000);
                        con.setReadTimeout(10*1000);
                        InputStream is =con.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                        is.close();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        if(con !=null){
                            con.disconnect();
                        }
                    }

                    bitmaps.add(bitmap);
                }
                mHandler.sendEmptyMessage(data);
            }
        }).start();
    }
}
