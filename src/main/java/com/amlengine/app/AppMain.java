package main.java.com.amlengine.app;

import main.java.com.amlengine.io.CsvTransactionLoader;

public class AppMain {
    public static void main(String[] args) {
        System.out.println("start");
        CsvTransactionLoader cs = new CsvTransactionLoader();

        cs.load("/Users/rimu/Projects/MyProjects/csvTest/tx_test_mixed.csv");
    }
    
}
