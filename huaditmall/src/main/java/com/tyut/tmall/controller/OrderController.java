package com.tyut.tmall.controller;

import com.tyut.tmall.pojo.Order;
import com.tyut.tmall.service.OrderItemService;
import com.tyut.tmall.service.OrderService;
import com.tyut.tmall.util.Page4Navigator;
import com.tyut.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.stream.events.StartDocument;
import java.util.Date;

/**
 * @ClassName OrderController
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/22 12:18
 * @Version 1.0
 */
@RestController
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;

    @GetMapping("/orders")
    public Page4Navigator<Order> list(@RequestParam(value = "start",defaultValue = "0") int start,@RequestParam(value = "size",defaultValue = "5") int size)throws Exception{
        start = start<0?0:start;
        Page4Navigator<Order> page =orderService.list(start,size,5);
        System.out.println("===========hello world==========");
        orderItemService.fill(page.getContent());
        orderService.removeOrderFromOrderItem(page.getContent());
        return page;
    }

    @PutMapping("deliveryOrder/{oid}")
    public Object deliveryOrder(@PathVariable("oid") int oid)throws  Exception{
        Order o = orderService.get(oid);
        o.setDeliveryDate(new Date());
        o.setStatus(OrderService.waitConfirm);
        orderService.update(o);
        return Result.success();
    }
}
