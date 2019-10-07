package com.tyut.tmall.service;

import com.tyut.tmall.dao.ProductImageDao;
import com.tyut.tmall.pojo.OrderItem;
import com.tyut.tmall.pojo.Product;
import com.tyut.tmall.pojo.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.xml.ws.soap.Addressing;
import java.util.List;

/**
 * @ClassName ProductImageService
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/21 17:25
 * @Version 1.0
 */
@Service
@CacheConfig(cacheNames = "productImages")
public class ProductImageService {
    //分别表示单个图片和详情图片
    public static final String type_single = "single";
    public static final String type_detail = "detail";

    @Autowired
    ProductImageDao productImageDao;
    @Autowired
    ProductService productService;

    @CacheEvict(allEntries=true)
    public void add(ProductImage bean){
        productImageDao.save(bean);
    }
    @CacheEvict(allEntries=true)
    public void delete(int id){
        productImageDao.delete(id);
    }
    @Cacheable(key = "'productImages-one-'+#p0")
    public ProductImage get(int id){
        return productImageDao.findOne(id);
    }
    //还提供了根据产品id和图片类型查询的list方法
    @Cacheable(key="'productImages-single-pid-'+ #p0.id")
    public List<ProductImage> listSingleProductImages(Product product){
        return productImageDao.findByProductAndTypeOrderByIdDesc(product,type_single);
    }
    @Cacheable(key="'productImages-detail-pid-'+ #p0.id")
    public List<ProductImage> listDetailProductImages(Product product){
        return productImageDao.findByProductAndTypeOrderByIdDesc(product,type_detail);
    }

    public void setFirstProductImage(Product product){
        List<ProductImage> singleImages = listSingleProductImages(product);
        if(!singleImages.isEmpty()){
            product.setFirstProductImage(singleImages.get(0));
        }else {
            product.setFirstProductImage(new ProductImage()); //这样做是考虑到产品还没有来得及设置图片，
                                                                // 但是在订单后台管理里查看订单项的对应产品图片。
        }

    }

    public void setFirstProductImages(List<Product> products){
        for (Product product : products) {
            setFirstProductImage(product);
        }
    }

    //结算页面
    public void setFirstProductImagesOnOrderItems(List<OrderItem> ois){
        for (OrderItem orderItem : ois) {
            setFirstProductImage(orderItem.getProduct());
        }
    }

}
