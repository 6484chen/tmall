package com.tyut.tmall.comparator;

import com.tyut.tmall.pojo.Product;

import java.util.Comparator;

/**
 * @ClassName ProductPriceComparator
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/23 16:17
 * @Version 1.0
 */
public class ProductPriceComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        return (int) (o2.getPromotePrice() - o1.getPromotePrice());
    }
}
