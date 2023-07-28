/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
/**
 *
 * @author Bishistha
 */


//@SpringBootApplication
@EnableScheduling
public class SchedulerApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SchedulerApplication.class);
    }
}