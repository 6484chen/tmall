package com.tyut.tmall.dao;

import com.tyut.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.criteria.CriteriaBuilder;

public interface UserDao extends JpaRepository<User, Integer> {
    //注册检查
    User findByName(String name);
    //登录
    User getByNameAndPassword(String name,String password);
}
