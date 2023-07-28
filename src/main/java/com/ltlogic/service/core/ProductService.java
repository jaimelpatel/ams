/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.core;

import com.ltlogic.db.entity.Product;
import com.ltlogic.db.repository.ProductRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Jaimel
 */
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepo;
    
    public List<Product> getAllProducts(){
        return productRepo.getAllProducts();
    }
}
