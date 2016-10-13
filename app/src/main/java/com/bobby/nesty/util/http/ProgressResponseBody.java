package com.bobby.nesty.util.http;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by corous360 on 2016/6/6.
 */
public class ProgressResponseBody extends ResponseBody{

    private ResponseBody responseBody;
    private ProgressListener progressListener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ProgressListener progressListener, ResponseBody responseBody) {
        this.progressListener = progressListener;
        this.responseBody = responseBody;
    }

    @Override public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override public long contentLength() {
        return responseBody.contentLength();
    }

    @Override public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                if(progressListener != null && responseBody.contentLength() != -1){
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                }
                return bytesRead;
            }
        };
    }
}
