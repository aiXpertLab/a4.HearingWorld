package seeingvoice.jskj.com.seeingvoice.landed.presenter;

import seeingvoice.jskj.com.seeingvoice.landed.model.Model;
import seeingvoice.jskj.com.seeingvoice.landed.view.IView;

/**
 * Date:2019/4/29
 * Time:15:50
 * auther:zyy
 */
public class Presenter implements IPresenter, Model.LoadDataCallback {

    private final IView mView;
    private final Model mModel;

    public Presenter(IView view) {
        mView = view;
        mModel = new Model();
    }

    @Override
    public void loadData() {
        mView.showLoadingProgress("加载数据中...");
        mModel.getData(Presenter.this);
    }

    @Override
    public void success(String data) {
        mView.showData(data);
    }

    @Override
    public void failure() {
        mView.showData("加载失败");
    }
}
