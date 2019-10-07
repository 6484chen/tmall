package com.tyut.tmall.service;

import com.tyut.tmall.dao.PropertyValueDao;
import com.tyut.tmall.pojo.Product;
import com.tyut.tmall.pojo.Property;
import com.tyut.tmall.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName PropertyValueService
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/21 21:37
 * @Version 1.0
 */
@Service
@CacheConfig(cacheNames = "propertyValues")
public class PropertyValueService {
    @Autowired
    PropertyValueDao propertyValueDao;
    @Autowired
    PropertyService propertyService;

    @CacheEvict(allEntries = true)
    public void update(PropertyValue propertyValue){
        propertyValueDao.save(propertyValue);
    }

    @Cacheable(key="'propertyValues-one-pid-'+#p0.id+ '-ptid-' + #p1.id")
    public PropertyValue getByPropertyAndProduct(Product product,Property property){
        return propertyValueDao.getByPropertyAndProduct(property,product);
    }


    @Cacheable(key="'propertyValues-pid-'+ #p0.id")
    public List<PropertyValue> list(Product product){
        return propertyValueDao.findByProductOrderByIdDesc(product);
    }

    public void init(Product product){
        List<Property> propertys = propertyService.listByCategory(product.getCategory());
        for (Property property : propertys) {
            PropertyValue propertyValue = getByPropertyAndProduct(product,property);
            if(null==propertyValue){
                propertyValue  = new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValueDao.save(propertyValue);
            }
        }
    }


}
