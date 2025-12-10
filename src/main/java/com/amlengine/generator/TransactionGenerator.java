package main.java.com.amlengine.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import main.java.com.amlengine.domain.TransactionDTO;
import main.java.com.amlengine.domain.TransactionType;
import main.java.com.amlengine.domain.TxTypeHelper;

public class TransactionGenerator {
    private final Random random = new Random();

    public List<TransactionDTO> generate(GeneratorConfig config) {
        List<TransactionDTO> txList = new ArrayList<>();

        long baseUid = 1000;

        for (int i = 0; i < config.getUserCount(); i++) {
            long uid = baseUid + i;

            for (int j = 0; j < config.getTxPerUser(); j++) {
                TransactionType type = pickType(config.getTypeRatio());

                TransactionDTO tx = new TransactionDTO();
                tx.setUid(uid);
                tx.setType(type);
           
                tx.setTransactedAt(randomTransactedAt(config));
                
                String country = randomCountry(config.getForeignCountryRatio());
                tx.setCountryCode(country);
                tx.setIpAddress(randomIp(country));

                if (type == TransactionType.KRW_DEPOSIT || type == TransactionType.KRW_WITHDRAW) {
                    // KRW 전용: amountKrw만, 나머지는 null
                    long amount = randomKrwAmount(type);
                    tx.setAmountKrw(amount);
                    tx.setAssetSymbol(null);
                    tx.setAssetQuantity(null);
                    tx.setQuotePriceKrw(null);
                    tx.setFromAddress(null);
                    tx.setToAddress(null);
                } else {
                    // COIN / TRADE 공통
                    String symbol = randomAssetSymbol();
                    BigDecimal quantity = null;
                    BigDecimal quotePrice = null;

                    tx.setAssetSymbol(symbol);
                    tx.setAssetQuantity(quantity);
                    tx.setQuotePriceKrw(quotePrice);

                    BigDecimal amount = quotePrice.multiply(quantity);
                    long amountKrw = amount.setScale(0, RoundingMode.HALF_UP).longValue();
                    tx.setAmountKrw(amountKrw);
                }

                txList.add(tx);
            }
        }

        return txList;
    }

    public TransactionType pickType(Map<TransactionType, Double> ratio) {
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

    private LocalDateTime randomTransactedAt(GeneratorConfig config) {
        int minutes = config.getDurationMinutes();
        int offset = minutes > 0 ? random.nextInt(minutes) : 0;

        return config.getStartAt().plusMinutes(offset);
    }

    private String randomCountry(double foreignRatio) {
        double r = random.nextDouble();
        if (r > foreignRatio) {
            return "KR";
        }

        String[] foreign = { "US", "EU", "UK", "JP", "HK", "KH", "IR", "RU", "VN" };

        return foreign[random.nextInt(foreign.length)];
    }

    private String randomAssetSymbol() {
        String[] erc20 = { "ETH", "USDT", "USDC", "LINK", "ARB" };
        return erc20[random.nextInt(erc20.length)];
    }

    private String randomIp(String countryCode) {
        int a, b, c, d;

        if ("KR".equals(countryCode)) {
            int[] krFirst = { 59, 106, 110, 112, 118, 121, 175, 183, 211, 218, 222 };
            a = krFirst[random.nextInt(krFirst.length)];
        } else {
            int[] foreignFirst = { 3, 8, 13, 18, 34, 52, 54, 63, 91, 104, 151, 172 };
            a = foreignFirst[random.nextInt(foreignFirst.length)];
        }

        b = random.nextInt(256);
        c = random.nextInt(256);
        d = random.nextInt(256);

        return a + "." + b + "." + c + "." + d;
    }

    private long randomKrwAmount(TransactionType type) {

        double p = random.nextDouble();
        long min;
        long max;

        if (TxTypeHelper.isDeposit(type)) {
            if (p < 0.70) { 
                min = 100_000L;
                max = 3_000_000L;
            } else if (p < 0.90) { 
                min = 3_000_000L;
                max = 30_000_000L;
            } else if (p < 0.99) { 
                min = 30_000_000L;
                max = 500_000_000L;
            } else { 
                min = 500_000_000L;
                max = 30_000_000_000L;
            }

        } else if (TxTypeHelper.isWithdraw(type)) {
            if (p < 0.50) { 
                min = 100_000L;
                max = 3_000_000L;
            } else if (p < 0.85) { 
                min = 3_000_000L;
                max = 30_000_000L;
            } else if (p < 0.98) { 
                min = 30_000_000L;
                max = 500_000_000L;
            } else { 
                min = 500_000_000L;
                max = 30_000_000_000L;
            }

        } else {
            min = 100_000L;
            max = 3_000_000L;
        }

        if (min >= max)
            return min;

        long diff = max - min + 1;
        long offset = (long) (random.nextDouble() * diff); // 0 ~ diff-1

        return min + offset;
    }

    // private BigDecimal randomQuantity() {

    // };

    // generateTxId(type, uid, j)

    // randomQuotePrice()
    // randomAddress()

}