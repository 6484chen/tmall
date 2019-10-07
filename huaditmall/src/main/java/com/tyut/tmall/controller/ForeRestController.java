package com.tyut.tmall.controller;

import com.tyut.tmall.comparator.*;
import com.tyut.tmall.pojo.*;
import com.tyut.tmall.service.*;
import com.tyut.tmall.util.Result;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName ForeRestController
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/22 20:57
 * @Version 1.0
 */
@RestController
public class ForeRestController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderService orderService;

    @GetMapping("/forehome")
    public Object home(){
        List<Category> categorys = categoryService.list();

        productService.fill(categorys);
        productService.fillByRow(categorys);
        categoryService.removeCategoryFromProduct(categorys);
        return categorys;
    }
    //注册
    @PostMapping("/foreregister")
    public Object register(@RequestBody User user){
        String name =user.getName();
        String password = user.getPassword();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = userService.isExist(name);
        if(exist){
            String message = "用户名已经被使用，不能使用";
            return Result.fail(message);
        }
        //TODO
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;   //迭代两次
        String algorithmName = "md5";   //加密算法
        //加密后的密码
        String encodedPassword = new SimpleHash(algorithmName,password,salt,times).toString();

        user.setSalt(salt);
        user.setPassword(encodedPassword);
        userService.add(user);
        return Result.success();
    }

    //登录
    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam ,HttpSession sesson){
        String name = userParam.getName();
        name = HtmlUtils.htmlEscape(name);   //解决错误编码，弹窗类似的
        Subject subject = SecurityUtils.getSubject();  //TODO
        //token保存登录的原始账号和密码
        UsernamePasswordToken token = new UsernamePasswordToken(name,userParam.getPassword());
        /*if(null == user){
            String message = "账号密码错误";
            return Result.fail(message);
        }else {
            sesson.setAttribute("user",user);  //建立会话
            return Result.success();
        }*/
        //修改为
        try {
            subject.login(token); //TODO  搞懂subject
            User user  = userService.getByName(name);
            sesson.setAttribute("user",user);
            return Result.success();
        }catch (AuthenticationException e){
            String message = "账号密码错误";
            return Result.fail(message);
        }
    }
    //产品分类
    @GetMapping("/foreproduct/{pid}")
    public Object product(@PathVariable("pid") int pid){
        Product product = productService.get(pid);
        List<ProductImage> productSingleImages =productImageService.listSingleProductImages(product);
        List<ProductImage> productDetailImages = productImageService.listDetailProductImages(product);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);

        List<PropertyValue> pvs = propertyValueService.list(product);
        List<Review> reviews = reviewService.list(product);
        productService.setSaleAndReviewNumber(product);
        productImageService.setFirstProductImage(product);

        Map<String,Object> map = new HashMap<>();
        map.put("product",product);
        map.put("pvs",pvs);
        map.put("reviews",reviews);

        return Result.success(map);
    }

    //添加进购物车时，需要检查是否登录
    @GetMapping("forecheckLogin")
    public Object checkLogin(HttpSession session){
       /* User user = (User) session.getAttribute("user");
        if(null !=user)
            return Result.success();*/
       Subject subject = SecurityUtils.getSubject();
       if(subject.isAuthenticated())
           return  Result.success();
        return Result.fail("未登录");
    }

    //为产品分类排序
    @GetMapping("forecategory/{cid}")
    public Object category(@PathVariable("cid") int cid,String sort){
        Category category = categoryService.get(cid);
        productService.fill(category);
        productService.setSaleAndReviewNumber(category.getProducts());
        categoryService.removeCategoryFromProduct(category);

        if(null != sort){
            switch (sort){
                case "review":
                    Collections.sort(category.getProducts(),new ProductReviewComparator());
                    break;
                case "price":
                    Collections.sort(category.getProducts(),new ProductPriceComparator());
                    break;
                case "date":
                    Collections.sort(category.getProducts(),new ProductDateComparator());
                    break;
                case "saleCount":
                    Collections.sort(category.getProducts(),new ProductSaleCountComparator());
                    break;
                case "all":
                    Collections.sort(category.getProducts(),new ProductAllComparator());
                    break;

            }
        }

        return category;
    }
    //搜索
    @PostMapping("/foresearch")
    public Object search(String keyword){
        if(null == keyword){
            keyword = "";
        }
        List<Product> ps = productService.search(keyword,0,20);
        productImageService.setFirstProductImages(ps);
        productService.setSaleAndReviewNumber(ps);
        return ps;
    }

    //立即购买
    @GetMapping("forebuyone")
    public Object buyone(int pid,int num,HttpSession session){
        return buyoneAndAddCart(pid,num,session);
    }

    private int buyoneAndAddCart(int pid,int num,HttpSession session){

        Product product = productService.get(pid);
        int oiid = 0;
        User user = (User) session.getAttribute("user");
        boolean found = false;
        //如果有相同的订单，则进行数量相加
        List<OrderItem> orderItems = orderItemService.listByUser(user);
        for (OrderItem orderItem : orderItems) {
            if(orderItem.getProduct().getId() == product.getId()){
                orderItem.setNumber(orderItem.getNumber()+num);
                orderItemService.update(orderItem);
                found =true;
                oiid = orderItem.getId();
                break;
            }
        }
        //如果没有订单项，则生成新的订单
        if(!found){
            OrderItem orderItem = new OrderItem();
            orderItem.setUser(user);
            orderItem.setNumber(num);
            orderItem.setProduct(product);
            orderItemService.add(orderItem);
            oiid = orderItem.getId();
        }
        return  oiid;
    }

    //立即购买
    @GetMapping("forebuy")
    public Object buy(String[] oiid,HttpSession session){
        List<OrderItem> orderItems = new ArrayList<>();
        float total =0;
        for(String strid : oiid){
            int id =Integer.parseInt(strid);
            OrderItem orderItem = orderItemService.get(id);
            total += orderItem.getProduct().getPromotePrice()*orderItem.getNumber();
            orderItems.add(orderItem);
        }

        productImageService.setFirstProductImagesOnOrderItems(orderItems);

        session.setAttribute("ois",orderItems);
        Map<String,Object> map = new HashMap<>();
        map.put("orderItems",orderItems);
        map.put("total",total);
        return Result.success(map);
    }
    //加入购物车
    @GetMapping("foreaddCart")
    public Object addCart(int pid,int num,HttpSession session)throws Exception{
        int i = buyoneAndAddCart(pid,num,session);
        return Result.success();
    }
    //查看购物车
    @GetMapping("forecart")
    public Object cart(HttpSession session) {
        User user =(User)  session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user);
        productImageService.setFirstProductImagesOnOrderItems(ois);
        return ois;
    }
    //改变购物车订单项
    @GetMapping("forechangeOrderItem")
    public  Object changeOrderItem (HttpSession session,int pid,int num){
        User user = (User) session.getAttribute("user");
        if(null ==user){
            return Result.fail("未登录");
        }

        List<OrderItem> orderItems = orderItemService.listByUser(user);
        for (OrderItem orderItem : orderItems) {
            if(orderItem.getProduct().getId() == pid){
                orderItem.setNumber(num);
                orderItemService.update(orderItem);
                break;
            }
        }
        return  Result.success();
    }
    //删除购物车订单项
    @GetMapping("foredeleteOrderItem")
    public Object deleteOrderItem(HttpSession session,int oiid){
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        orderItemService.delete(oiid);
        return Result.success();
    }

    //生成订单
    @PostMapping("forecreateOrder")
    public Object createOrder(@RequestBody Order order,HttpSession session){
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        //生成订单号
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())+ RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setStatus(OrderService.waitPay);
        order.setUser(user);
        List<OrderItem> orderItems = (List<OrderItem>) session.getAttribute("ois");  //获取数据库中的订单项

        float total = orderService.add(order,orderItems); //更新订单项，和订单的状态
        Map<String,Object> map = new HashMap<>();
        map.put("oid",order.getId());
        map.put("total",total);

        return Result.success(map);
    }

    //支付成功
    @GetMapping("forepayed")
    public Object payed(int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return order;
    }

    //查看订单
    @GetMapping("forebought")
    public Object bought(HttpSession session){
        User user = (User) session.getAttribute("user");
        if(null == user)
            return Result.fail("未登录");
        List<Order> orders = orderService.listByUserWithoutDelete(user);
        orderService.removeOrderFromOrderItem(orders);
        return orders;
    }

    //确认付款
    @GetMapping("foreconfirmPay")
    public Object confirmPay(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.cacl(o);
        orderService.removeOrderFromOrderItem(o);
        return o;
    }

    //确认收货后，等待评价
    @GetMapping("foreorderConfirmed")
    public Object orderConfirmed( int oid) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.waitReview);
        o.setConfirmDate(new Date());
        orderService.update(o);
        return Result.success();
    }

    //删除订单
    @PutMapping("foredeleteOrder")
    public Object deleteOrder(int oid){
        Order o = orderService.get(oid);
        o.setStatus(OrderService.delete);
        orderService.update(o);
        return Result.success();
    }

    //评价商品
    @GetMapping("forereview")
    public Object review(int oid){
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        orderService.removeOrderFromOrderItem(order);
        //TODO：这里只对第一个订单进行评价，待改进
        Product p = order.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(p);
        productService.setSaleAndReviewNumber(p);
        Map<String,Object> map = new HashMap<>();
        map.put("p",p);
        map.put("o",order);
        map.put("reviews",reviews);
        return Result.success(map);
    }

    @GetMapping("foredoreview")
    public Object doreview(HttpSession session,int oid,int pid,String content){
        Order order = orderService.get(oid);
        order.setStatus(OrderService.finish);
        orderService.update(order);

        Product p = productService.get(pid);
        content = HtmlUtils.htmlEscape(content);

        User user = (User) session.getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setProduct(p);
        review.setCreateDate(new Date());
        review.setUser(user);
        reviewService.add(review);
        return Result.success();
    }
}
