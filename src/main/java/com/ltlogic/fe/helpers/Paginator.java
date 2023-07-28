/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.fe.helpers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

/**
 *
 * @author raymond
 */
@Service
public class Paginator {
    
    /*
     name null = one paging only in specific page
    */
    public void setPagination(String name, Integer currentPage, int totalItems, ModelMap model) {

        Integer totalPages = 0;
        
        if(totalItems > 0) {
            Double tempTotalPages = Math.ceil((double)totalItems / 15);
            totalPages = tempTotalPages.intValue();
        }
        
        List<Integer> pages = new ArrayList<>();
        if(totalPages > 1)
            for(int i = 0; i < totalPages; i++) {
                pages.add(i+1);
            }
        
        
        Integer prevPage = currentPage > 1 ? (currentPage -1) : 1;
        Integer nextPage = currentPage < totalPages ? currentPage +1 : totalPages;
        
        //pass null to use default paging
        if(name == null) {
            if(currentPage > 1) {
                model.addAttribute("hasPrevPage", true);
            }

            if(currentPage < totalPages) {
                 model.addAttribute("hasNextPage", true);
            }

            model.addAttribute("pages", pages);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("prevPage", prevPage);
            model.addAttribute("nextPage", nextPage);
        }
        else {
            if(currentPage > 1) {
                model.addAttribute(name + "HasPrevPage", true);
            }

            if(currentPage < totalPages) {
                 model.addAttribute(name + "HasNextPage", true);
            }

            model.addAttribute(name + "Pages", pages);
            model.addAttribute(name + "CurrentPage", currentPage);
            model.addAttribute(name + "PrevPage", prevPage);
            model.addAttribute(name + "NextPage", nextPage);
        }
    }
    
    public void setPaginationForNotifications(String name, Integer currentPage, int totalItems, ModelMap model) {

        Integer totalPages = 0;
        
        if(totalItems > 0) {
            Double tempTotalPages = Math.ceil((double)totalItems / 20);
            totalPages = tempTotalPages.intValue();
        }
        
        List<Integer> pages = new ArrayList<>();
        if(totalPages > 1)
            for(int i = 0; i < totalPages; i++) {
                pages.add(i+1);
            }
        
        
        Integer prevPage = currentPage > 1 ? (currentPage -1) : 1;
        Integer nextPage = currentPage < totalPages ? currentPage +1 : totalPages;
        
        //pass null to use default paging
        if(name == null) {
            if(currentPage > 1) {
                model.addAttribute("hasPrevPage", true);
            }

            if(currentPage < totalPages) {
                 model.addAttribute("hasNextPage", true);
            }

            model.addAttribute("pages", pages);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("prevPage", prevPage);
            model.addAttribute("nextPage", nextPage);
        }
        else {
            if(currentPage > 1) {
                model.addAttribute(name + "HasPrevPage", true);
            }

            if(currentPage < totalPages) {
                 model.addAttribute(name + "HasNextPage", true);
            }

            model.addAttribute(name + "Pages", pages);
            model.addAttribute(name + "CurrentPage", currentPage);
            model.addAttribute(name + "PrevPage", prevPage);
            model.addAttribute(name + "NextPage", nextPage);
        }
    }
}
