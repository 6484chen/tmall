package com.tyut.tmall.dao;

import com.tyut.tmall.pojo.Order;
import com.tyut.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDao extends JpaRepository<Order,Integer> {
    public List<Order> findByUserAndStatusNotOrderByIdDesc(User user, String status);
}
