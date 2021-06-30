package seeingvoice.jskj.com.seeingvoice.ui;

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

/**
 * Date:2019/8/19
 * Time:9:24
 * auther:zyy
 */
public class LineChartView_Original extends View {
    // 默认边距
    private int Margin = 80;
    // 原点坐标
    private int Xpoint,Ypoint,Xscale,Yscale,screenWidth,screenHeight,sub;
    private String[] Xlabel = {"125","250", "500", "750", "1K", "1.5K", "2K", "3K", "4K", "6K", "8K"};
    private int[] Ylabel = {-10, -5, 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50,
            55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120};
    // 标题文本
    private String Title;
    // 曲线数据
    private Integer[] Data,leftData,rightData;
    private boolean isLeft = false,isLeftRight = false;
    private int startX = 0,startY = 0,stopX = 0,stopY = 0;
    private boolean isOldFlag = false,isLeftNoData = false,isRightNoData = false, isOnlyShowLeft = false, isOnlyShowRight = false,isSelfDefine = false;

    public LineChartView_Original(Context context) {
        this(context, null);
    }

    public LineChartView_Original(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public LineChartView_Original(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 初始化数据
     * @param xlabel
     * @param ylabel
     * @param title
     * @param data
     */
    public void initLineChartView(String[] xlabel, int[] ylabel, String title, Integer[] data) {
        isSelfDefine = true;
        this.Xlabel = xlabel;
        this.Ylabel = ylabel;
        this.Title = title;
        this.Data = data;
        if (title.equals("左耳结果")){
            isOnlyShowLeft = true;
            isOnlyShowRight = false;
        }else if (title.equals("右耳结果")){
            isOnlyShowRight = true;
            isOnlyShowLeft = false;
        }
        init();
    }

    public void initLineChartView(String[] xlabel, int[] ylabel, Integer[] leftEarDatas, Integer[] rightEarDatas, boolean isOld) {
        isSelfDefine = true;
        this.Xlabel = xlabel;
        this.Ylabel = ylabel;
        this.isOldFlag = isOld;
        this.isOnlyShowRight = false;
        this.isOnlyShowLeft = false;
        if (isOldFlag){
            this.leftData = new Integer[9];
            this.leftData = leftEarDatas;
            this.rightData = new Integer[9];
            this.rightData = rightEarDatas;
            isLeftRight = true;
        }else {
            this.leftData = new Integer[10];
            this.leftData = leftEarDatas;

            this.rightData = new Integer[10];
            this.rightData = rightEarDatas;//125

            if (leftData[0] != 121 && rightData[0] != 121){
                isLeftRight = true;//双耳都有结果数据
            }

            if (leftData[0] == 121 && rightData[0] != 121){
                isLeft = false;//左耳没数据
                isLeftRight = false;//不是双耳有结果，等于左耳没数据
                isLeftNoData = true;
            }
            if (leftData[0] != 121 && rightData[0] == 121){
                isLeft = true;//右耳有数据
                isLeftRight = false;//不是双耳有结果，等于右耳没数据
                isRightNoData = true;
            }
        }
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
        Yscale = (screenHeight - 2 * this.Margin)/ (this.Ylabel.length - 1);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);//画布背景颜色
        Paint p1 = new Paint();
        p1.setStyle(Paint.Style.STROKE);
        p1.setAntiAlias(true);
        p1.setColor(Color.BLACK);
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

        this.drawXLine(canvas, p1);//画横轴
        this.drawYLine(canvas, p1);//画纵轴
        this.drawTableY(canvas,p2,paintText);//画虚线表格
        this.drawTableHalfY(canvas,p3,paintText);//画虚线表格
        this.drawTableX(canvas,p2,paintText);//画虚线表格
        this.drawSafeLine(canvas,p4,paintText);//画虚线表格
        this.drawTips(canvas);//画说明问题
        if (isSelfDefine){
            if (!isOnlyShowLeft && !isOnlyShowRight){//显示双耳数据
                this.drawData(canvas);//画折线
            }

            if (isOnlyShowLeft && !isOnlyShowRight){//只显示
                drawLeftData(canvas);//只画  左侧结果
            }

            if (isOnlyShowRight && !isOnlyShowLeft){
                drawRightData(canvas);//只画 右侧结果
            }
        }

    }

    // 画数据
    private void drawData(Canvas canvas) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.BLUE);
        p.setTextSize(this.Margin / 2);
        p.setStyle(Paint.Style.STROKE);
        Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.bw);
        int bw = temp.getWidth();//蓝×的宽度
        int bh = temp.getHeight();//蓝×的高度
