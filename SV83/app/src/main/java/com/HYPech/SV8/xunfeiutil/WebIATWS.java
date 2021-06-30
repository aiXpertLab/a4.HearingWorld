package com.HYPech.SV8.xunfeiutil;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.HYPech.SV8.App;
import com.HYPech.SV8.event.NoNetWorkEvent;
import com.HYPech.SV8.util.CommonUtil;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import androidx.appcompat.app.AppCompatActivity;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import tech.oom.idealrecorder.IdealRecorder;
import tech.oom.idealrecorder.StatusListener;
import tech.oom.idealrecorder.utils.BytesTransUtil;

//import java.util.Base64;

/**
 * 语音听写流式 WebAPI 接口调用示例 接口文档（必看）：https://doc.xfyun.cn/rest_api/语音听写（流式版）.html
 * webapi 听写服务参考帖子（必看）：http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=38947&extra=
 * 语音听写流式WebAPI 服务，热词使用方式：登陆开放平台https://www.xfyun.cn/后，找到控制台--我的应用---语音听写---个性化热词，上传热词
 * 注意：热词只能在识别的时候会增加热词的识别权重，需要注意的是增加相应词条的识别率，但并不是绝对的，具体效果以您测试为准。
 * 错误码链接：https://www.xfyun.cn/document/error-code （code返回错误码时必看）
 * 语音听写流式WebAPI 服务，方言或小语种试用方法：登陆开放平台https://www.xfyun.cn/后，在控制台--语音听写（流式）--方言/语种处添加
 * 添加后会显示该方言/语种的参数值
 * @author iflytek
 */

public class WebIATWS extends WebSocketListener {
    private static final String hostUrl = "https://iat-api.xfyun.cn/v2/iat"; //中英文，http url 不支持解析 ws/wss schema
    // private static final String hostUrl = "https://iat-niche-api.xfyun.cn/v2/iat";//小语种
    //TODO 个人账户认证
//    private static final String appid = "5bcd74d0"; //在控制台-我的应用获取
//    private static final String apiKey = "7f57134dd38330d3218f2a32893b5a32"; //在控制台-我的应用-语音听写（流式版）获取
//    private static final String apiSecret = "ea8ae90d132f0e868d4e196e4b1bfa61"; //在控制台-我的应用-语音听写（流式版）获取
//

    /** 860563368@qq.com Shanxi Jiansheng
    private static final String appid = "5d5f4538"; //在控制台-我的应用获取
    private static final String apiKey = "0e17f700061819d24c9d106c61f24a38"; //在控制台-我的应用-语音听写（流式版）获取
    private static final String apiSecret = "51997f653c94e2db6ab86460301660f3"; //在控制台-我的应用-语音听写（流式版）获取
    */

    //MagicLeap
    private static final String appid = "5d8d3ccc"; //在控制台-我的应用获取
    private static final String apiKey = "454bdbe7e638d0676c1a54ef16256002"; //在控制台-我的应用-语音听写（流式版）获取
    private static final String apiSecret = "099dd5d5cf5b4bd0715b47dac97bf26a"; //在控制台-我的应用-语音听写（流式版）获取


//    private static final String file = "resource\\iat\\16k_10.pcm"; // 中文
    private static final String file = "/sdcard/16k_10.pcm"; // 中文
    public static final int StatusFirstFrame = 0;
    public static final int StatusContinueFrame = 1;
    public static final int StatusLastFrame = 2;
//    public boolean canRun = true;
    public static final Gson json = new Gson();
    Decoder decoder = new Decoder();
    // 开始时间
    private static Date dateBegin = new Date();
    // 结束时间
    private static Date dateEnd = new Date();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss.SSS");
    private Activity mContext;

    WebSocket mWebSocket;
    OkHttpClient client;
    Request request;

    private AppCompatActivity _mActivity;

