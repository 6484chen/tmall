package com.tyut.tmall.dao;

import com.tyut.tmall.pojo.Category;
import com.tyut.tmall.pojo.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyDao extends JpaRepository<Property,Integer> {

    //接口
    //比如这里的findByCategory，就是基于Category进行查询，第二个参数传一个 Pageable ， 就支持分页了。
    Page<Property> findByCategory (Category category , Pageable pageable);
    List<Property> findByCategory(Category category);
}
