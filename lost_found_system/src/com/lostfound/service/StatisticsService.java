package com.lostfound.service;

import com.lostfound.repository.*;

public class StatisticsService {

    public static void showStats(){

        System.out.println("\n--- SYSTEM STATS ---");

        System.out.println("Users: "+
                UserRepository.getUsers().size());

        System.out.println("Lost Items: "+
                ItemRepository.getLostItems().size());

        System.out.println("Found Items: "+
                ItemRepository.getFoundItems().size());
    }
}