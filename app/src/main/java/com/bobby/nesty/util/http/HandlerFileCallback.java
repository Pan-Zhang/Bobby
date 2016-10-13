package com.bobby.nesty.util.http;

import android.os.Handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by corous360 on 2016/7/18.
 */
public abstract class HandlerFileCallback implements ProgressListener {

    private File desFile;
    Handler handler = new Handler();

    public HandlerFileCallback(String path){
        desFile = new File(path);
    }

    public HandlerFileCallback(File file){
        desFile = file;
    }

    public abstract void failed(String str);

    public abstract void success(File file);

    @Override
    public void onFailure(Call call, final IOException e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                failed(e.toString());
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try
        {
            is = response.body().byteStream();
            if(!desFile.exists()){
                Boolean creat = desFile.createNewFile();
            }else{
                desFile.delete();
            }
            fos = new FileOutputStream(desFile);
            while ((len = is.read(buf)) != -1)
            {
                fos.write(buf, 0, len);
            }
            fos.flush();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    success(desFile);
                }
            });
        } catch (final IOException e)
        {
            e.printStackTrace();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    failed(e.toString());
                }
            });
        } finally
        {
            try
            {
                if (is != null) is.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                if (fos != null) fos.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
