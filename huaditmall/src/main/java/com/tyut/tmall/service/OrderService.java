package com.tyut.tmall.service;

import com.tyut.tmall.dao.OrderDao;
import com.tyut.tmall.pojo.Order;
import com.tyut.tmall.pojo.OrderItem;
import com.tyut.tmall.pojo.User;
import com.tyut.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.beans.Transient;
import java.util.List;

/**
 * @ClassName OrderService
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/22 11:39
 * @Version 1.0
 */
@Service
@CacheConfig(cacheNames = "orders")
public class OrderService {
    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";

    @Autowired
    OrderDao orderDao;
    @Autowired
    OrderItemService orderItemService;
    @Cacheable(key="'orders-page-'+#p0+ '-' + #p1")
    public Page4Navigator<Order> list(int start, int size, int navigatePages){
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(start,size,sort);
        Page pageFormJPA = orderDao.findAll(pageable);
        return new Page4Navigator<>(pageFormJPA,navigatePages);
    }

    public void removeOrderFromOrderItem(List<Order> orders){
        for (Order order : orders) {
            removeOrderFromOrderItem(order);
        }
    }
    //避免与Order中的OrderItems的属性造成死循环
    public void removeOrderFromOrderItem(Order order){
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(null);
        }
    }
    @Cacheable(key="'orders-one-'+ #p0")
    public Order get(int id){
        return orderDao.findOne(id);
    }
    @CacheEvict(allEntries=true)
    public void update(Order order){
        orderDao.save(order);
    }


    @CacheEvict(allEntries=true)
    public void add(Order order){
        orderDao.save(order);
    }
    //添加订单
    @CacheEvict(allEntries=true)
    @Transactional(propagation = Propagation.REQUIRED,rollbackForClassName = "Exception")
    public float add(Order order,List<OrderItem> orderItems){
        float total =0;
        add(order);
        if(false){  //改为true，可以看到事务处理的错误
            throw new RuntimeException();
        }
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(order);
            orderItemService.update(orderItem);
            total += orderItem.getNumber()*orderItem.getProduct().getPromotePrice();
        }
        return  total;
    }

    //查看我的订单项
    @Cacheable(key="'orders-uid-'+ #p0.id")
    public List<Order> listByUserAndNotDelete(User user){
        return orderDao.findByUserAndStatusNotOrderByIdDesc(user,OrderService.delete);
    }

    public List<Order> listByUserWithoutDelete(User user){
        List<Order> orders =listByUserAndNotDelete(user);
        orderItemService.fill(orders);
        return orders;
    }

    //计算订单总金额
    public void cacl(Order order){
        List<OrderItem> orderItems = order.getOrderItems();
        float total =0;
        for (OrderItem orderItem : orderItems) {
            total += orderItem.getNumber()*orderItem.getProduct().getPromotePrice();
        }
        order.setTotal(total);
    }

}

