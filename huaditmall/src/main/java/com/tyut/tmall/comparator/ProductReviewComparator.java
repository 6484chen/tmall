package com.tyut.tmall.comparator;

import com.tyut.tmall.pojo.Product;

import java.util.Comparator;

/**
 * @ClassName ProductReviewComparator
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/23 16:18
 * @Version 1.0
 */
public class ProductReviewComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        return o2.getReviewCount() - o1.getReviewCount();
    }
}
