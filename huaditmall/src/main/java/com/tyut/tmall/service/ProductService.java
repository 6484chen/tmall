package com.tyut.tmall.service;

import com.tyut.tmall.dao.ProductDao;
import com.tyut.tmall.elasticsearch.ProductESDAO;
import com.tyut.tmall.pojo.Category;
import com.tyut.tmall.pojo.Product;
import com.tyut.tmall.util.Page4Navigator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ProductService
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/21 16:07
 * @Version 1.0
 */
@Service
@CacheConfig(cacheNames = "products")
public class ProductService {
    @Autowired
    ProductDao productDao;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    ProductESDAO productESDAO;    //与数据库中的数据同步

    //初始化搜索引擎
    private void initDatabase2ES(){
        Pageable pageable = new PageRequest(0,5);
        Page<Product> page = productESDAO.findAll(pageable);
        if(page.getContent().isEmpty()){
            List<Product> products = productDao.findAll();
            for (Product product : products) {
                productESDAO.save(product);
            }
        }
    }



    @CacheEvict(allEntries=true)
    public void add(Product product){
        productDao.save(product);
        productESDAO.save(product);
    }
    @CacheEvict(allEntries=true)
    public void delete(int id){
        productDao.delete(id);
        productESDAO.delete(id);
    }
    @Cacheable(key = "'products-one-'+#p0")
    public Product get(int id) {
        Product product = productDao.findOne(id);
        return product;
    }
    @CacheEvict(allEntries=true)
    public void update(Product product){
        productDao.save(product);
        productESDAO.save(product);
    }

    @Cacheable(key = "'products-cid-'+#p0+'-page-'+#p1+'-'+#p2")
    public Page4Navigator<Product> list(int cid,int start,int size,int navigatePages){
        Category category = categoryService.get(cid);
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(start,size,sort);
        Page<Product> pageFormJPA = productDao.findByCategory(category,pageable);
        return new Page4Navigator<>(pageFormJPA,navigatePages);
    }


    //fore：
    //为多个分类填充产品集合
    public void fill(List<Category> categories){
        for (Category category : categories) {
            fill(category);
        }
    }

    //为分类填充产品集合
    public void fill(Category category){
        //因为 springboot 的缓存机制是通过切面编程 aop来实现的。
        // 从fill方法里直接调用 listByCategory 方法， aop 是拦截不到的，也就不会走缓存了。
        List<Product> products = listByCategory(category);
        productImageService.setFirstProductImages(products);
        category.setProducts(products);
    }

    //根据分类获得产品,
    @Cacheable(key="'products-cid-'+ #p0.id")
    public List<Product> listByCategory(Category category){
        return productDao.findByCategoryOrderById(category);
    }

    //为多个分类填充推荐产品集合，即把分类下的产品集合，按照8个为一行，拆成多行，以利于后续页面上进行显示
    public void fillByRow(List<Category> categories){
        int productNumberEachRow = 8;
        for (Category category : categories) {
            List<Product> products = category.getProducts();
            List<List<Product>> productsByRow = new ArrayList<>();
            //切为8个一行
            for (int i = 0; i < products.size(); i+=productNumberEachRow) {
                int size = i+productNumberEachRow;
                size = size>products.size()?products.size():size;
                List<Product> productsOfEachRow = products.subList(i,size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }

    public void setSaleAndReviewNumber(Product product){
        int saleCount = orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);

        int reviewCount = reviewService.getCount(product);
        product.setReviewCount(reviewCount);
    }

    public void setSaleAndReviewNumber(List<Product> products){
        for (Product product : products) {
            setSaleAndReviewNumber(product);
        }
    }


    //模糊查询
    public List<Product> search(String keyword,int start,int size){
        initDatabase2ES();
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
                .add(QueryBuilders.matchPhraseQuery("name",keyword), ScoreFunctionBuilders.weightFactorFunction(100))
                .scoreMode("sum")
                .setMinScore(10);
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(start,size,sort);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(functionScoreQueryBuilder).build();
//        List<Product> products = productDao.findByNameLike("%"+keyword+"%",pageable);
        Page<Product> page = productESDAO.search(searchQuery);
        return page.getContent();
    }





}
