package com.seeingvoice.www.svhearing.heartests.puretest.mpchart;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.List;

/**
 * Date:2019/3/15
 * Time:15:41
 * auther:zyy
 */
public class LineChartManager {
    private LineChart lineChart;
    private XAxis xAxis;            //X轴
    private YAxis leftYAxis;        //左侧Y轴
    private Legend legend;          //图例
    private LimitLine limitLine;    //限制线
    private String Xlabels[] = {"250","500","1K","1.5K","2K","3K","4K","6K","8K"};

    public LineChartManager(LineChart lineChart) {
        this.lineChart = lineChart;
        leftYAxis = lineChart.getAxisLeft(); //得到
        xAxis = lineChart.getXAxis();
        
        initChart(lineChart);
    }

    private void initChart(LineChart lineChart) {
        /** 图表设置*/
        lineChart.setDrawGridBackground(false);                //是否展示网格线
        lineChart.setBackgroundColor(Color.WHITE);             //设置背景颜色
        lineChart.setDrawBorders(false);                       //是否显示边界
        lineChart.setDoubleTapToZoomEnabled(false);            //双击放大
//        lineChart.setPointerIcon(new PointerIcon());
        Description description = new Description();           //需要展示的内容
        description.setText("纯音测试结果单位dB HL");
        description.setEnabled(true);
        lineChart.setDescription(description);



        /***XY轴的设置***/
        xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);

        leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setDrawGridLines(false);           //设置Y轴网格线为虚线
//        leftYAxis.enableGridDashedLine(10f, 5f, 0f);

        xAxis.setPosition(XAxis.XAxisPosition.TOP);         //X轴设置显示位置在顶部
        xAxis.setGranularity(1f);                           //间隔尺寸
        xAxis.setAxisLineWidth(1f);                         //线的宽度
        leftYAxis.setAxisMinimum(-10f);                     //保证Y轴从0开始，不然会上移一点

        /**根据需求的不同 在此在设置X Y轴的显示内容*/
        xAxis.setLabelCount(9, false);        //绘制x轴标签数量
        xAxis.setDrawAxisLine(false);
//        xAxis.setDrawGridLines(true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setDrawGridLines(true);
        xAxis.setAxisLineWidth(1f);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(8f);
        xAxis.setValueFormatter(new MyXFormatter(Xlabels));     //把自定义的X轴的标签放在上面

        leftYAxis.setLabelCount(23);
        leftYAxis.setDrawZeroLine(true);                        // draw a zero line
//        leftYAxis.setZeroLineColor(Color.GREEN);                  //画0所在的位置的线
        leftYAxis.setInverted(true);//倒转Y轴，关键所在
        leftYAxis.setDrawGridLines(true);
        leftYAxis.setAxisLineWidth(1f);
        leftYAxis.enableGridDashedLine(10f, 10f, 0f);
        leftYAxis.setAxisLineColor(Color.GRAY);
        leftYAxis.setYOffset(0f);
        leftYAxis.setAxisMinimum(-10f);     //最小值-10
        leftYAxis.setAxisMaximum(120f);     //最大值100
        leftYAxis.setValueFormatter(new MyYFormatter());
//        limitLine.enableDashedLine(6f,20f,10f);
//        leftYAxis.addLimitLine(limitLine);
        LimitLine yLimitLine = new LimitLine(25f,"警戒线");
        yLimitLine.setLineColor(Color.RED);
        yLimitLine.setTextColor(Color.RED);
        // 获得左侧侧坐标轴
        leftYAxis.addLimitLine(yLimitLine);

        YAxis rightYAxis = lineChart.getAxisRight();//禁用左侧的Y轴
        rightYAxis.setEnabled(false);

        /** 折线图例 标签 设置*/
//        legend = lineChart.getLegend();                     //设置显示类型，LINE CIRCLE SQUARE EMPTY 等等 多种方式，查看LegendForm 即可
//        legend.setForm(Legend.LegendForm.LINE);
//        legend.setTextSize(12f);
//        //显示位置 左下方
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        //是否绘制在图表里面
//        legend.setDrawInside(false);
//        //是否显示
//        legend.setEnabled(false);
    }

    /**
     * 曲线初始化设置 一个LineDataSet 代表一条曲线
     * @param lineDataSet 线条
     * @param color       线条颜色
     * @param mode
     */
    private void initLineDataSet(LineDataSet lineDataSet, int color, LineDataSet.Mode mode) {
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return lineChart.getAxisLeft().getAxisMinimum();
            }
        });

        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawValues(true);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(8f);
        //设置折线图填充
        lineDataSet.setDrawFilled(false);
//        lineDataSet.setFormLineWidth(2f);
//        lineDataSet.setFormSize(10f);
        if (mode == null) {
            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }
    }


    public void ShowLineChart(List<Entry> entries, String name, int color){
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.LINEAR);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        //刷新数据
        lineChart.invalidate();
    }

    /**
     * 设置描述信息
     *
     * @param str
     */
    public void setDescription(String str) {
        Description description = new Description();
        description.setText(str);
        lineChart.setDescription(description);
        lineChart.invalidate();
    }


    /**
     * 添加折线
     */
    public void AddLine(List<Entry> entries, String name, int color) {
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.LINEAR);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.invalidate();
    }

    /**
     * 重置某条曲线 position 从 0 开始!! 传入Entries值,第几条线
     */

    public void ResetLine(int position, List<Entry> entries, String name, int color) {
        //代表视图中所有的线
        LineData lineData = lineChart.getData();
        //list是折线的集合
        List<ILineDataSet> list = lineData.getDataSets();
        if (list.size() <= position) {
            return;
        }

        LineDataSet lineDataSet = new LineDataSet(entries, name);
        lineDataSet.notifyDataSetChanged();
        //设置线的样式    initLineDataSet 方法初始化线
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.LINEAR);
        //换掉哪条线

        lineData.getDataSets().set(position, lineDataSet);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    /**
     * 删除某一条线
     * @param position
     */
    public void HideLine(int position){
        lineChart.getLineData().getDataSets().get(position).setVisible(false);
        lineChart.invalidate();
    }

    /**
     * 设置 可以显示X Y 轴自定义值的 MarkerView
     */
//    public void setMarkerView(Context context) {
//        LineChartMarkView mv = new LineChartMarkView(context, xAxis.getValueFormatter());
//        mv.setChartView(lineChart);
//        lineChart.setMarker(mv);
//        lineChart.invalidate();
//    }


}
