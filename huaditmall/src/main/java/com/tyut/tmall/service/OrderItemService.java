package com.tyut.tmall.service;

import com.tyut.tmall.dao.OrderItemDao;
import com.tyut.tmall.pojo.Order;
import com.tyut.tmall.pojo.OrderItem;
import com.tyut.tmall.pojo.Product;
import com.tyut.tmall.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import java.util.List;

/**
 * @ClassName OrderItemService
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/22 11:26
 * @Version 1.0
 */
@Service
@CacheConfig(cacheNames = "orderItems")
public class OrderItemService {
    @Autowired
    OrderItemDao orderItemDao;
    @Autowired
    ProductImageService productImageService;

    @Cacheable(key = "'orderItems-oid-'+ #p0.id")
    public List<OrderItem> listByOrder(Order order){
        return orderItemDao.findByOrderOrderByIdDesc(order);
    }

    public void fill(Order order){
        List<OrderItem> orderItems = listByOrder(order);
        float total = 0;
        int totalNumber = 0;
        for (OrderItem orderItem : orderItems) {
            total += orderItem.getNumber()*orderItem.getProduct().getPromotePrice();
            totalNumber += orderItem.getNumber();
            productImageService.setFirstProductImage(orderItem.getProduct());
        }
        order.setTotal(total);
        order.setTotalNumber(totalNumber);
        order.setOrderItems(orderItems);
    }

    public void fill(List<Order> orders){
        for (Order order : orders) {
            fill(order);
        }
    }

    //fore:
    @Cacheable(key="'orderItems-pid-'+ #p0.id")
    public List<OrderItem> listByProduct(Product product){
        return orderItemDao.findByProduct(product);
    }
    //统计评价数
    public int getSaleCount(Product product){
        List<OrderItem> ois = listByProduct(product);
        int result =0;
        for (OrderItem orderItem : ois) {
            if(null!=orderItem.getOrder()){
                if(null!=orderItem.getOrder() && null!=orderItem.getOrder().getPayDate())
                    result += orderItem.getNumber();
            }
        }
        return result;

    }

    @Cacheable(key="'orderItems-uid-'+ #p0.id")
    public  List<OrderItem> listByUser(User user){
        return orderItemDao.findByUserAndOrderIsNull(user);
    }

    @CacheEvict(allEntries=true)
    public void add(OrderItem orderItem){
        orderItemDao.save(orderItem);
    }
    @CacheEvict(allEntries=true)
    public  void update(OrderItem orderItem){
        orderItemDao.save(orderItem);
    }

    @Cacheable(key="'orderItems-one-'+ #p0")
    public OrderItem get(int id){
        return orderItemDao.findOne(id);
    }
    @CacheEvict(allEntries=true)
    public void delete(int id){
        orderItemDao.delete(id);
    }

}
