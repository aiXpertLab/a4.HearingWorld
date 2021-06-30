package seeingvoice.jskj.com.seeingvoice.javabeans;

import seeingvoice.jskj.com.seeingvoice.javabeans.userDao.page;
import seeingvoice.jskj.com.seeingvoice.javabeans.userDao.userInfo;

import java.util.List;

/**
 * Date:2019/6/11
 * Time:13:18
 * auther:zyy
 */
public class users {

    private page mPage;
    private List<userInfo> userList;

    public users() {
    }

    public users(page mPage, List<userInfo> userList) {
        this.mPage = mPage;
        this.userList = userList;
    }

    public page getmPage() {
        return mPage;
    }

    public void setmPage(page mPage) {
        this.mPage = mPage;
    }

    public List<userInfo> getUserList() {
        return userList;
    }

    public void setUserList(List<userInfo> userList) {
        this.userList = userList;
    }

    @Override
    public String toString() {
        return "page:"+mPage+",List<userInfo>"+userList;
    }


}
