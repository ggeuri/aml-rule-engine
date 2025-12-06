package main.java.com.amlengine.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import main.java.com.amlengine.domain.TransactionDTO;
import main.java.com.amlengine.domain.TransactionType;

public class TransactionGenerator {
    private final Random random = new Random();

    public List<TransactionDTO> generate(GeneratorConfig config){
        List<TransactionDTO> txList = new ArrayList<>();

        long baseUid = 1000;

        for(int i = 0; i < config.getUserCount(); i++){
            long uid = baseUid + i;

            for (int j = 0; j < config.getTxPerUser(); j++) {
                TransactionType type = pickType(config.getTypeRatio()); 



            }

        }


        return txList;
    }

    public TransactionType pickType(Map<TransactionType, Double> ratio){
        double sum = 0.0;
        for (Double v : ratio.values()) {
        sum += v;
        }

        double r = random.nextDouble() * sum;
        
         double cumulative = 0.0;

        for (Map.Entry<TransactionType, Double> e : ratio.entrySet()) {
            cumulative += e.getValue();      
            if (r <= cumulative) {           
            return e.getKey();          
            }
        }

        List<TransactionType> keys = new ArrayList<>(ratio.keySet());
        int i = random.nextInt(keys.size());

        return keys.get(i);
    }
    
}
