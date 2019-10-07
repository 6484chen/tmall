package com.tyut.tmall.dao;

import com.tyut.tmall.pojo.Product;
import com.tyut.tmall.pojo.Property;
import com.tyut.tmall.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyValueDao extends JpaRepository<PropertyValue,Integer> {
    List<PropertyValue> findByProductOrderByIdDesc(Product product);
    PropertyValue getByPropertyAndProduct(Property property,Product product);
}
