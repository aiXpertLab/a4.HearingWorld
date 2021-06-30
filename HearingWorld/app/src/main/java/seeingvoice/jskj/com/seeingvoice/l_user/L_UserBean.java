package seeingvoice.jskj.com.seeingvoice.l_user;
/**
 * Created by LeoReny@hypech.com
 * 2021/01/19
 */
public class L_UserBean {
    private String name;
    private String password;
    private String email;
    private String phonenum;

    public L_UserBean(String name, String password, String email, String phonenum) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.phonenum = phonenum;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phonenum='" + phonenum + '\'' +
                '}';
    }
}


