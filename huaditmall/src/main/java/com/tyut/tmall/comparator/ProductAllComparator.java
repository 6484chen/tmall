package com.tyut.tmall.comparator;

import com.tyut.tmall.pojo.Product;

import java.util.Comparator;

/**
 * @ClassName ProductAllComparator
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/23 16:11
 * @Version 1.0
 */
public class ProductAllComparator implements Comparator<Product> {


    @Override
    public int compare(Product o1, Product o2) {
        return o2.getReviewCount()*o2.getSaleCount() - o1.getReviewCount()*o1.getSaleCount();
    }
}
