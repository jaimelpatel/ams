    /*
     * To change this license header, choose License Headers in Project Properties.
     * To change this template file, choose Tools | Templates
     * and open the template in the editor.
     */
    package com.ltlogic.db.entity;

    import com.ltlogic.db.superentity.SuperEntity;
    import java.math.BigDecimal;
    import java.util.ArrayList;
    import javax.persistence.Entity;
    import javax.persistence.NamedQueries;
    import javax.persistence.NamedQuery;

    /**
     *
     * @author Jaimel
     */

    @NamedQueries({
        @NamedQuery(
                name = "Product.getAllProducts",
                query = "SELECT p FROM Product p"
        )
    })
    @Entity
    public class Product extends SuperEntity{

        private String productName;

        private ArrayList<BigDecimal> priceRangeList = new ArrayList<>();

        private String productDescription;

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public ArrayList<BigDecimal> getPriceRangeList() {
            return priceRangeList;
        }

        public void setPriceRangeList(ArrayList<BigDecimal> priceRangeList) {
            this.priceRangeList = priceRangeList;
        }

        public String getProductDescription() {
            return productDescription;
        }

        public void setProductDescription(String productDescription) {
            this.productDescription = productDescription;
        }
    }
