/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.iws;

import com.ltlogic.db.entity.Product;
import com.ltlogic.service.core.ProductService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Jaimel
 */
public class ProductIWS {
    @Autowired
    ProductService productService;
    
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }
}
