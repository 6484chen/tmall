package com.tyut.tmall.service;

import com.tyut.tmall.dao.CategoryDao;
import com.tyut.tmall.pojo.Category;
import com.tyut.tmall.pojo.Product;
import com.tyut.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName CategoryService
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/20 9:59
 * @Version 1.0
 */
@Service
@CacheConfig(cacheNames = "categories")
public class CategoryService {
    @Autowired
    CategoryDao categoryDao;

    @Cacheable(key = "'categories-page-'+#p0+'-'+#p1")
    public Page4Navigator<Category> list(int start,int size, int navigatePages){
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(start,size,sort);
        Page pageFromJPA = categoryDao.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    @Cacheable(key = "'categories-all'")
    public List<Category> list(){
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        return categoryDao.findAll(sort);
    }

    /**
     * @CacheEvict(allEntries=true)  这里将所有的key都删除，避免与分页的数据发生冲突
     * @CachePut(key = "'category-one-'+#p0") 这句注释不可以更新分页缓存页面categories-page-0-5里的数据
     */

    @CacheEvict(allEntries=true)
    //@CachePut(key = "'category-one-'+#p0")
    public void add(Category category){
        categoryDao.save(category);
    }
    @CacheEvict(allEntries=true)
    //@CacheEvict(key = "'category-one-'+#p0")
    public void delete(int id){
        categoryDao.delete(id);
    }

    @Cacheable(key = "'categories'+#p0")
    public Category get(int id){
        Category c = categoryDao.findOne(id);
        return  c;
    }
    @CacheEvict(allEntries=true)
   // @CachePut(key = "'category-one-'+#p0")
    public void update(Category bean){
        categoryDao.save(bean);
    }


    //fore:
    //这个方法的用处是删除Product对象上的 分类。 为什么要删除呢？ 因为在对分类做序列还转换为 json 的时候，
    // 会遍历里面的 products, 然后遍历出来的产品上，又会有分类，接着就开始子子孙孙无穷溃矣地遍历了，就搞死个人了
    //而在这里去掉，就没事了。 只要在前端业务上，没有通过产品获取分类的业务，去掉也没有关系

    public void removeCategoryFromProduct(List<Category> categories){
        for (Category category : categories) {
            removeCategoryFromProduct(category);
        }
    }
    //对分类做序列化转换为 json 的时候，会遍历里面的 products, 然后遍历出来的产品上，又会有分类，会造成死循环
    public void removeCategoryFromProduct(Category category){
        List<Product> products = category.getProducts();
        if(null!= products){
            for (Product product : products) {
                product.setCategory(null);
            }
        }
        List<List<Product>> productsByRow = category.getProductsByRow();
        if(null != productsByRow){
            for (List<Product> productList : productsByRow) {
                for (Product product : productList) {
                    product.setCategory(null);
                }
            }
        }
    }


}