//        float scale = Math.min(1f * mWidth / bw, 1f * mHeight / bh);
//        bitmap = scaleBitmap(temp, scale);
        // 纵向线
        int startX = 0,stopX = 0;
        if (isOldFlag){//如果是旧版本的测试结果 显示如下
            for (int i = 0; i < leftData.length; i++) {
                switch (i){
                    case 0://250
                        startX = Xpoint + 3 * Xscale + Xscale;
                        stopX = Xpoint + 2*3 * Xscale + Xscale;
                        break;
                    case 1://500
                        startX = Xpoint + 2*3 * Xscale + Xscale;
                        stopX = Xpoint + 3*3 * Xscale + Xscale;
                        break;
                    case 2://1K
                        startX = Xpoint + 3*3 * Xscale + Xscale;
                        stopX = Xpoint + 4*3 * Xscale;
                        break;
                    case 3://1.5k
                        startX = Xpoint + 4*3 * Xscale;
                        stopX = Xpoint + 4*3 * Xscale + Xscale;
                        break;
                    case 4://2k
                        startX = Xpoint + 4*3 * Xscale + Xscale;
                        stopX = Xpoint + 5*3 * Xscale;
                        break;
                    case 5://3k
                        startX = Xpoint + 5*3 * Xscale;
                        stopX = Xpoint + 5*3 * Xscale + Xscale;
                        break;
                    case 6://4k
                        startX = Xpoint + 5*3 * Xscale + Xscale;
                        stopX = Xpoint + 6*3 * Xscale;
                        break;
                    case 7://6k
                        startX = Xpoint + 6*3 * Xscale;
                        stopX = Xpoint + 6*3 * Xscale + Xscale;
                        break;
                    case 8://8k
                        startX = Xpoint + 6*3 * Xscale + Xscale;
                        break;
                }
                canvas.drawBitmap(temp,startX-bw/2,calY(leftData[i])-bh/2,p);//蓝色×
                if (i <8) {
                    canvas.drawLine(startX, calY(leftData[i]), stopX, calY(leftData[i + 1]), p);
                }
            }

            p.setColor(Color.RED);
            for (int i = 0; i < rightData.length; i++) {
                switch (i){
                    case 0://250
                        startX = Xpoint + 3 * Xscale + Xscale;
                        stopX = Xpoint + 2*3 * Xscale + Xscale;
                        break;
                    case 1://500
                        startX = Xpoint + 2*3 * Xscale + Xscale;
                        stopX = Xpoint + 3*3 * Xscale + Xscale;
                        break;
                    case 2://1K
                        startX = Xpoint + 3*3 * Xscale + Xscale;
                        stopX = Xpoint + 4*3 * Xscale;
                        break;
                    case 3://1.5k
                        startX = Xpoint + 4*3 * Xscale;
                        stopX = Xpoint + 4*3 * Xscale + Xscale;
                        break;
                    case 4://2k
                        startX = Xpoint + 4*3 * Xscale + Xscale;
                        stopX = Xpoint + 5*3 * Xscale;
                        break;
                    case 5://3k
                        startX = Xpoint + 5*3 * Xscale;
                        stopX = Xpoint + 5*3 * Xscale + Xscale;
                        break;
                    case 6://4k
                        startX = Xpoint + 5*3 * Xscale + Xscale;
                        stopX = Xpoint + 6*3 * Xscale;
                        break;
                    case 7:
                        startX = Xpoint + 6*3 * Xscale;
                        stopX = Xpoint + 6*3 * Xscale + Xscale;
                        break;
                    case 8:
                        startX = Xpoint + 6*3 * Xscale + Xscale;
                        break;
                }
                canvas.drawCircle(startX, calY(rightData[i]), 10, p);//红圈
                if (i <8) {
                    canvas.drawLine(startX, calY(rightData[i]), stopX, calY(rightData[i + 1]), p);
                }
            }
        }else {//新版本测试结果
            if (!isLeftRight && isLeft){//只有左耳结果
                p.setColor(Color.BLUE);
                for (int i = 0; i < leftData.length; i++) {
                    switch (i){
                        case 0://125
                            startX = Xpoint + Xscale;
                            stopX = Xpoint + 3 * Xscale + Xscale;
                            break;
                        case 1://250
                            startX = Xpoint + 3 * Xscale + Xscale;
                            stopX = Xpoint + 2*3 * Xscale + Xscale;
                            break;
                        case 2://500
                            startX = Xpoint + 2*3 * Xscale + Xscale;
                            stopX = Xpoint + 3*3 * Xscale + Xscale;
                            break;
                        case 3://1k
                            startX = Xpoint + 3*3 * Xscale + Xscale;
                            stopX = Xpoint + 4*3 * Xscale;
                            break;
                        case 4://1.5k
                            startX = Xpoint + 4*3 * Xscale;
                            stopX = Xpoint + 4*3 * Xscale + Xscale;
                            break;
                        case 5://2k
                            startX = Xpoint + 4*3 * Xscale + Xscale;
                            stopX = Xpoint + 5*3 * Xscale;
                            break;
                        case 6://3k
                            startX = Xpoint + 5*3 * Xscale;
                            stopX = Xpoint + 5*3 * Xscale + Xscale;
                            break;
                        case 7://4k
                            startX = Xpoint + 5*3 * Xscale + Xscale;
                            stopX = Xpoint + 6*3 * Xscale;
                            break;
                        case 8://6k
                            startX = Xpoint + 6*3 * Xscale;
                            stopX = Xpoint + 6*3 * Xscale + Xscale;
                            break;
                        case 9://8k
                            startX = Xpoint + 6*3 * Xscale + Xscale;
                            break;
                    }

                    canvas.drawBitmap(temp,startX-bw/2,calY(leftData[i])-bh/2,p);//蓝色×
                    if (i <9) {
                        canvas.drawLine(startX, calY(leftData[i]), stopX, calY(leftData[i + 1]), p);
                    }
                }
                p.setColor(Color.RED);
                canvas.drawText("无右耳数据！",this.Margin+screenWidth/2,this.Margin+screenHeight/2,p);
            }

            if (!isLeftRight && !isLeft){//只有右耳结果
                p.setColor(Color.RED);
                for (int i = 0; i < rightData.length; i++) {
                    switch (i){
                        case 0://125
                            startX = Xpoint + Xscale;
                            stopX = Xpoint + 3 * Xscale + Xscale;
                            break;
                        case 1://250
                            startX = Xpoint + 3 * Xscale + Xscale;
                            stopX = Xpoint + 2*3 * Xscale + Xscale;
                            break;
                        case 2://500
                            startX = Xpoint + 2*3 * Xscale + Xscale;
                            stopX = Xpoint + 3*3 * Xscale + Xscale;
                            break;
                        case 3://1k
                            startX = Xpoint + 3*3 * Xscale + Xscale;
                            stopX = Xpoint + 4*3 * Xscale;
                            break;
                        case 4://1.5k
                            startX = Xpoint + 4*3 * Xscale;
                            stopX = Xpoint + 4*3 * Xscale + Xscale;
                            break;
                        case 5://2k
                            startX = Xpoint + 4*3 * Xscale + Xscale;
                            stopX = Xpoint + 5*3 * Xscale;
                            break;
                        case 6://3k
                            startX = Xpoint + 5*3 * Xscale;
                            stopX = Xpoint + 5*3 * Xscale + Xscale;
                            break;
                        case 7://4k
                            startX = Xpoint + 5*3 * Xscale + Xscale;
                            stopX = Xpoint + 6*3 * Xscale;
                            break;
                        case 8://6k
                            startX = Xpoint + 6*3 * Xscale;
                            stopX = Xpoint + 6*3 * Xscale + Xscale;
                            break;
                        case 9://8k
                            startX = Xpoint + 6*3 * Xscale + Xscale;
                            break;
                    }
                    canvas.drawCircle(startX, calY(rightData[i]), 10, p);//红圈
                    if (i <9) {
                        canvas.drawLine(startX, calY(rightData[i]), stopX, calY(rightData[i + 1]), p);
                    }
                }
                p.setColor(Color.BLUE);
                canvas.drawText("无左耳数据！",this.Margin+screenWidth/2,this.Margin+screenHeight/2,p);
            }

            if (isLeftRight){//双耳都有结果
                p.setColor(Color.BLUE);
                for (int i = 0; i < leftData.length; i++) {
                    switch (i){
                        case 0://125
                            startX = Xpoint + Xscale;
                            stopX = Xpoint + 3 * Xscale + Xscale;
                            break;
                        case 1://250
                            startX = Xpoint + 3 * Xscale + Xscale;
                            stopX = Xpoint + 2*3 * Xscale + Xscale;
                            break;
                        case 2://500
                            startX = Xpoint + 2*3 * Xscale + Xscale;
                            stopX = Xpoint + 3*3 * Xscale + Xscale;
                            break;
                        case 3://1k
                            startX = Xpoint + 3*3 * Xscale + Xscale;
                            stopX = Xpoint + 4*3 * Xscale;
                            break;
                        case 4://1.5k
                            startX = Xpoint + 4*3 * Xscale;
                            stopX = Xpoint + 4*3 * Xscale + Xscale;
                            break;
                        case 5://2k
                            startX = Xpoint + 4*3 * Xscale + Xscale;
                            stopX = Xpoint + 5*3 * Xscale;
                            break;
                        case 6://3k
                            startX = Xpoint + 5*3 * Xscale;
                            stopX = Xpoint + 5*3 * Xscale + Xscale;
                            break;
                        case 7://4k
                            startX = Xpoint + 5*3 * Xscale + Xscale;
                            stopX = Xpoint + 6*3 * Xscale;
                            break;
                        case 8://6k
                            startX = Xpoint + 6*3 * Xscale;
                            stopX = Xpoint + 6*3 * Xscale + Xscale;
                            break;
                        case 9://8k
                            startX = Xpoint + 6*3 * Xscale + Xscale;
                            break;
                    }
                    canvas.drawBitmap(temp,startX-bw/2,calY(leftData[i])-bh/2,p);//蓝色×
                    if (i <9) {
                        canvas.drawLine(startX, calY(leftData[i]), stopX, calY(leftData[i + 1]), p);
                    }
                }
                p.setColor(Color.RED);
                for (int i = 0; i < rightData.length; i++) {
                    switch (i){
                        case 0://125
                            startX = Xpoint + Xscale;
                            stopX = Xpoint + 3 * Xscale + Xscale;
                            break;
                        case 1://250
                            startX = Xpoint + 3 * Xscale + Xscale;
                            stopX = Xpoint + 2*3 * Xscale + Xscale;
                            break;
                        case 2://500
                            startX = Xpoint + 2*3 * Xscale + Xscale;
                            stopX = Xpoint + 3*3 * Xscale + Xscale;
                            break;
                        case 3://1k
                            startX = Xpoint + 3*3 * Xscale + Xscale;
                            stopX = Xpoint + 4*3 * Xscale;
                            break;
                        case 4://1.5k
                            startX = Xpoint + 4*3 * Xscale;
                            stopX = Xpoint + 4*3 * Xscale + Xscale;
                            break;
                        case 5://2k
                            startX = Xpoint + 4*3 * Xscale + Xscale;
                            stopX = Xpoint + 5*3 * Xscale;
                            break;
                        case 6://3k
                            startX = Xpoint + 5*3 * Xscale;
                            stopX = Xpoint + 5*3 * Xscale + Xscale;
                            break;
                        case 7://4k
                            startX = Xpoint + 5*3 * Xscale + Xscale;
                            stopX = Xpoint + 6*3 * Xscale;
                            break;
                        case 8://6k
                            startX = Xpoint + 6*3 * Xscale;
                            stopX = Xpoint + 6*3 * Xscale + Xscale;
                            break;
                        case 9://8k
                            startX = Xpoint + 6*3 * Xscale + Xscale;
                            break;
                    }
                    canvas.drawCircle(startX, calY(rightData[i]), 10, p);//红圈
                    if (i <9) {
                        canvas.drawLine(startX, calY(rightData[i]), stopX, calY(rightData[i + 1]), p);
                    }
                }
            }
        }
    }

    private void drawLeftData(Canvas canvas) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.BLUE);
        p.setTextSize(this.Margin / 2);
        p.setStyle(Paint.Style.STROKE);
        Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.bw);
        int bw = temp.getWidth();//蓝×的宽度
        int bh = temp.getHeight();//蓝×的高度
