package com.tyut.tmall.elasticsearch;

import com.tyut.tmall.pojo.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface ProductESDAO extends ElasticsearchRepository<Product,Integer> {
}
