package com.seeingvoice.www.svhearing.beans;

/**
 * Date:2019/8/12
 * Time:16:08
 * auther:zyy
 */
public class TelRegisterBean {

    /**
     * data : {"user_info":{"uid":71,"user_age":26,"user_city":null,"user_country":null,"user_headimgurl":null,"user_is_verify_tel":1,"user_name":"test_2","user_openid":null,"user_province":null,"user_sex":1,"user_tel":"18406554525"}}
     * error_code :
     * error_info :
     * message_code : A000000
     */

    private DataBean data;
    private String error_code;
    private String error_info;
    private String message_code;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getError_info() {
        return error_info;
    }

    public void setError_info(String error_info) {
        this.error_info = error_info;
    }

    public String getMessage_code() {
        return message_code;
    }

    public void setMessage_code(String message_code) {
        this.message_code = message_code;
    }

    public static class DataBean {
        /**
         * user_info : {"uid":71,"user_age":26,"user_city":null,"user_country":null,"user_headimgurl":null,"user_is_verify_tel":1,"user_name":"test_2","user_openid":null,"user_province":null,"user_sex":1,"user_tel":"18406554525"}
         */

        private UserInfoBean user_info;

        public UserInfoBean getUser_info() {
            return user_info;
        }

        public void setUser_info(UserInfoBean user_info) {
            this.user_info = user_info;
        }

        public static class UserInfoBean {
            /**
             * uid : 71
             * user_age : 26
             * user_city : null
             * user_country : null
             * user_headimgurl : null
             * user_is_verify_tel : 1
             * user_name : test_2
             * user_openid : null
             * user_province : null
             * user_sex : 1
             * user_tel : 18406554525
             */

            private int uid;
            private int user_age;
            private Object user_city;
            private Object user_country;
            private Object user_headimgurl;
            private int user_is_verify_tel;
            private String user_name;
            private Object user_openid;
            private Object user_province;
            private int user_sex;
            private String user_tel;

            public int getUid() {
                return uid;
            }

            public void setUid(int uid) {
                this.uid = uid;
            }

            public int getUser_age() {
                return user_age;
            }

            public void setUser_age(int user_age) {
                this.user_age = user_age;
            }

            public Object getUser_city() {
                return user_city;
            }

            public void setUser_city(Object user_city) {
                this.user_city = user_city;
            }

            public Object getUser_country() {
                return user_country;
            }

            public void setUser_country(Object user_country) {
                this.user_country = user_country;
            }

            public Object getUser_headimgurl() {
                return user_headimgurl;
            }

            public void setUser_headimgurl(Object user_headimgurl) {
                this.user_headimgurl = user_headimgurl;
            }

            public int getUser_is_verify_tel() {
                return user_is_verify_tel;
            }

            public void setUser_is_verify_tel(int user_is_verify_tel) {
                this.user_is_verify_tel = user_is_verify_tel;
            }

            public String getUser_name() {
                return user_name;
            }

            public void setUser_name(String user_name) {
                this.user_name = user_name;
            }

            public Object getUser_openid() {
                return user_openid;
            }

            public void setUser_openid(Object user_openid) {
                this.user_openid = user_openid;
            }

            public Object getUser_province() {
                return user_province;
            }

            public void setUser_province(Object user_province) {
                this.user_province = user_province;
            }

            public int getUser_sex() {
                return user_sex;
            }

            public void setUser_sex(int user_sex) {
                this.user_sex = user_sex;
            }

            public String getUser_tel() {
                return user_tel;
            }

            public void setUser_tel(String user_tel) {
                this.user_tel = user_tel;
            }
        }
    }
}
