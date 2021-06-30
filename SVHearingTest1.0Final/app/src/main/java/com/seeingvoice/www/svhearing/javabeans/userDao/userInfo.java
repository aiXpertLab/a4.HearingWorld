package com.seeingvoice.www.svhearing.javabeans.userDao;

/**
 * Date:2019/6/11
 * Time:13:59
 * auther:zyy
 */
public class userInfo {
    private String id;
    private String email;
    private String passwd;
    private int admin;
    private String name;
    private String image;
    private String created_at;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "userInfo [id=" + id + ", passwd=" + passwd + ", admin"+admin+",name=" + name + ",image"+image+",created_at=" + created_at + "]\n\n";
    }
}
