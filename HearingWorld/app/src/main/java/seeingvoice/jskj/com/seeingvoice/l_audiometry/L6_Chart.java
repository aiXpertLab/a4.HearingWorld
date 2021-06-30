package seeingvoice.jskj.com.seeingvoice.l_audiometry;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import seeingvoice.jskj.com.seeingvoice.MyApp;
import seeingvoice.jskj.com.seeingvoice.R;

import static seeingvoice.jskj.com.seeingvoice.MyData.Xlabel;
import static seeingvoice.jskj.com.seeingvoice.MyData.Ylabel;
import static seeingvoice.jskj.com.seeingvoice.MyData.XCM;   //chart CM
import static seeingvoice.jskj.com.seeingvoice.MyData.YCM;   //chart CM

/**
 * @author  LeoReny@hypech.com
 * @version 3.0
 * @since   2021-02-13
 */
public class L6_Chart extends View {

    private Paint mPaint;
    private Typeface tf = Typeface.createFromAsset(getContext().getAssets(),"OpenSans-Regular.ttf");
    private Typeface tfPM = Typeface.createFromAsset(getContext().getAssets(), "PermanentMarker.ttf");
    private int[] lData,rData;
    private int Xpoint, Ypoint, Xscale, Yscale, chartWidth, chartHeight;
    private int startX,startY,stopX,stopY;
    int sp12 = getResources().getDimensionPixelSize(R.dimen.sp12);
    int sp14 = getResources().getDimensionPixelSize(R.dimen.sp14);

    public L6_Chart(Context context) {        this(context, null);    }
    public L6_Chart(Context context, AttributeSet attrs) {        this(context, attrs, 0);
        init();    }
    public L6_Chart(Context context, AttributeSet attrs, int defStyleAttr) {super(context, attrs, defStyleAttr);}

    public void initLineChartView(int[] leftEarDatas, int[] rightEarDatas) {
        this.lData = leftEarDatas;
        this.rData = rightEarDatas;
        init();
        postInvalidate();
    }

    // 初始化数据值
    public void init() {
        chartWidth  = MyApp.screenWidth;              //720
        chartHeight = MyApp.screenHeight/2;    //512 1344
        Xscale = (chartWidth - 2 * XCM) / 20;      //Xlabel 长度是9  8个空
        Yscale = (chartHeight - 2 * YCM)/ (Ylabel.length - 1);
        Xpoint = XCM;
        Ypoint = YCM;

        mPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);

