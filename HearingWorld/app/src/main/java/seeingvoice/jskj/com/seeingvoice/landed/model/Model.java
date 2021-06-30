package seeingvoice.jskj.com.seeingvoice.landed.model;

/**
 * Date:2019/4/29
 * Time:15:04
 * auther:zyy
 */
public class Model implements IModel {

    /**
     * 实现IModel接口，负责实际的数据获取操作（数据库读取，网络加载等），然后通过自己的接口（LoadDataCallback）反馈出去
     */
    @Override
    public void getData(final LoadDataCallback callback) {
        //数据获取操作，如数据库查询、网络加载等
        new Thread(){
            @Override
            public void run() {
                try {
                    //模拟耗时操作
                    Thread.sleep(3000);
                    //获取到了数据
                    String data = "我是获取到的数据";
                    //将获取的数据通过接口反馈出去
                    callback.success(data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //获取数据失败的回调
                    callback.failure();
                }
            }
        }.start();
    }

    /**
     *
     * 用于回传请求的数据的回传
     */
    public interface LoadDataCallback {
        void success(String taskId);
        void failure();
    }
}
