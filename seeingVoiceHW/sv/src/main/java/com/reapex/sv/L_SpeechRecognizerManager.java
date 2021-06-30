/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.reapex.sv;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.mlsdk.asr.MLAsrConstants;
import com.huawei.hms.mlsdk.asr.MLAsrListener;
import com.huawei.hms.mlsdk.asr.MLAsrRecognizer;

import java.util.ArrayList;

public class L_SpeechRecognizerManager {
    private final static String TAG = "SpeechRecognizerManager";

    protected AudioManager mAudioManager;
    protected MLAsrRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;

    private OnResultsReadyInterface mCallBack;  //2 接口变量
    ArrayList<String> mResultsList = new ArrayList<>();

    public L_SpeechRecognizerManager(Context context, String language, OnResultsReadyInterface onResultsReadyInterfaceCallBack) {
        try {
            mCallBack = onResultsReadyInterfaceCallBack;        //3 提供注册接口的方法 暴露接口给调用者；
        } catch (ClassCastException e) {
            Log.e(TAG, e.toString());
        }
//        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        //用户调用接口创建一个语音识别器。
        mSpeechRecognizer = MLAsrRecognizer.createAsrRecognizer(context);       //sv

        //将新建的结果监听器回调与语音识别器绑定
        mSpeechRecognizer.setAsrListener(new SpeechRecognitionListener());

        // 新建Intent，用于配置语音识别参数。
        mSpeechRecognizerIntent = new Intent(MLAsrConstants.ACTION_HMS_ASR_SPEECH);

        mSpeechRecognizerIntent.putExtra(MLAsrConstants.LANGUAGE, language)
                .putExtra(MLAsrConstants.FEATURE, MLAsrConstants.FEATURE_WORDFLUX);
        // Set the usage scenario to shopping.
        //.putExtra(MLAsrConstants.SCENES, MLAsrConstants.SCENES_SHOPPING);
        // 设置识别文本返回模式为边识别边出字，若不设置，默认为边识别边出字。支持设置：
        // MLAsrConstants.FEATURE_WORDFLUX：通过onRecognizingResults接口，识别同时返回文字；
        // MLAsrConstants.FEATURE_ALLINONE：识别完成后通过onResults接口返回文字。
        // .putExtra(MLAsrConstants.FEATURE, MLAsrConstants.FEATURE_WORDFLUX)
    }

    public void startListening() {
        Log.d(TAG, "startListening()");
        mSpeechRecognizer.startRecognizing(mSpeechRecognizerIntent);
        int i = 10000;
        Log.d(TAG, "startListening()73 " + i);
    }

    public void destroy() {
        Log.d(TAG, "onDestroy");
        if (mSpeechRecognizer != null) {
//sv            mSpeechRecognizer.destroy();
  //sv          mSpeechRecognizer = null;
        }

    }

    // 回调实现MLAsrListener接口，实现接口中的方法。
    protected class SpeechRecognitionListener implements MLAsrListener {

        // 录音器开始接收声音。
        @Override
        public void onStartListening() {
            Log.d(TAG, "onStartListening--");
        }

        // 用户开始讲话，即语音识别器检测到用户开始讲话。
        @Override
        public void onStartingOfSpeech() {
            Log.d(TAG, "onStartingOfSpeech--");
        }

        // 返回给用户原始的PCM音频流和音频能量，该接口并非运行在主线程中，返回结果需要在子线程中处理。
        @Override
        public void onVoiceDataReceived(byte[] data, float energy, Bundle bundle) {
            int length = data == null ? 0 : data.length;
            Log.d(TAG, "onVoiceDataReceived-- data.length=" + length);
            Log.d(TAG, "onVoiceDataReceived-- energy =" + energy);
        }

        // 从MLAsrRecognizer接收到持续语音识别的文本，该接口并非运行在主线程中，返回结果需要在子线程中处理。
        @Override
        public void onRecognizingResults(Bundle partialResults) {
            if (partialResults != null && mCallBack != null) {
                mResultsList.clear();
                mResultsList.add(partialResults.getString(MLAsrRecognizer.RESULTS_RECOGNIZING));
                mCallBack.onResults(mResultsList);      //4 接口变量调用被实现的接口方法；
                Log.d(TAG, "onRecognizingResults is_sv " + partialResults);
            }
        }

        // 收尾。语音识别的文本数据，该接口并非运行在主线程中，返回结果需要在子线程中处理。
        @Override
        public void onResults(Bundle results) {
            Log.e(TAG, "onResults");
            if (results != null && mCallBack != null) {
                int i = 1;
                mResultsList.clear();
                mResultsList.add(results.getString(MLAsrRecognizer.RESULTS_RECOGNIZED));
//                mListener.onFinsh();
                Log.d(TAG, "onResults is " + results);
            }
        }

        @Override
        public void onError(int error, String errorMessage) {
            Log.e(TAG, "onError: " + errorMessage);
            // If you don't add this, there will be no response after you cut the network
            if (mCallBack != null) {
                mCallBack.onError(error);
            }
        }

        @Override
        // 通知应用状态发生改变，该接口并非运行在主线程中，返回结果需要在子线程中处理。
        //sv listener 不关闭，但语音识别还是关闭了
        public void onState(int state, Bundle params) {
            Log.e(TAG, "onState: L_142 " + state);
            if (state == MLAsrConstants.STATE_NO_SOUND_TIMES_EXCEED) {
                // onState回调中的状态码，表示6s内没有检测到结果。Constant Value：3
                if (mCallBack != null) {
                    mCallBack.onFinsh();
                    //sv
                }
            Log.e(TAG, "onState: L_142 no sound " + state);
            }
        }
    }

    public interface OnResultsReadyInterface {
        //1 定义接口和接口中的方法
        void onResults(ArrayList<String> results);
        void onFinsh();
        void onError(int error);
    }

}
