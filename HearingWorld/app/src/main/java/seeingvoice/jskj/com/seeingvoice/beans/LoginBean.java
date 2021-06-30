package seeingvoice.jskj.com.seeingvoice.beans;

/**
 * Date:2019/7/29
 * Time:13:36
 * auther:zyy
 */
public class LoginBean {

    /**
     * message_code : A100101
     * user_info : {"login_type":null,"uid":48,"user_headimgurl":null,"user_name":"test"}
     */

    private String message_code;
    private UserInfoBean user_info;

    public String getMessage_code() {
        return message_code;
    }

    public void setMessage_code(String message_code) {
        this.message_code = message_code;
    }

    public UserInfoBean getUser_info() {
        return user_info;
    }

    public void setUser_info(UserInfoBean user_info) {
        this.user_info = user_info;
    }

    public static class UserInfoBean {
        /**
         * login_type : null
         * uid : 48
         * user_headimgurl : null
         * user_name : test
         */

        private String login_type;
        private int uid;
        private String user_headimgurl;
        private String user_name;

        public String getLogin_type() {
            return login_type;
        }

        public void setLogin_type(String login_type) {
            this.login_type = login_type;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getUser_headimgurl() {
            return user_headimgurl;
        }

        public void setUser_headimgurl(String user_headimgurl) {
            this.user_headimgurl = user_headimgurl;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }
    }
}
