package seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import seeingvoice.jskj.com.seeingvoice.MyApp;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.util.CalcUtils;

import static seeingvoice.jskj.com.seeingvoice.MyData.Xlabel;
import static seeingvoice.jskj.com.seeingvoice.MyData.Ylabel;

/**
 * @author  LeoReny@hypech.com
 * @version 3.0
 * @since   2021-02-13
 */
public class L_Chart_Original extends View {
    //灰色背景的画笔
    private Paint mPaint_bg;
    // 默认边距
    private int Margin = 80;
    // 原点坐标
    private int Xpoint,Ypoint,Xscale,Yscale,screenWidth,screenHeight,sub;
    // 标题文本
    private String Title;
    // 曲线数据
    private int[] lrData,lData,rData;
    private int startX,startY,stopX,stopY;
    public String sLR = "LR";

    public L_Chart_Original(Context context) {
        this(context, null);
    }

    public L_Chart_Original(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public L_Chart_Original(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initLineChartView(int[] leftEarDatas, int[] rightEarDatas, String LorR) {
        this.lData = leftEarDatas;
        this.rData = rightEarDatas;
        this.sLR = LorR;

        init();
        postInvalidate();
    }

    // 初始化数据值
    public void init() {
        //Xpoint,Ypoint是原点的坐标
        screenWidth = MyApp.screenWidth;
        screenHeight = MyApp.screenHeight/2-2*this.Margin;
        Xpoint = this.Margin;
        Ypoint = this.Margin;
        Xscale = (screenWidth - 2 * this.Margin) / 20;//Xlabel 长度是9  8个空
        Yscale = (screenHeight - 2 * this.Margin)/ (Ylabel.length - 1);

        mPaint_bg=new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint_bg.setColor(Color.argb(0xff,0xef,0xef,0xef));

        invalidate();   //sv
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);//画布背景颜色
        //绘制灰色矩形区域
        canvas.drawRect(10,0,screenWidth,screenHeight, mPaint_bg);

        Paint p1 = new Paint();
        p1.setStyle(Paint.Style.STROKE);
        p1.setAntiAlias(true);
        p1.setAlpha(10);            // transparency. 255 no-trans
        p1.setColor(0xFF008b8b);
        p1.setStrokeWidth(3);

        Paint p2 = new Paint();
        p2.setStyle(Paint.Style.STROKE);
        p2.setAntiAlias(true);
        p2.setColor(Color.BLACK);

        Paint p3 = new Paint();
        p3.setStyle(Paint.Style.STROKE);
        p3.setAntiAlias(true);
        p3.setPathEffect(new DashPathEffect( new float[]{ 3, 2 }, 0 ));//线的样式：圆角半径
        p3.setColor(Color.BLACK);

        Paint p4 = new Paint();
        p4.setStyle(Paint.Style.STROKE);
        p4.setAntiAlias(true);
        p4.setPathEffect(new DashPathEffect( new float[]{ 3, 3 }, 0 ));//线的样式：圆角半径
        p4.setStrokeWidth(3);
        p4.setColor(Color.GREEN);

        Paint paintText = new Paint();//写字的画笔
        paintText.setStyle(Paint.Style.FILL);
        paintText.setTextSize(this.Margin/4);
        paintText.setColor(Color.BLUE);

        this.drawTableY(canvas,p2,paintText);//画虚线表格
        this.drawTableHalfY(canvas,p3,paintText);//画虚线表格
        this.drawTableX(canvas,p2,paintText);//画虚线表格
        this.drawSafeLine(canvas,p4,paintText);//画虚线表格
        this.drawTips(canvas);//画说明问题

        if (sLR == "L"){//只显示
            drawData(canvas, "L");//只画  左侧结果
        }else if (sLR == "R"){
            drawData(canvas,"R");//只画 右侧结果
        }else{                   //显示双耳数据
            this.drawData(canvas, "LR");
        }
    }

    // 画数据
    private void drawData(Canvas canvas, String lr) {
        String sideLR = lr;
        Paint p = new Paint();

        p.setAntiAlias(true);
        p.setTextSize(this.Margin / 2);
        p.setStyle(Paint.Style.STROKE);
        Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.bw);
        int bw = temp.getWidth();//蓝×的宽度
        int bh = temp.getHeight();//蓝×的高度
        int startX = 0,stopX = 0;

        if (sideLR=="L" || sideLR=="LR") {
            p.setColor(Color.BLUE);

            for (int i = 0; i < lData.length; i++) {
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
                canvas.drawBitmap(temp, startX - bw / 2, calY(lData[i]) - bh / 2, p);//蓝色×
                if (i < 9) {
                    canvas.drawLine(startX, calY(lData[i]), stopX, calY(lData[i + 1]), p);
                }
            }
        }
        if (sideLR == "R" || sideLR == "LR") {
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
                canvas.drawCircle(startX, calY(rData[i]), 10, p);//红圈
                if (i < 9) {
                    canvas.drawLine(startX, calY(rData[i]), stopX, calY(rData[i + 1]), p);
                }
            }
        }
    }

    private void drawSafeLine(Canvas canvas, Paint paint, Paint paintText) {
        Path path = new Path();
        startX = Xpoint;
        startY = Ypoint + 7 * Yscale;
        stopX = screenWidth - this.Margin;
        stopY = Ypoint + 7 * Yscale;

        path.moveTo(startX, startY);
        path.lineTo(stopX, stopY);
        canvas.drawPath(path, paint);
        paintText.setColor(Color.GREEN);
        canvas.drawText("安全线", screenWidth-this.Margin,stopY, paintText);
    }


    private void drawTableX(Canvas canvas, Paint paint, Paint paintText) {

        Path path = new Path();
        // 横向线
        canvas.drawText("分贝(dB)", this.Margin/6, startY-this.Margin/2, paintText);
        for (int i = 0; i < Ylabel.length; i++) {
            startX = Xpoint;
            startY = Ypoint + i * Yscale;
            stopX = screenWidth - this.Margin;
            stopY = Ypoint + i * Yscale;
            if (i%2 == 0){
                path.moveTo(startX, startY);
                path.lineTo(stopX, stopY);
                paint.setColor(Color.BLACK);
                canvas.drawPath(path, paint);
                paint.setColor(Color.GREEN);
                paint.setTextSize(this.Margin / 2);
                canvas.drawText(Ylabel[i]+"", this.Margin/4, startY+this.Margin/6, paintText);
            }
        }
    }
    private void drawTableHalfY(Canvas canvas, Paint paint, Paint paintText) {
        Path path = new Path();
        int j = 0;
        do {
            if ( j == 3 || j == 5 || j ==7 || j ==9){
                switch (j){
                    case 3:
                        startX = Xpoint + j*3*Xscale;//原点坐标+空格 = 新的竖线 横坐标的位置
                        break;
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
                startY = Ypoint;//竖线的Y轴位置始终是Ypoint
                stopX = startX;
                stopY = (Ylabel.length-1)*Yscale + this.Margin;
                path.moveTo(startX, startY);
                path.lineTo(stopX, stopY);
                canvas.drawPath(path, paint);
                canvas.drawText(Xlabel[j], startX - Xscale/3,Yscale*Ylabel.length+this.Margin, paintText);//+this.Margin + this.Margin/2
            }
        }while (j++ <= Xlabel.length +1);
        canvas.drawText("赫兹(Hz)", startX + Xscale,Yscale*Ylabel.length+this.Margin, paintText);
    }
    // 画表格
    private void drawTableY(Canvas canvas, Paint paint, Paint paintText) {
        Path path = new Path();
        int j = 0;
        do {
            if (j == 11){//8k后面的那个
                startX = Xpoint + 20 * Xscale;//原点坐标+空格 = 新的竖线 横坐标的位置
                startY = Ypoint;//竖线的Y轴位置始终是Ypoint
                stopX = startX;
                stopY = (Ylabel.length-1)*Yscale  + this.Margin;
                path.moveTo(startX, startY);
                path.lineTo(stopX, stopY);
                canvas.drawPath(path, paint);
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
            stopY = (Ylabel.length-1)*Yscale  + this.Margin;
            path.moveTo(startX, startY);
            path.lineTo(stopX, stopY);
            path.close();

            if (j != 3 && j != 5 && j != 7 && j != 9){
                canvas.drawPath(path, paint);
                canvas.drawText(Xlabel[j], startX - Xscale/2,2*this.Margin/3, paintText);
            }
        }while (j++ <= Xlabel.length +1);
    }

    /**
     * @param y
     * @return
     */
    private float calY(int y) {

        Double y0 = Double.valueOf(y);


        if (y >0){
            y0 = CalcUtils.divide(y0,120d)*24*Yscale+this.Margin+2*Yscale;
            return Float.valueOf(String.valueOf(y0));
        }

        if (y < 0){
            y0 = Double.valueOf(10 - Math.abs(y))/10*2*Yscale + this.Margin;
            return Float.valueOf(String.valueOf(y0));
        }

        if (y == 0){
            return this.Margin + 2*Yscale;
        }
        if (y == -10){
            return this.Margin;
        }
        if (y == 120){
            return this.getHeight()-this.Margin;
        }
        return -1;
    }

    //画说明
    private void drawTips(Canvas canvas){
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.BLUE);
        p.setTextSize(this.Margin/4);
        p.setStyle(Paint.Style.FILL);
        canvas.drawText("纯音听力测试结果仅供参考",this.Margin,Yscale*Ylabel.length+this.Margin,p);
    }
}
