package com.tyut.tmall.pojo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

/**
 * @ClassName User
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/22 10:15
 * @Version 1.0
 */
@Entity
@Table(name = "user")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    private String name;
    private String password;
    private String salt;   //盐，为密码进行加密
    @Transient
    private String anonymousName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
    //其中anonymousName没有和数据库关联，用于获取匿名，其实就是前后保留，中间换成星星，如果长度只有2或者1，单独处理一下
    public String getAnonymousName() {
        if(null != anonymousName)
            return anonymousName;
        if (null == name)
            anonymousName = null;
        else if (name.length() <=1)
            anonymousName="*";
        else if (name.length() ==2)
            anonymousName = name.substring(0,1)+"*";
        else {
            char[] cs = name.toCharArray();
            for (int i = 1; i < cs.length-1; i++) {
                cs[i] = '*';
            }
            anonymousName = new String(cs);
        }
        return anonymousName;
    }

    public void setAnonymousName(String anonymousName) {
        this.anonymousName = anonymousName;
    }
}