//        float scale = Math.min(1f * mWidth / bw, 1f * mHeight / bh);
//        bitmap = scaleBitmap(temp, scale);
        // 纵向线
        int startX = 0,stopX = 0;
        if (isOldFlag){
            for (int i = 0; i < leftData.length; i++) {
                switch (i){
                    case 0://250
                        startX = Xpoint + 3 * Xscale + Xscale;
                        stopX = Xpoint + 2*3 * Xscale + Xscale;
                        break;
                    case 1://500
                        startX = Xpoint + 2*3 * Xscale + Xscale;
                        stopX = Xpoint + 3*3 * Xscale + Xscale;
                        break;
                    case 2://1K
                        startX = Xpoint + 3*3 * Xscale + Xscale;
                        stopX = Xpoint + 4*3 * Xscale;
                        break;
                    case 3://1.5k
                        startX = Xpoint + 4*3 * Xscale;
                        stopX = Xpoint + 4*3 * Xscale + Xscale;
                        break;
                    case 4://2k
                        startX = Xpoint + 4*3 * Xscale + Xscale;
                        stopX = Xpoint + 5*3 * Xscale;
                        break;
                    case 5://3k
                        startX = Xpoint + 5*3 * Xscale;
                        stopX = Xpoint + 5*3 * Xscale + Xscale;
                        break;
                    case 6://4k
                        startX = Xpoint + 5*3 * Xscale + Xscale;
                        stopX = Xpoint + 6*3 * Xscale;
                        break;
                    case 7://6k
                        startX = Xpoint + 6*3 * Xscale;
                        stopX = Xpoint + 6*3 * Xscale + Xscale;
                        break;
                    case 8://8k
                        startX = Xpoint + 6*3 * Xscale + Xscale;
                        break;
                }
                canvas.drawBitmap(temp,startX-bw/2,calY(leftData[i])-bh/2,p);//蓝色×
                if (i <8) {
                    canvas.drawLine(startX, calY(leftData[i]), stopX, calY(leftData[i + 1]), p);
                }
            }
        }else {
            if (!isLeftRight && isLeft) {//只有左耳结果
                for (int i = 0; i < leftData.length; i++) {
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

                    canvas.drawBitmap(temp, startX - bw / 2, calY(leftData[i]) - bh / 2, p);//蓝色×
                    if (i < 9) {
                        canvas.drawLine(startX, calY(leftData[i]), stopX, calY(leftData[i + 1]), p);
                    }
                }
            }

            if (!isLeftRight && !isLeft){//只有右耳结果
                p.setColor(Color.BLUE);
                canvas.drawText("无左耳数据！",this.Margin+screenWidth/2,this.Margin+screenHeight/2,p);
            }

            if (isLeftRight){//双耳都有结果
                for (int i = 0; i < leftData.length; i++) {
                    switch (i){
                        case 0://125
                            startX = Xpoint + Xscale;
                            stopX = Xpoint + 3 * Xscale + Xscale;
                            break;
                        case 1://250
                            startX = Xpoint + 3 * Xscale + Xscale;
                            stopX = Xpoint + 2*3 * Xscale + Xscale;
                            break;
                        case 2://500
                            startX = Xpoint + 2*3 * Xscale + Xscale;
                            stopX = Xpoint + 3*3 * Xscale + Xscale;
                            break;
                        case 3://1k
                            startX = Xpoint + 3*3 * Xscale + Xscale;
                            stopX = Xpoint + 4*3 * Xscale;
                            break;
                        case 4://1.5k
                            startX = Xpoint + 4*3 * Xscale;
                            stopX = Xpoint + 4*3 * Xscale + Xscale;
                            break;
                        case 5://2k
                            startX = Xpoint + 4*3 * Xscale + Xscale;
                            stopX = Xpoint + 5*3 * Xscale;
                            break;
                        case 6://3k
                            startX = Xpoint + 5*3 * Xscale;
                            stopX = Xpoint + 5*3 * Xscale + Xscale;
                            break;
                        case 7://4k
                            startX = Xpoint + 5*3 * Xscale + Xscale;
                            stopX = Xpoint + 6*3 * Xscale;
                            break;
                        case 8://6k
                            startX = Xpoint + 6*3 * Xscale;
                            stopX = Xpoint + 6*3 * Xscale + Xscale;
                            break;
                        case 9://8k
                            startX = Xpoint + 6*3 * Xscale + Xscale;
                            break;
                    }
                    canvas.drawBitmap(temp,startX-bw/2,calY(leftData[i])-bh/2,p);//蓝色×
                    if (i <9) {
                        canvas.drawLine(startX, calY(leftData[i]), stopX, calY(leftData[i + 1]), p);
                    }
                }
            }
        }
    }

    private void drawRightData(Canvas canvas) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.BLUE);
        p.setTextSize(this.Margin / 2);
        p.setStyle(Paint.Style.STROKE);
        Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.bw);
        int bw = temp.getWidth();//蓝×的宽度
        int bh = temp.getHeight();//蓝×的高度
