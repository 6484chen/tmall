package com.tyut.tmall.dao;

import com.tyut.tmall.pojo.Product;
import com.tyut.tmall.pojo.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewDao extends JpaRepository<Review,Integer> {
    //返回某产品对应的评价集合
    List<Review> findByProductOrderByIdDesc(Product product);
    //返回某产品对应的评价数量
    int countByProduct(Product product);
}
