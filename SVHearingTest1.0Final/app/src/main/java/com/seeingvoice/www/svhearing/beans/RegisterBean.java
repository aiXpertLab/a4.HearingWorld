package com.seeingvoice.www.svhearing.beans;

/**
 * Date:2019/7/29
 * Time:11:31
 * auther:zyy
 */
public class RegisterBean {

    /**
     * message_code : A000000
     * user_info : {"is_verify_tel":0,"login_type":"2","uid":56,"user_headimgurl":null,"user_name":"lll"}
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
         * is_verify_tel : 0
         * login_type : 2
         * uid : 56
         * user_headimgurl : null
         * user_name : lll
         */

        private int is_verify_tel;
        private String login_type;
        private int uid;
        private Object user_headimgurl;
        private String user_name;

        public int getIs_verify_tel() {
            return is_verify_tel;
        }

        public void setIs_verify_tel(int is_verify_tel) {
            this.is_verify_tel = is_verify_tel;
        }

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

        public Object getUser_headimgurl() {
            return user_headimgurl;
        }

        public void setUser_headimgurl(Object user_headimgurl) {
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
