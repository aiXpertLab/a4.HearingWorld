package seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest.sinwavesound;

import java.math.BigDecimal;

/**
 * Date:2019/5/27
 * Time:11:46
 * auther:zyy
 */
public class UpdateSinWave {

    /** 正弦波的高度 **/
    public static double HEIGHT = 65535;
//    public static double HEIGHT = 32768;
    /** 2PI **/
    public static final double TWOPI = 2 * 3.1415;

    private static final String TAG = "UpdateSinWave";

    /**@auth 张阳阳
     * @date 2019/2/28
     * @param waveLen 波长
     * @param length 采样率
     * @return frame float[]类型
     */

    public static short[] getSinWave(float waveLen, int length) {

        short[] wave = new short[length];
        BigDecimal tt;
        float kk;
        for (int i = 0; i < length; i++) {
//            tt = new BigDecimal(i) ;
//            kk = tt.floatValue();
            wave[i] = (short) (HEIGHT * (1 - Math.sin(TWOPI
                    * (i % waveLen) * 1.00 / waveLen)));
//            Log.e("数组打印", "getSinWave:wave (i % waveLen) "+wave[i]);
//                        Log.e("数组打印", "getSinWave:wave (i % waveLen) "+(kk % waveLen));
        }
        return wave;
    }

    public static float[] getFloatSinWave(float waveLen, int length) {
        float[] wave = new float[length];
        for (int i = 0; i < length; i++) {
            wave[i] = (float) (HEIGHT * (1 - Math.sin(TWOPI
                                * ((i % waveLen) * 1.00 / waveLen))));
//            Log.e("数组打印", "getSinWave:wave (i % waveLen) "+wave[i]);
        }
        return wave;
    }

    /**
     * 更新声音的分贝
     * 
     * @param hz
     * @param dB
     */
    public static void updateDB(int hz, int dB) {
        double temp = 0;
        switch (hz) {
            case 1000:// 1000频率
                temp = 7.5;
                break;
            case 2000:// 2000频率
                temp = 9;
                break;
            case 3000:// 2000频率
                temp = 11.5;
                break;
            case 4000:// 4000频率
                temp = 12;
                break;
            case 6000:// 6000频率
                temp = 16;
                break;
            case 8000:// 8000频率
                temp = 15.5;
                break;
            case 125:// 125频率
                temp = 45;
                break;
            case 500:// 500频率
                temp = 13.5;
                break;
            case 250:// 250频率
                temp = 27;
                break;
        }
        HEIGHT = Math.pow(10, (dB + temp) / 20);
    }
}
