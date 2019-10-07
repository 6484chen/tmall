package com.tyut.tmall.dao;

import com.tyut.tmall.pojo.Category;
import com.tyut.tmall.pojo.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDao extends JpaRepository<Product,Integer> {
    Page<Product> findByCategory(Category category, Pageable pageable);
    //fore:通过分类查询所有产品,不需要分页
    List<Product> findByCategoryOrderById(Category category);
    List<Product> findByNameLike(String keyword,Pageable pageable);
}

