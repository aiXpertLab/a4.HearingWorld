package com.seeingvoice.www.svhearing.landed.presenter;

import com.seeingvoice.www.svhearing.landed.model.Model;
import com.seeingvoice.www.svhearing.landed.view.IView;

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