        invalidate();   //sv
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.drawBlock(canvas);                 //Must be the 1st
        this.drawTableX(canvas);       // 纵线们
        this.drawTableY(canvas);       // 横线们
        this.drawData(canvas);         // 数据
        this.drawXLine(canvas);     //x轴
        this.drawYLine(canvas);     //y轴
        this.drawSafeLine(canvas);  //safe line
        this.drawTips(canvas);         //画说明问题
        this.drawTableHalfY(canvas);   // 画虚线y轴
    }

    private void drawBlock(Canvas canvas) {
        canvas.drawColor(Color.WHITE);//画布背景颜色
        mPaint.setColor(Color.argb(0xff, 0xef, 0xef, 0xef));
        canvas.drawRect(XCM, YCM, chartWidth - XCM, chartHeight - YCM, mPaint);

        //绘制右边色块
        mPaint.setColor(Color.parseColor("#90EE90"));
        canvas.drawRect(chartWidth - XCM, YCM, chartWidth + XCM, YCM + Yscale * 2 + Yscale / 2, mPaint);
        mPaint.setColor(Color.parseColor("#9ACD32"));
        canvas.drawRect(chartWidth - XCM, (YCM + Yscale * 2 + Yscale / 2), chartWidth + XCM, YCM + Yscale * 2 + Yscale / 2 + (Yscale + Yscale / 2), mPaint);
        mPaint.setColor(Color.parseColor("#FFD700"));
        canvas.drawRect(chartWidth - XCM, YCM + Yscale * 2 + Yscale / 2 + (Yscale + Yscale / 2), chartWidth + XCM, YCM + Yscale * 2 + Yscale / 2 + (Yscale + Yscale / 2) * 2, mPaint);
        mPaint.setColor(Color.parseColor("#FFA500"));
        canvas.drawRect(chartWidth - XCM, YCM + Yscale * 2 + Yscale / 2 + (Yscale + Yscale / 2) * 2, chartWidth + XCM, YCM + Yscale * 2 + Yscale / 2 + (Yscale + Yscale / 2) * 3, mPaint);
        mPaint.setColor(Color.parseColor("#FF4500"));
        canvas.drawRect(chartWidth - XCM, YCM + Yscale * 2 + Yscale / 2 + (Yscale + Yscale / 2) * 3, chartWidth + XCM, YCM + Yscale * 2 + Yscale / 2 + (Yscale + Yscale / 2) * 4, mPaint);
        mPaint.setColor(Color.parseColor("#FF0000"));
        canvas.drawRect(chartWidth - XCM, YCM + Yscale * 2 + Yscale / 2 + (Yscale + Yscale / 2) * 4, chartWidth + XCM, chartHeight - YCM, mPaint);

        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(sp14);
        mPaint.setFakeBoldText(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(getContext().getString(R.string.chart_good),   chartWidth - XCM / 2, YCM + Yscale, mPaint);
        canvas.drawText(getContext().getString(R.string.chart_hearing),chartWidth - XCM / 2, YCM + 2 * Yscale, mPaint);
        canvas.drawText(getContext().getString(R.string.chart_mini),   chartWidth - XCM / 2, YCM + 3 * Yscale + Yscale / 2, mPaint);
        canvas.drawText(getContext().getString(R.string.chart_middle), chartWidth - XCM / 2, YCM + 5 * Yscale, mPaint);
        canvas.drawText(getContext().getString(R.string.chart_heavy),  chartWidth - XCM / 2, YCM + 6 * Yscale + Yscale / 2, mPaint);
        canvas.drawText(getContext().getString(R.string.chart_heavy),  chartWidth - XCM / 2, YCM + 8 * Yscale, mPaint);
        canvas.drawText(getContext().getString(R.string.chart_heavy),  chartWidth - XCM / 2, chartHeight - YCM - YCM / 2, mPaint);
    }

    // 画X轴
    private void drawXLine(Canvas canvas) {
        mPaint.setColor(Color.rgb(0, 139, 139));
        mPaint.setStrokeWidth(5);

        canvas.drawLine(
                XCM,                YCM,
                chartWidth - XCM,                YCM,
                mPaint);
        canvas.drawLine(
                chartWidth - XCM,                YCM,
                chartWidth - XCM - XCM / 3,      YCM - YCM / 8,
                mPaint);         //箭头一边
        canvas.drawLine(
                chartWidth - XCM,                YCM,
                chartWidth - XCM - XCM / 3,      YCM + YCM / 8,
                mPaint);         //箭头一边
    }

    // 画Y轴
    private void drawYLine(Canvas canvas) {
        canvas.drawLine(XCM, YCM, XCM, chartHeight-YCM,mPaint);
        canvas.drawLine(XCM, chartHeight-YCM, XCM - XCM/8, chartHeight-YCM - YCM/3, mPaint);//箭头一边
        canvas.drawLine(XCM, chartHeight-YCM, XCM + XCM/8, chartHeight-YCM - YCM/3, mPaint);//箭头一边
        mPaint.setStrokeWidth(2);
    }

    // 画数据
    private void drawData(Canvas canvas) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(40);
        p.setTypeface(tfPM);
        p.setStrokeWidth(4);

        int startX = 0,stopX = 0;

        p.setColor(Color.rgb(0,0,128));
        for (int i = 0; i < lData.length; i++) {
            switch (i) {
                case 0://125
                    startX = Xpoint + Xscale;
                    stopX   = Xpoint + 3 * Xscale + Xscale;
                    break;
                case 1://250
                    startX = Xpoint + 3 * Xscale + Xscale;
                    stopX = Xpoint + 2 * 3 * Xscale + Xscale;
                    break;
                case 2://500
                    startX = Xpoint + 2 * 3 * Xscale + Xscale;
                    stopX = Xpoint + 3 * 3 * Xscale + Xscale;
                    break;
                case 3://1k
                    startX = Xpoint + 3 * 3 * Xscale + Xscale;
                    stopX = Xpoint + 4 * 3 * Xscale;
                    break;
                case 4://1.5k
                    startX = Xpoint + 4 * 3 * Xscale;
                    stopX = Xpoint + 4 * 3 * Xscale + Xscale;
                    break;
                case 5://2k
                    startX = Xpoint + 4 * 3 * Xscale + Xscale;
                    stopX = Xpoint + 5 * 3 * Xscale;
                    break;
                case 6://3k
                    startX = Xpoint + 5 * 3 * Xscale;
                    stopX = Xpoint + 5 * 3 * Xscale + Xscale;
                    break;
                case 7://4k
                    startX = Xpoint + 5 * 3 * Xscale + Xscale;
                    stopX = Xpoint + 6 * 3 * Xscale;
                    break;
                case 8://6k
                    startX = Xpoint + 6 * 3 * Xscale;
                    stopX = Xpoint + 6 * 3 * Xscale + Xscale;
                    break;
                case 9://8k
                    startX = Xpoint + 6 * 3 * Xscale + Xscale;
                    break;
            }
            if (i < 10) {
                p.setStyle(Paint.Style.FILL);
                canvas.drawText("X", startX - 12, calY(lData[i]) + 13, p);
            }
            if (i < 9) {
                canvas.drawLine(startX, calY(lData[i]), stopX, calY(lData[i + 1]), p);
            }
        }
        p.setColor(Color.RED);
        for (int i = 0; i < rData.length; i++) {
            switch (i) {
                case 0://125
                    startX = Xpoint + Xscale;
                    stopX = Xpoint + 3 * Xscale + Xscale;
                    break;
                case 1://250
                    startX = Xpoint + 3 * Xscale + Xscale;
                    stopX = Xpoint + 2 * 3 * Xscale + Xscale;
                    break;
                case 2://500
                    startX = Xpoint + 2 * 3 * Xscale + Xscale;
                    stopX = Xpoint + 3 * 3 * Xscale + Xscale;
                    break;
                case 3://1k
                    startX = Xpoint + 3 * 3 * Xscale + Xscale;
                    stopX = Xpoint + 4 * 3 * Xscale;
                    break;
                case 4://1.5k
                    startX = Xpoint + 4 * 3 * Xscale;
                    stopX = Xpoint + 4 * 3 * Xscale + Xscale;
                    break;
                case 5://2k
                    startX = Xpoint + 4 * 3 * Xscale + Xscale;
                    stopX = Xpoint + 5 * 3 * Xscale;
                    break;
                case 6://3k
                    startX = Xpoint + 5 * 3 * Xscale;
                    stopX = Xpoint + 5 * 3 * Xscale + Xscale;
                    break;
                case 7://4k
                    startX = Xpoint + 5 * 3 * Xscale + Xscale;
                    stopX = Xpoint + 6 * 3 * Xscale;
                    break;
                case 8://6k
                    startX = Xpoint + 6 * 3 * Xscale;
                    stopX = Xpoint + 6 * 3 * Xscale + Xscale;
                    break;
                case 9://8k
                    startX = Xpoint + 6 * 3 * Xscale + Xscale;
                    break;
            }
            if (i < 9) {
                canvas.drawLine(startX, calY(rData[i]), stopX, calY(rData[i + 1]), p);
            }
            if (i < 10) {
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(startX, calY(rData[i]), 13, p);           //红实心
                mPaint.setColor(Color.argb(0xff, 0xef, 0xef, 0xef));
                canvas.drawCircle(startX, calY(rData[i]), 10, mPaint);      //灰实心
            }
        }
    }

    private float calY(int y) {
        float yy = (float)y;
        return (float) (yy / 10 * Yscale + YCM);
    }

    private void drawSafeLine(Canvas canvas) {
        Path path = new Path();
        startX = Xpoint;
        startY = Ypoint + 3 * Yscale - Yscale/2;
        stopX  = chartWidth - XCM;
        stopY  = Ypoint + 3 * Yscale- Yscale/2;

        mPaint.setPathEffect(new DashPathEffect( new float[]{ 30, 15 }, 0 ));//线的样式：圆角半径
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Color.parseColor("#9ACD32"));
        mPaint.setTextSize(sp12);
        mPaint.setTextAlign(Paint.Align.RIGHT);

        path.moveTo(startX, startY);
        path.lineTo(stopX, stopY);
        canvas.drawPath(path, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(getContext().getString(R.string.chart_line), chartWidth - XCM-4, stopY, mPaint);
        mPaint.setColor(Color.GRAY);
    }

    private void drawTableX(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < Ylabel.length; i++) {
            startX = Xpoint;
            startY = Ypoint + i * Yscale;
            stopX  = chartWidth - XCM;
            stopY  = Ypoint + i * Yscale;

            path.moveTo(startX, startY);
            path.lineTo(stopX, stopY);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.LTGRAY);
            canvas.drawPath(path, mPaint);

            mPaint.setTypeface(tf);
            mPaint.setTextSize(sp12);
            mPaint.setColor(Color.GRAY);
            mPaint.setTextAlign(Paint.Align.LEFT);
            mPaint.setFakeBoldText(false);
            mPaint.setStyle(Paint.Style.FILL);

            canvas.drawText(Ylabel[i], XCM/4, startY + YCM / 7, mPaint);
        }
        canvas.drawText(getContext().getString(R.string.chart_big), XCM/4, startY - Yscale/2, mPaint);
        canvas.drawText(getContext().getString(R.string.chart_sound), XCM/4, startY - Yscale/2 + Yscale*55/100, mPaint);
        canvas.drawText(getContext().getString(R.string.chart_sound),
                XCM/4, Ypoint + Yscale*6/10, mPaint);
    }

    // 画表格
    private void drawTableY(Canvas canvas) {
        Path path = new Path();
        int j = 0;
        do {
            if (j == 11){                   //8k后面的那个
                startX = Xpoint + 20 * Xscale;//原点坐标+空格 = 新的竖线 横坐标的位置
                startY = Ypoint;//竖线的Y轴位置始终是Ypoint
                stopX = startX;
                stopY = (Ylabel.length-1)*Yscale  + YCM;
                path.moveTo(startX, startY);
                path.lineTo(stopX, stopY);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(Color.LTGRAY);
                canvas.drawPath(path, mPaint);
                break;
            }else if ( j == 4){
                startX = Xpoint + (j-1) *3* Xscale + Xscale;//原点坐标+空格 = 新的竖线 横坐标的位置
            }else if ( j == 6  ){
                startX = Xpoint + (j-2) *3* Xscale + Xscale;//原点坐标+空格 = 新的竖线 横坐标的位置
            }else if (j ==8){
                startX = Xpoint + (j-3) *3* Xscale + Xscale;//原点坐标+空格 = 新的竖线 横坐标的位置
            } else if (j ==10){
                startX = Xpoint + (j-4) *3* Xscale + Xscale;//原点坐标+空格 = 新的竖线 横坐标的位置
            }else if (j == 0 || j == 1 || j == 2){
                startX = Xpoint + j*3*Xscale + Xscale;//原点坐标+空格 = 新的竖线 横坐标的位置
            }
            startY = Ypoint;//竖线的Y轴位置始终是Ypoint
            stopX = startX;
            stopY = (Ylabel.length-1)*Yscale  + YCM;
            path.moveTo(startX, startY);
            path.lineTo(stopX, stopY);
            path.close();

            if (j != 3 && j != 5 && j != 7 && j != 9){
//                canvas.drawPath(path, mPaint);
                mPaint.setTextSize(sp12);
                mPaint.setColor(Color.GRAY);
                canvas.drawText(Xlabel[j], startX - Xscale/2,2*YCM/3, mPaint);
            }
        }while (j++ <= Xlabel.length +1);
    }

    private void drawTableHalfY(Canvas canvas) {
        mPaint.setPathEffect(new DashPathEffect( new float[]{ 30, 20 }, 0 ));//线的样式：圆角半径
        mPaint.setTextSize(sp12);
        mPaint.setFakeBoldText(false);

        Path path = new Path();
        int j = 0;
        do {
            if ( j == 5 || j ==7 || j ==9){
                switch (j){
                    case 5:
                        startX = Xpoint + (j-1)*3*Xscale;//原点坐标+空格 = 新的竖线 横坐标的位置
                        break;
                    case 7:
                        startX = Xpoint + (j-2)*3*Xscale;//原点坐标+空格 = 新的竖线 横坐标的位置
                        break;
                    case 9:
                        startX = Xpoint + (j-3)*3*Xscale;//原点坐标+空格 = 新的竖线 横坐标的位置
                        break;
                }
                startY= Ypoint;//竖线的Y轴位置始终是Ypoint
                stopX = startX;
                stopY = (Ylabel.length-1)*Yscale + YCM;
                path.moveTo(startX, startY);
                path.lineTo(stopX, stopY);

                mPaint.setColor(Color.LTGRAY);
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawPath(path, mPaint);
                
                mPaint.setColor(Color.GRAY);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawText(Xlabel[j], startX - Xscale ,chartHeight, mPaint);
            }
        }while (j++ <= Xlabel.length +1);
        mPaint.setPathEffect(new DashPathEffect( new float[]{ 30, 0 }, 0 ));//线的样式：圆角半径
    }

    //画说明
    private void drawTips(Canvas canvas){
        mPaint.setTextSize(sp12);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setFakeBoldText(false);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setColor(Color.rgb(0, 139, 139));
        canvas.drawText(getContext().getString(R.string.t4_volumeDB),XCM/4,chartHeight, mPaint);
        mPaint.setColor(Color.rgb(0,0,128));
        canvas.drawText(getContext().getString(R.string.chart_xleft), XCM+Xscale*4,chartHeight, mPaint);
        mPaint.setColor(Color.RED);
        canvas.drawText(getContext().getString(R.string.chart_oright), XCM+Xscale*7,chartHeight, mPaint);
    }
}