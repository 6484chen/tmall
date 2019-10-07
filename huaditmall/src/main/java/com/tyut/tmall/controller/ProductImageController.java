package com.tyut.tmall.controller;

import com.tyut.tmall.pojo.Product;
import com.tyut.tmall.pojo.ProductImage;
import com.tyut.tmall.service.CategoryService;
import com.tyut.tmall.service.ProductImageService;
import com.tyut.tmall.service.ProductService;
import com.tyut.tmall.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ProductImageController
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/21 18:01
 * @Version 1.0
 */
@RestController
public class ProductImageController {
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    CategoryService categoryService;

    @GetMapping("/products/{pid}/productImages")
    public List<ProductImage> list(@RequestParam("type") String type, @PathVariable("pid") int pid)throws Exception{
        Product product = productService.get(pid);
        if(ProductImageService.type_single.equals(type)){
            List<ProductImage> singles = productImageService.listSingleProductImages(product);
            return singles;
        }else if(ProductImageService.type_detail.equals(type)){
            List<ProductImage> details = productImageService.listDetailProductImages(product);
            return details;
        }else {
            return  new ArrayList<>();
        }
    }

    @PostMapping("/productImages")
    public Object add(@RequestParam("pid") int pid, @RequestParam("type") String type, MultipartFile image, HttpServletRequest request) throws Exception {
        Product product = productService.get(pid);
        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setType(type);

        productImageService.add(productImage);
        String folder = "img/";
        if (ProductImageService.type_single.equals(productImage.getType())) {
            folder += "producteSingle";
        } else {
            folder += "productDetail";
        }

        File imageFolder = new File(request.getServletContext().getRealPath(folder));
        File file = new File(imageFolder, productImage.getId() + ".jpg");
        String fileName = file.getName();
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try {
            image.transferTo(file);
            BufferedImage img = ImageUtil.change2jpg(file);
            ImageIO.write(img, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (ProductImageService.type_single.equals(productImage.getType())) {
            String imageFolder_small = request.getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle = request.getServletContext().getRealPath("img/productSingle_middle");
            File f_small = new File(imageFolder_small, fileName);
            File f_middle = new File(imageFolder_middle, fileName);
            f_small.getParentFile().mkdirs();
            f_middle.getParentFile().mkdirs();
            ImageUtil.resizeImage(file, 56, 56, f_small);
            ImageUtil.resizeImage(file, 217, 190, f_middle);
        }
        return productImage;

    }

    @DeleteMapping("/productImages/{id}")
    public String delete(@PathVariable("id") int id,HttpServletRequest request)throws Exception {
        ProductImage productImage = productImageService.get(id);
        productImageService.delete(id);
        String folder = "img/";
        if (ProductImageService.type_single.equals(productImage.getType())){
            folder += "productSingle";
        }else{
            folder += "productDetail";
        }
        File imageFolder = new File(request.getServletContext().getRealPath(folder));
        File file = new File(imageFolder,productImage.getId()+".img");
        String fileName = file.getName();
        file.delete();

        if (ProductImageService.type_single.equals(productImage.getType())) {
            String imageFolder_small = request.getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle = request.getServletContext().getRealPath("img/productSingle_middle");
            File f_small = new File(imageFolder_small, fileName);
            File f_middle = new File(imageFolder_middle, fileName);
            f_small.delete();
            f_middle.delete();
        }
        return null;
    }

}