//        float scale = Math.min(1f * mWidth / bw, 1f * mHeight / bh);
//        bitmap = scaleBitmap(temp, scale);
        // 纵向线
        int startX = 0,stopX = 0;
        if (isOldFlag){
            p.setColor(Color.RED);
            for (int i = 0; i < rightData.length; i++) {
                switch (i) {
                    case 0://250
                        startX = Xpoint + 3 * Xscale + Xscale;
                        stopX = Xpoint + 2 * 3 * Xscale + Xscale;
                        break;
                    case 1://500
                        startX = Xpoint + 2 * 3 * Xscale + Xscale;
                        stopX = Xpoint + 3 * 3 * Xscale + Xscale;
                        break;
                    case 2://1K
                        startX = Xpoint + 3 * 3 * Xscale + Xscale;
                        stopX = Xpoint + 4 * 3 * Xscale;
                        break;
                    case 3://1.5k
                        startX = Xpoint + 4 * 3 * Xscale;
                        stopX = Xpoint + 4 * 3 * Xscale + Xscale;
                        break;
                    case 4://2k
                        startX = Xpoint + 4 * 3 * Xscale + Xscale;
                        stopX = Xpoint + 5 * 3 * Xscale;
                        break;
                    case 5://3k
                        startX = Xpoint + 5 * 3 * Xscale;
                        stopX = Xpoint + 5 * 3 * Xscale + Xscale;
                        break;
                    case 6://4k
                        startX = Xpoint + 5 * 3 * Xscale + Xscale;
                        stopX = Xpoint + 6 * 3 * Xscale;
                        break;
                    case 7:
                        startX = Xpoint + 6 * 3 * Xscale;
                        stopX = Xpoint + 6 * 3 * Xscale + Xscale;
                        break;
                    case 8:
                        startX = Xpoint + 6 * 3 * Xscale + Xscale;
                        break;
                }
                canvas.drawCircle(startX, calY(rightData[i]), 10, p);//红圈
                if (i < 8) {
                    canvas.drawLine(startX, calY(rightData[i]), stopX, calY(rightData[i + 1]), p);
                }
            }
        }else {
            if (!isLeftRight && isLeft){//只有左耳结果
                for (int i = 0; i < leftData.length; i++) {
                    p.setColor(Color.RED);
                    canvas.drawText("无右耳数据！",this.Margin+screenWidth/2,this.Margin+screenHeight/2,p);
                }
            }
            if (!isLeftRight && !isLeft){//只有右耳结果
                p.setColor(Color.RED);
                for (int i = 0; i < rightData.length; i++) {
                    switch (i){
                        case 0://125
                            startX = Xpoint + Xscale;
                            stopX = Xpoint + 3 * Xscale + Xscale;
                            break;
                        case 1://250
                            startX = Xpoint + 3 * Xscale + Xscale;
                            stopX = Xpoint + 2*3 * Xscale + Xscale;
                            break;
                        case 2://500
                            startX = Xpoint + 2*3 * Xscale + Xscale;
                            stopX = Xpoint + 3*3 * Xscale + Xscale;
                            break;
                        case 3://1k
                            startX = Xpoint + 3*3 * Xscale + Xscale;
                            stopX = Xpoint + 4*3 * Xscale;
                            break;
                        case 4://1.5k
                            startX = Xpoint + 4*3 * Xscale;
                            stopX = Xpoint + 4*3 * Xscale + Xscale;
                            break;
                        case 5://2k
                            startX = Xpoint + 4*3 * Xscale + Xscale;
                            stopX = Xpoint + 5*3 * Xscale;
                            break;
                        case 6://3k
                            startX = Xpoint + 5*3 * Xscale;
                            stopX = Xpoint + 5*3 * Xscale + Xscale;
                            break;
                        case 7://4k
                            startX = Xpoint + 5*3 * Xscale + Xscale;
                            stopX = Xpoint + 6*3 * Xscale;
                            break;
                        case 8://6k
                            startX = Xpoint + 6*3 * Xscale;
                            stopX = Xpoint + 6*3 * Xscale + Xscale;
                            break;
                        case 9://8k
                            startX = Xpoint + 6*3 * Xscale + Xscale;
                            break;
                    }
                    canvas.drawCircle(startX, calY(rightData[i]), 10, p);//红圈
                    if (i <9) {
                        canvas.drawLine(startX, calY(rightData[i]), stopX, calY(rightData[i + 1]), p);
                    }
                }
            }

            if (isLeftRight){//双耳都有结果
                p.setColor(Color.RED);
                for (int i = 0; i < rightData.length; i++) {
                    switch (i){
                        case 0://125
                            startX = Xpoint + Xscale;
                            stopX = Xpoint + 3 * Xscale + Xscale;
                            break;
                        case 1://250
                            startX = Xpoint + 3 * Xscale + Xscale;
                            stopX = Xpoint + 2*3 * Xscale + Xscale;
                            break;
                        case 2://500
                            startX = Xpoint + 2*3 * Xscale + Xscale;
                            stopX = Xpoint + 3*3 * Xscale + Xscale;
                            break;
                        case 3://1k
                            startX = Xpoint + 3*3 * Xscale + Xscale;
                            stopX = Xpoint + 4*3 * Xscale;
                            break;
                        case 4://1.5k
                            startX = Xpoint + 4*3 * Xscale;
                            stopX = Xpoint + 4*3 * Xscale + Xscale;
                            break;
                        case 5://2k
                            startX = Xpoint + 4*3 * Xscale + Xscale;
                            stopX = Xpoint + 5*3 * Xscale;
                            break;
                        case 6://3k
                            startX = Xpoint + 5*3 * Xscale;
                            stopX = Xpoint + 5*3 * Xscale + Xscale;
                            break;
                        case 7://4k
                            startX = Xpoint + 5*3 * Xscale + Xscale;
                            stopX = Xpoint + 6*3 * Xscale;
                            break;
                        case 8://6k
                            startX = Xpoint + 6*3 * Xscale;
                            stopX = Xpoint + 6*3 * Xscale + Xscale;
                            break;
                        case 9://8k
                            startX = Xpoint + 6*3 * Xscale + Xscale;
                            break;
                    }
                    canvas.drawCircle(startX, calY(rightData[i]), 10, p);//红圈
                    if (i <9) {
                        canvas.drawLine(startX, calY(rightData[i]), stopX, calY(rightData[i + 1]), p);
                    }
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
                canvas.drawText(this.Ylabel[i]+"", this.Margin/4, startY+this.Margin/6, paintText);
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
                canvas.drawText(this.Xlabel[j], startX - Xscale/3,Yscale*Ylabel.length+this.Margin, paintText);//+this.Margin + this.Margin/2
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
                canvas.drawText(this.Xlabel[j], startX - Xscale/2,2*this.Margin/3, paintText);
            }
        }while (j++ <= Xlabel.length +1);
    }

    // 画X轴
    private void drawXLine(Canvas canvas, Paint p) {
        canvas.drawLine(this.Margin, this.Margin  + 2*Yscale, screenWidth-this.Margin, this.Margin + 2*Yscale, p);
        canvas.drawLine(screenWidth - this.Margin, this.Margin + 2*Yscale, screenWidth - this.Margin - this.Margin / 3, this.Margin + 2*Yscale - this.Margin / 3, p);//箭头一边
        canvas.drawLine(screenWidth - this.Margin, this.Margin + 2*Yscale, screenWidth - this.Margin - this.Margin / 3, this.Margin + 2*Yscale + this.Margin / 3, p);//箭头一边
    }

    // 画Y轴
    private void drawYLine(Canvas canvas, Paint p) {
        canvas.drawLine(this.Margin, this.Margin, this.Margin, screenHeight-this.Margin,p);
        canvas.drawLine(this.Margin, screenHeight-this.Margin, this.Margin - this.Margin/3, screenHeight-this.Margin - this.Margin/3, p);//箭头一边
        canvas.drawLine(this.Margin, screenHeight-this.Margin, this.Margin + this.Margin/3, screenHeight-this.Margin - this.Margin/3, p);//箭头一边
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
