package main.java.com.amlengine.app;

import java.util.ArrayList;
import java.util.List;

import main.java.com.amlengine.domain.TransactionDTO;
import main.java.com.amlengine.io.CsvTransactionLoader;
import main.java.com.amlengine.rule.Rule;
import main.java.com.amlengine.rule.config.RapidWithdrawRuleConfig;
import main.java.com.amlengine.rule.impl.Cu001ForeignCountryRule;
import main.java.com.amlengine.rule.impl.IO003RapidWithdrawRule;

public class AppMain {
    public static void main(String[] args) {
        System.out.println("start");
        CsvTransactionLoader cs = new CsvTransactionLoader();
        // Rule rule = new Cu001ForeignCountryRule();
        RapidWithdrawRuleConfig config = new RapidWithdrawRuleConfig(10, 3);
        Rule rule = new IO003RapidWithdrawRule(config);
        
        List<TransactionDTO> txList = cs.load("/Users/rimu/Projects/MyProjects/csvTest/rapid_withdraw_sample.csv");
        
        List<TransactionDTO> history = new ArrayList<>();
        
        for (TransactionDTO tx : txList) {
            history.add(tx);

            if(rule.match(tx, history)){
                System.out.println("[Alert 발생]");
                System.out.println("uid : " + tx.getUid());
                System.out.println("type : " + tx.getType());
                System.out.println("Time : " + tx.getTransactedAt());
                System.out.println("Country : " + tx.getCountryCode());
                System.out.println("txid : " + tx.getTxId());

            }
            
        }
    }
    
}
