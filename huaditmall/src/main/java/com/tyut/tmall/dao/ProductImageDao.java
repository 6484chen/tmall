package com.tyut.tmall.dao;

import com.tyut.tmall.pojo.Product;
import com.tyut.tmall.pojo.ProductImage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageDao extends JpaRepository<ProductImage,Integer> {
    public List<ProductImage> findByProductAndTypeOrderByIdDesc(Product product, String type);
}
