package seeingvoice.jskj.com.seeingvoice.beans;

/**
 * Date:2019/6/21
 * Time:14:20
 * auther:zyy
 */
public class UserEntity {
    private String UserID;//用户ID
    private String UserName;//用户名称
    private String UserPassword;//用户账户密码
    private String UserCity;//用户所在城市
    private String UserImg;//头像图片
    private String UserPhone;//手机号码
    private String UserWxID;//微信ID
    private String MerchantCaId;//支付卡ID

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String userPassword) {
        UserPassword = userPassword;
    }

    public String getUserCity() {
        return UserCity;
    }

    public void setUserCity(String userCity) {
        UserCity = userCity;
    }

    public String getUserImg() {
        return UserImg;
    }

    public void setUserImg(String userImg) {
        UserImg = userImg;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getUserWxID() {
        return UserWxID;
    }

    public void setUserWxID(String userWxID) {
        UserWxID = userWxID;
    }

    public String getMerchantCaId() {
        return MerchantCaId;
    }

    public void setMerchantCaId(String merchantCaId) {
        MerchantCaId = merchantCaId;
    }
}
