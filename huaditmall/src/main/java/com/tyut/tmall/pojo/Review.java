package com.tyut.tmall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

/**
 * @ClassName Review
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/23 9:29
 * @Version 1.0
 */
@Entity
@Table(name = "review")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;

    @ManyToOne
    @JoinColumn(name="pid")
    private Product product;

    private String content;
    private Date createDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
