package com.seeingvoice.www.svhearing.database.entity;

import com.seeingvoice.www.svhearing.database.StringConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Date:2019/6/3
 * Time:11:26
 * auther:zyy
 */
@Entity
public class PureTestResult {
    /** (autoincrement = true)表示主键会自增，如果false就会使用旧值 。*/
    @Id(autoincrement = true)
    Long ID;
    String timeStamp;
    String IMEI;
    @Convert(columnType = String.class,converter = StringConverter.class)
    List<String> mLeftResult;
    @Convert(columnType = String.class,converter = StringConverter.class)
    List<String> mRightResult;
    Long USER_ID;
    String remark;
    @Generated(hash = 1536372458)
    public PureTestResult(Long ID, String timeStamp, String IMEI,
            List<String> mLeftResult, List<String> mRightResult, Long USER_ID,
            String remark) {
        this.ID = ID;
        this.timeStamp = timeStamp;
        this.IMEI = IMEI;
        this.mLeftResult = mLeftResult;
        this.mRightResult = mRightResult;
        this.USER_ID = USER_ID;
        this.remark = remark;
    }
    @Generated(hash = 1365343512)
    public PureTestResult() {
    }
    public Long getID() {
        return this.ID;
    }
    public void setID(Long ID) {
        this.ID = ID;
    }
    public String getTimeStamp() {
        return this.timeStamp;
    }
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
    public String getIMEI() {
        return this.IMEI;
    }
    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }
    public List<String> getMLeftResult() {
        return this.mLeftResult;
    }
    public void setMLeftResult(List<String> mLeftResult) {
        this.mLeftResult = mLeftResult;
    }
    public List<String> getMRightResult() {
        return this.mRightResult;
    }
    public void setMRightResult(List<String> mRightResult) {
        this.mRightResult = mRightResult;
    }
    public Long getUSER_ID() {
        return this.USER_ID;
    }
    public void setUSER_ID(Long USER_ID) {
        this.USER_ID = USER_ID;
    }
    public String getRemark() {
        return this.remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
