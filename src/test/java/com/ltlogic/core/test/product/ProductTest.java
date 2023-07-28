/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.core.test.product;

import com.ltlogic.db.entity.Product;
import com.ltlogic.db.repository.ProductRepository;
import com.ltlogic.iws.ProductIWS;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.transaction.Transactional;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 *
 * @author Jaimel
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TransactionConfiguration(defaultRollback = false)
@Service
public class ProductTest {
    @Autowired
    ProductIWS productIWS;
    
    @Autowired
    ProductRepository productRepo;
    
    @Test
    @Ignore
    public void createProductTest(){
        Product product = new Product();
        product.setProductName("NLG Cash");
        product.setProductDescription("NLG cash that users can use to enter and play tournaments and wagers.");
        ArrayList<BigDecimal> priceRange = new ArrayList<>();
        
        BigDecimal fiveDollars = new BigDecimal(5.45);
        BigDecimal tenDollars = new BigDecimal(10.60);
        BigDecimal twentyDollars = new BigDecimal(20.90);
        BigDecimal fiftyDollars = new BigDecimal(51.80);
        BigDecimal oneHundredDollars = new BigDecimal(103.30);
        
        priceRange.add(fiveDollars);
        priceRange.add(tenDollars);
        priceRange.add(twentyDollars);
        priceRange.add(fiftyDollars);
        priceRange.add(oneHundredDollars);
        
        product.setPriceRangeList(priceRange);
        
        productRepo.persistProduct(product);
    }
}