    public WebIATWS(AppCompatActivity mActivity){
        _mActivity = mActivity;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        if(App.isDebug)Log.d("ljp","onOPen");
            mWebSocket = webSocket;
            //开启websocket 与讯飞通讯后开启录音
            startRecord();

    }
    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        if(App.isDebug)Log.d("ljp","onClosed"+reason+ App.myApplication.canRun);
        restartWebIA(_mActivity);
    }
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        //System.out.println(text);
        ResponseData resp = json.fromJson(text, ResponseData.class);
        if (resp != null) {
            if (resp.getCode() != 0) {
                System.out.println( "code=>" + resp.getCode() + " error=>" + resp.getMessage() + " sid=" + resp.getSid());
                System.out.println( "错误码查询链接：https://www.xfyun.cn/document/error-code");
                return;
            }
            if (resp.getData() != null) {
                if (resp.getData().getResult() != null) {
                    Text te = resp.getData().getResult().getText();
                    //System.out.println(te.toString());
                    try {
                        decoder.decode(te);
                        System.out.println("中间识别结果 ==》" + decoder.toString());
//                        resultListener.onResult(decoder.toString(),false);
                        if(!CommonUtil.isEmpty(decoder.toString())) {
                            EventBusActivityScope.getDefault(_mActivity).post(new xunfeiMessageEvent(decoder.toString(), false, resp.getSid()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (resp.getData().getStatus() == 2) {
                    // todo  resp.data.status ==2 说明数据全部返回完毕，可以关闭连接，释放资源
                    System.out.println("session end ");
                    dateEnd = new Date();
                    System.out.println(sdf.format(dateBegin) + "开始"+sdf.format(dateEnd) + "结束"+"最终识别结果 ==》" + decoder.toString());
                    System.out.println("耗时:" + (dateEnd.getTime() - dateBegin.getTime()) + "ms");
//                    System.out.println("最终识别结果 ==》" + decoder.toString());
//                    System.out.println("本次识别sid ==》" + resp.getSid());
                    if(!CommonUtil.isEmpty(decoder.toString())) {
                        EventBusActivityScope.getDefault(_mActivity).post(new xunfeiMessageEvent(decoder.toString(), true, resp.getSid()));
                    }
                    decoder.discard();
                    webSocket.close(1000, "");
                } else {
                    // todo 根据返回的数据处理
                }
            }
        }
    }
    private void restartWebIA(AppCompatActivity mActivity){
        if(App.isDebug)Log.d("ljp","restartWebIA canrun"+ App.myApplication.canRun);
        if(App.myApplication.canRun){
            if(App.isDebug)Log.d("ljp","onFailed webIATWS restart");
            // 构建鉴权url
            String authUrl = null;
            try {
                authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
            } catch (Exception e) {
                e.printStackTrace();
            }

            client = new OkHttpClient.Builder().build();
            //将url中的 schema http://和https://分别替换为ws:// 和 wss://
            String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
            //System.out.println(url);
            request = new Request.Builder().url(url).build();
            // System.out.println(client.newCall(request).execute());
            //System.out.println("url===>" + url);
            client.newWebSocket(request, new WebIATWS(mActivity));
        }
    }
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        if(App.isDebug)Log.d("ljp","onFailed webIATWS");
        try {
            restartWebIA(_mActivity);
            if (null != response) {
                int code = response.code();
                if(App.isDebug)System.out.println("onFailure code:" + code);
                if(App.isDebug)System.out.println("onFailure body:" + response.body().string());
                if (101 != code) {
                    if(App.isDebug)System.out.println("connection failed");
                    System.exit(0);
                }
            }else{

                if(t!= null && t.getMessage() != null && t.getMessage().contains("No address associated with hostname")){
                    if(App.isDebug)Log.d("nonetwork",t.getMessage());
                    EventBusActivityScope.getDefault(_mActivity).post(new NoNetWorkEvent("请检查网络连接！"));
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public  void StartAsr(AppCompatActivity context ) throws Exception {
        startRecord();
        mContext = context;
        App.myApplication.canRun = true;
        // 构建鉴权url
        String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);

         client = new OkHttpClient.Builder().build();
        //将url中的 schema http://和https://分别替换为ws:// 和 wss://
        String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
        //System.out.println(url);
        request = new Request.Builder().url(url).build();
        // System.out.println(client.newCall(request).execute());
        //System.out.println("url===>" + url);
        client.newWebSocket(request, new WebIATWS(context));
    }
    public void StopAsr(){

        stopRecord();
        App.myApplication.canRun = false;

    }
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        StringBuilder builder = new StringBuilder("host: ").append(url.getHost()).append("\n").//
                append("date: ").append(date).append("\n").//
                append("GET ").append(url.getPath()).append(" HTTP/1.1");
        //System.out.println(builder);
        Charset charset = Charset.forName("UTF-8");
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(charset), "hmacsha256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(builder.toString().getBytes(charset));
        String sha = android.util.Base64.encodeToString(hexDigits,android.util.Base64.DEFAULT).trim().replaceAll("\r|\n", "");

        //System.out.println(sha);
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        //System.out.println(authorization);
        HttpUrl httpUrl = HttpUrl.parse("https://" + url.getHost() + url.getPath()).newBuilder().//
                addQueryParameter("authorization", android.util.Base64.encodeToString(authorization.getBytes(charset),android.util.Base64.DEFAULT).trim().replaceAll("\r|\n", "")).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();
        return httpUrl.toString();
    }
    public static class ResponseData {
        private int code;
        private String message;
        private String sid;
        private Data data;
        public int getCode() {
            return code;
        }
        public String getMessage() {
            return this.message;
        }
        public String getSid() {
            return sid;
        }
        public Data getData() {
            return data;
        }
    }
    public static class Data {
        private int status;
        private Result result;
        public int getStatus() {
            return status;
        }
        public Result getResult() {
            return result;
        }
    }
    public static class Result {
        int bg;
        int ed;
        String pgs;
        int[] rg;
        int sn;
        Ws[] ws;
        boolean ls;
        JsonObject vad;
        public Text getText() {
            Text text = new Text();
            StringBuilder sb = new StringBuilder();
            for (Ws ws : this.ws) {
                sb.append(ws.cw[0].w);
            }
            text.sn = this.sn;
            text.text = sb.toString();
            text.sn = this.sn;
            text.rg = this.rg;
            text.pgs = this.pgs;
            text.bg = this.bg;
            text.ed = this.ed;
            text.ls = this.ls;
            text.vad = this.vad==null ? null : this.vad;
            return text;
        }
    }
    public static class Ws {
        Cw[] cw;
        int bg;
        int ed;
    }
    public static class Cw {
        int sc;
        String w;
    }
    public static class Text {
        int sn;
        int bg;
        int ed;
        String text;
        String pgs;
        int[] rg;
        boolean deleted;
        boolean ls;
        JsonObject vad;
        @Override
        public String toString() {
            return "Text{" +
                    "bg=" + bg +
                    ", ed=" + ed +
                    ", ls=" + ls +
                    ", sn=" + sn +
                    ", text='" + text + '\'' +
                    ", pgs=" + pgs +
                    ", rg=" + Arrays.toString(rg) +
                    ", deleted=" + deleted +
                    ", vad=" + (vad==null ? "null" : vad.getAsJsonArray("ws").toString()) +
                    '}';
        }
    }
    //解析返回数据，仅供参考
    public static class Decoder {
        private Text[] texts;
        private int defc = 10;
        public Decoder() {
            this.texts = new Text[this.defc];
        }
        public synchronized void decode(Text text) {
            if (text.sn >= this.defc) {
                this.resize();
            }
            if ("rpl".equals(text.pgs)) {
                for (int i = text.rg[0]; i <= text.rg[1]; i++) {
                    this.texts[i].deleted = true;
                }
            }
            this.texts[text.sn] = text;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Text t : this.texts) {
                if (t != null && !t.deleted) {
                    sb.append(t.text);
                }
            }
            return sb.toString();
        }
        public void resize() {
            int oc = this.defc;
            this.defc <<= 1;
            Text[] old = this.texts;
            this.texts = new Text[this.defc];
            for (int i = 0; i < oc; i++) {
                this.texts[i] = old[i];
            }
        }
        public void discard(){
            for(int i=0;i<this.texts.length;i++){
                this.texts[i]= null;
            }
        }
    }
    private IdealRecorder idealRecorder;

    private IdealRecorder.RecordConfig recordConfig;
    int status = 0;  // 音频的状态
    private StatusListener statusListener = new StatusListener() {
        @Override
        public void onStartRecording() {
//            Toast.makeText(mContext, "recorder started", Toast.LENGTH_SHORT).show();
            if(App.isDebug)Log.d("ljp", "recorder started" );
        }

        @Override
        public void onRecordData(short[] data2, int length) {
            if (length == 0 || data2.length != length) {
//                if(App.isDebug)Log.d("ljp", "---bytes length---" + data2.length);
                return;
            }
            final byte[] buffer = BytesTransUtil.getInstance().Shorts2Bytes(data2);
            if(buffer != null) {
//                if(App.isDebug)Log.d("ljp", "---bytes length---" + buffer.length);
//                System.out.print("开始录制声音数据集");
            }
            if(mWebSocket != null) {
                switch (status) {
                    case StatusFirstFrame:   // 第一帧音频status = 0
                        JsonObject frame = new JsonObject();
                        JsonObject business = new JsonObject();  //第一帧必须发送
                        JsonObject common = new JsonObject();  //第一帧必须发送
                        JsonObject data = new JsonObject();  //每一帧都要发送
                        // 填充common
                        common.addProperty("app_id", appid);
                        //填充business
                        business.addProperty("language", "zh_cn");

                        //检测用户静默时间
                        business.addProperty("vad_eos",1000);
                        //business.addProperty("language", "en_us");//英文
                        //business.addProperty("language", "ja_jp");//日语，在控制台可添加试用或购买
                        //business.addProperty("language", "ko_kr");//韩语，在控制台可添加试用或购买
                        //business.addProperty("language", "ru-ru");//俄语，在控制台可添加试用或购买
                        business.addProperty("domain", "iat");
                        business.addProperty("accent", "mandarin");//中文方言请在控制台添加试用，添加后即展示相应参数值
                        //business.addProperty("nunum", 0);
                        //business.addProperty("ptt", 0);//标点符号
                        //business.addProperty("rlang", "zh-hk"); // zh-cn :简体中文（默认值）zh-hk :繁体香港(若未授权不生效，在控制台可免费开通)
                        //business.addProperty("vinfo", 1);
                        business.addProperty("dwa", "wpgs");//动态修正(若未授权不生效，在控制台可免费开通)
                        //business.addProperty("nbest", 5);// 句子多候选(若未授权不生效，在控制台可免费开通)
                        //business.addProperty("wbest", 3);// 词级多候选(若未授权不生效，在控制台可免费开通)
                        //填充data
                        data.addProperty("status", StatusFirstFrame);
                        data.addProperty("format", "audio/L16;rate=16000");
                        data.addProperty("encoding", "raw");
                        data.addProperty("audio", android.util.Base64.encodeToString(Arrays.copyOf(buffer, buffer.length),android.util.Base64.DEFAULT).trim().replaceAll("\r|\n", ""));
                        //填充frame
                        frame.add("common", common);
                        frame.add("business", business);
                        frame.add("data", data);
                        mWebSocket.send(frame.toString());
                        status = StatusContinueFrame;  // 发送完第一帧改变status 为 1
                        break;
                    case StatusContinueFrame:  //中间帧status = 1
                        JsonObject frame1 = new JsonObject();
                        JsonObject data1 = new JsonObject();
                        data1.addProperty("status", StatusContinueFrame);
                        data1.addProperty("format", "audio/L16;rate=16000");
                        data1.addProperty("encoding", "raw");
                        data1.addProperty("audio", android.util.Base64.encodeToString(Arrays.copyOf(buffer, buffer.length),android.util.Base64.DEFAULT).trim().replaceAll("\r|\n", ""));
                        frame1.add("data", data1);
                        mWebSocket.send(frame1.toString());
                        // System.out.println("send continue");
                        break;
                }
            }
        }

        @Override
        public void onVoiceVolume(int volume) {

        }

        @Override
        public void onRecordError(int code, String errorMsg) {
            if(App.isDebug)Log.d("ljp","recorder error");
        }

        @Override
        public void onFileSaveFailed(String error) {
        }

        @Override
        public void onFileSaveSuccess(String fileUri) {
        }

        @Override
        public void onStopRecording() {
//            Toast.makeText(mContext, "recorder stopped", Toast.LENGTH_SHORT).show();
            if(App.isDebug)Log.d("ljp","recorder stoped");
        }

    };
    public void prepareRecord(){
        idealRecorder = IdealRecorder.getInstance();
        recordConfig = new IdealRecorder.RecordConfig(MediaRecorder.AudioSource.MIC, 16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        //如果需要保存录音文件  设置好保存路径就会自动保存  也可以通过onRecordData 回调自己保存  不设置 不会保存录音
        //设置录音配置 最长录音时长 以及音量回调的时间间隔
        idealRecorder.setRecordConfig(recordConfig).setMaxRecordTime(60000).setVolumeInterval(100);
        //设置录音时各种状态的监听
        idealRecorder.setStatusListener(statusListener);
    }
    public void startRecord() {
        prepareRecord();
        if(App.isDebug)Log.d("ljp","record start");
        idealRecorder.start(); //开始录音

    }
    /**
     * stop record
     */
    private void stopRecord() {
        //停止录音
         IdealRecorder.getInstance().stop();
    }
}