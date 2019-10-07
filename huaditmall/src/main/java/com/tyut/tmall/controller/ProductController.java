package com.tyut.tmall.controller;

import com.tyut.tmall.pojo.Product;
import com.tyut.tmall.pojo.ProductImage;
import com.tyut.tmall.service.CategoryService;
import com.tyut.tmall.service.ProductImageService;
import com.tyut.tmall.service.ProductService;
import com.tyut.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @ClassName ProductService
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/21 16:18
 * @Version 1.0
 */
@RestController
public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;

    @GetMapping("categories/{cid}/products")
    Page4Navigator<Product> list(@PathVariable("cid") int cid, @RequestParam(value = "start",defaultValue = "0") int start,
                                 @RequestParam(value = "size",defaultValue = "5") int size)throws  Exception{
        start = start<0?0:start;
        Page4Navigator<Product> page = productService.list(cid,start,size,5);
        productImageService.setFirstProductImages(page.getContent());
        return page;
    }

    @DeleteMapping("/products/{id}")
    public String delete(@PathVariable("id") int id ,HttpServletRequest request ) throws Exception{
        productService.delete(id);
        return null;
    }
    @PostMapping("/products")
    public Object add(@RequestBody Product product) throws Exception{
        product.setCreateDate(new Date());
        productService.add(product);
        return product;
    }
    @PutMapping("/products")
    public Object update(@RequestBody Product product) throws  Exception{
        productService.update(product);
        return product;
    }
    @GetMapping("/products/{id}")
    public Product get(@PathVariable("id") int id) throws  Exception{
       Product product = productService.get(id);
       return  product;
    }

}
