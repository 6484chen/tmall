package com.tyut.tmall.comparator;

import com.tyut.tmall.pojo.Product;

import java.util.Comparator;

/**
 * @ClassName ProductDateComparator
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/23 16:15
 * @Version 1.0
 */
public class ProductDateComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        return o1.getCreateDate().compareTo(o2.getCreateDate());
    }
}
