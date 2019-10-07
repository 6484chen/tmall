package com.tyut.tmall.interceptor;

import com.tyut.tmall.pojo.Category;
import com.tyut.tmall.pojo.OrderItem;
import com.tyut.tmall.pojo.User;
import com.tyut.tmall.service.CategoryService;
import com.tyut.tmall.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @ClassName OtherInterceptor
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/24 10:56
 * @Version 1.0
 */
public class OtherInterceptor implements HandlerInterceptor {
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    CategoryService categoryService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        HttpSession session = httpServletRequest.getSession();
        User user = (User) session.getAttribute("user");
        int cartTotalItemNumber = 0;
        if(null != user){
            List<OrderItem> orderItems =orderItemService.listByUser(user);
            for (OrderItem orderItem : orderItems) {
                cartTotalItemNumber +=orderItem.getNumber();
            }
        }
        List<Category> categories = categoryService.list();
        String contextPath = httpServletRequest.getServletContext().getContextPath();   //  /huaditmall

        httpServletRequest.getServletContext().setAttribute("categories_below_search",categories);  //搜索框下面的分类
        httpServletRequest.getServletContext().setAttribute("contextPath",contextPath);  //top的首页图标跳转首页
        session.setAttribute("cartTotalItemNumber",cartTotalItemNumber);  //购物车总件数
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
