package com.tyut.tmall.dao;

import com.tyut.tmall.pojo.Order;
import com.tyut.tmall.pojo.OrderItem;
import com.tyut.tmall.pojo.Product;
import com.tyut.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemDao extends JpaRepository<OrderItem,Integer> {
    List<OrderItem> findByOrderOrderByIdDesc(Order order);
    //根据产品获取产品集合
    List<OrderItem> findByProduct(Product product);
    //
    List<OrderItem> findByUserAndOrderIsNull(User user);
}
