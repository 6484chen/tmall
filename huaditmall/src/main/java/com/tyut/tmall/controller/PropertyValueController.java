package com.tyut.tmall.controller;

import com.tyut.tmall.pojo.Product;
import com.tyut.tmall.pojo.Property;
import com.tyut.tmall.pojo.PropertyValue;
import com.tyut.tmall.service.ProductService;
import com.tyut.tmall.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName PropertyValueController
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/21 21:46
 * @Version 1.0
 */
@RestController
public class PropertyValueController {
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ProductService productService;

    @GetMapping("products/{pid}/propertyValues")
    public List<PropertyValue> list(@PathVariable("pid") int pid) throws Exception{
        Product product = productService.get(pid);
        propertyValueService.init(product);
        List<PropertyValue> propertyValues = propertyValueService.list(product);
        return propertyValues;
    }

    @PutMapping("/propertyValues")
    public Object update(@RequestBody PropertyValue propertyValue) throws  Exception{
        propertyValueService.update(propertyValue);
        return propertyValue;
    }
}
