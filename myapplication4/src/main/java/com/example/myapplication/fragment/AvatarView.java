package com.example.myapplication.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.User;
import com.example.myapplication.api.Server;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/12/12.
 */

public class AvatarView extends View {
    public AvatarView(Context context) {
        super(context);
    }

    public AvatarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AvatarView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }

    Paint paint;
    float radius;
    Handler mainThreadHandler = new Handler();
    ;

    public void setBitmap(Bitmap bmp) {
        paint = new Paint();
        paint.setShader(new BitmapShader(bmp, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        radius = Math.min(bmp.getWidth(), bmp.getHeight()) / 2;
        invalidate();
    }

    public void load(User user) {
        OkHttpClient client = Server.getSharedClient();

        Request request = new Request.Builder()
                .url(Server.serverAddress + user.getAvatar())
                .method("get", null)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                try {
                    byte[] bytes = arg1.body().bytes();
                    final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    mainThreadHandler.post(new Runnable() {
                        public void run() {
                            if (bmp != null && !bmp.isRecycled()) {
                                setBitmap(bmp);
                            } else {
                                //没有头像的情况，绘制默认头像，从资源中获取Bitmap
                                Resources res = getResources();
                                Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.logo);
                                setBitmap(bmp);
                            }

                        }
                    });
                } catch (Exception ex) {
                    mainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (paint != null) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, paint);
        }

    }

}
