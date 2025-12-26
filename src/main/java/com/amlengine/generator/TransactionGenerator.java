package com.amlengine.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.amlengine.domain.TransactionDTO;
import com.amlengine.domain.TransactionType;
import com.amlengine.domain.TxTypeHelper;

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

                tx.setTxId(generateTxId(type, uid, j));

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
                    BigDecimal quotePrice = randomQuotePrice(symbol);
                    BigDecimal quantity = randomQuantity(symbol);

                    tx.setAssetSymbol(symbol);
                    tx.setQuotePriceKrw(quotePrice);
                    tx.setAssetQuantity(quantity);

                    BigDecimal amount = quotePrice.multiply(quantity);
                    long amountKrw = amount.setScale(0, RoundingMode.HALF_UP).longValue();
                    tx.setAmountKrw(amountKrw);

                    // 코인 입출고면 주소도 세팅
                    if (type == TransactionType.COIN_DEPOSIT || type == TransactionType.COIN_WITHDRAW) {
                        tx.setFromAddress(randomAddress());
                        tx.setToAddress(randomAddress());
                    } else {
                        // TRADE
                        tx.setFromAddress(null);
                        tx.setToAddress(null);
                    }
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
        String[] asset = { "BTC","ETH", "USDT", "USDC", "LINK", "ARB" };
        return asset[random.nextInt(asset.length)];
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
                min = 1_000L;
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
                min = 10_000L;
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

    private static final String BTC_BASE58 = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final String HEX_CHARS = "0123456789abcdef";

    private String randomBtcAddress() {

        int length = 26 + random.nextInt(10); // 26~35

        StringBuilder sb = new StringBuilder();
        if (length >= 32) {
            sb.append('3');
        } else {
            sb.append('1');
        }

        for (int i = 1; i < length; i++) {
            int idx = random.nextInt(BTC_BASE58.length());
            sb.append(BTC_BASE58.charAt(idx));
        }

        return sb.toString();
    }

    private String randomEthAddress() {
        StringBuilder sb = new StringBuilder("0x");
        for (int i = 0; i < 40; i++) {
            int idx = random.nextInt(HEX_CHARS.length());
            sb.append(HEX_CHARS.charAt(idx));
        }
        return sb.toString();
    }

    private String randomAddress() {
        double p = random.nextDouble();

        if (p < 0.3) {
            return randomBtcAddress();
        } else {
            return randomEthAddress();
        }
    }

    private String generateTxId(TransactionType type, long uid, int seq) {
        String prefix;
        switch (type) {
            case KRW_DEPOSIT:
                prefix = "KD";
                break;
            case KRW_WITHDRAW:
                prefix = "KW";
                break;
            case COIN_DEPOSIT:
                prefix = "CD";
                break;
            case COIN_WITHDRAW:
                prefix = "CW";
                break;
            case TRADE_BUY:
                prefix = "TB";
                break;
            case TRADE_SELL:
                prefix = "TS";
                break;
            default:
                prefix = "TX";
        }

        return String.format("%s%06d%03d",
                prefix,
                uid,
                seq);
    }

    private BigDecimal randomQuantity(String symbol) {
        double p = random.nextDouble(); // 0.0 이상 1.0 미만

        double min;
        double max;

        if (symbol.equals("BTC")) {
            if (p < 0.70) {
                min = 0.001;
                max = 0.1;
            }
            // 중간 사이즈
            else if (p < 0.95) {
                min = 0.1;
                max = 1.5;
            } else {
                min = 1.5;
                max = 10.0;
            }
        } else if (symbol.equals("ETH")) {
            if (p < 0.70) {
                min = 0.001;
                max = 1.5;
            }
            // 중간 사이즈
            else if (p < 0.95) {
                min = 1.5;
                max = 30.0;
            } else {
                min = 30.0;
                max = 70.0;
            }
        } else {
            if (p < 0.50) {
                min = 1.0;
                max = 100.0;
            } else if (p < 0.95) {
                min = 100.0;
                max = 600.0;
            } else {
                min = 600.0;
                max = 1500.0;
            }
        }

        double range = max - min;
        double value = min + range * random.nextDouble();

        return BigDecimal
                .valueOf(value)
                .setScale(6, RoundingMode.HALF_UP);
    }

    private BigDecimal randomQuotePrice(String symbol) {
        double p = random.nextDouble(); // 0.0 ~ 1.0 미만
        long min;
        long max;

        if ("BTC".equals(symbol)) {
            // BTC: 5,000만 ~ 3억 구간
            if (p < 0.70) { // 70%
                min = 90_000_000L;
                max = 130_000_000L;
            } else if (p < 0.95) { // 25%
                min = 130_000_000L;
                max = 150_000_000L;
            } else { // 5%
                min = 150_000_000L;
                max = 230_000_000L;
            }
        } else if ("ETH".equals(symbol)) {
            // ETH: 200만 ~ 1,000만 정도 느낌
            if (p < 0.70) {
                min = 2_400_000L;
                max = 4_000_000L;
            } else if (p < 0.95) {
                min = 4_000_000L;
                max = 6_000_000L;
            } else {
                min = 6_000_000L;
                max = 12_000_000L;
            }
        } else if ("USDT".equals(symbol) || "USDC".equals(symbol)) {
            min = 1_200L;
            max = 1_600L;
        } else {
            if (p < 0.60) {
                min = 1_000L;
                max = 20_000L;
            } else if (p < 0.90) {
                min = 20_000L;
                max = 80_000L;
            } else {
                min = 80_000L;
                max = 200_000L;
            }
        }

        if (min >= max) {
            return BigDecimal.valueOf(min);
        }

        long diff = max - min + 1;
        long offset = (long) (random.nextDouble() * diff);
        long price = min + offset;

        return BigDecimal
                .valueOf(price)
                .setScale(0, RoundingMode.HALF_UP);
    }

}