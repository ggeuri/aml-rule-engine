package com.amlengine.io;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.amlengine.domain.TransactionDTO;
import com.amlengine.domain.TransactionType;

public class CsvTransactionLoader {
    
    
    // 나중에 CSV 경로 받아서 List<TransactionDTO> 반환할 예정
    public static List<TransactionDTO> load(String csvPath){
        List<TransactionDTO> txList = new ArrayList<>();

        // TODO: Files.lines()로 CSV 파일 읽기
        try (Stream<String> csvMap = Files.lines(Path.of(csvPath))) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); 
            
            csvMap.skip(1).forEach(line -> {System.out.println(line);
                String[] splitTx = line.split(",",-1); 


                try {
                    long uid = Long.parseLong(splitTx[0]) ;
                    LocalDateTime transactedAt = LocalDateTime.parse(splitTx[1].trim(),dtf) ;
                    TransactionType txType = TransactionType.valueOf(splitTx[2]) ;
                    String assetSymbol = splitTx[3].isBlank() ? null : splitTx[3];
                    BigDecimal assetQuantity = splitTx[4].isBlank() ? null : new BigDecimal(splitTx[4].trim()); 
                    BigDecimal quotePriceKrw = splitTx[5].isBlank() ? null : new BigDecimal(splitTx[5].trim()); 
                    long amountKrw = Long.parseLong(splitTx[6]) ; 
                    String countryCode = (splitTx[7] == null || splitTx[7].isBlank()) ? "UNKNOWN" :  splitTx[7];
                    String ipAddress = splitTx[8] ;
                    String fromAddress = splitTx[9].isBlank() ? null : splitTx[9].trim();
                    String toAddress = splitTx[10].isBlank() ? null : splitTx[10].trim();
                    String txId = splitTx[11] ;

                    TransactionDTO txDto = new TransactionDTO();

                    txDto.setUid(uid);
                    txDto.setTransactedAt(transactedAt);
                    txDto.setTxType(txType);
                    txDto.setAssetSymbol(assetSymbol);
                    txDto.setAssetQuantity(assetQuantity);
                    txDto.setQuotePriceKrw(quotePriceKrw);
                    txDto.setCountryCode(countryCode);
                    txDto.setIpAddress(ipAddress);
                    txDto.setFromAddress(fromAddress);
                    txDto.setToAddress(toAddress);
                    txDto.setTxId(txId);
                    
                    long finalAmountKrw = calculateRawAmountKrw(txType,assetQuantity,quotePriceKrw,amountKrw);
                    txDto.setAmountKrw(finalAmountKrw);

                    if (!isValid(txDto)) {
                        System.out.println("[CSV 스킵] line = " + line);
                        System.out.println("reason: invalid value 입력");
                        return;
                    }

                    txList.add(txDto);
                } catch (Exception e) {
                    System.out.println("[CSV 스킵] line = " + line);
                    System.out.println("reason: " + e.getMessage());
                } 

            });
            

            
        } catch (IOException e) {
            e.printStackTrace();
        }
          
    return txList; }

public static long calculateRawAmountKrw(TransactionType txType, BigDecimal assetQuantity, BigDecimal quotePriceKrw, long csvAmountKrw) {
    if(txType == TransactionType.KRW_DEPOSIT || txType == TransactionType.KRW_WITHDRAW){
        return csvAmountKrw; 
    } 
    
    if(assetQuantity == null || quotePriceKrw == null ){
        throw new IllegalArgumentException("필수값(assetQuantity/quotePriceKrw) 누락. s거래타입: " + txType);}
    
    // 매수 매도도? 주식이어도 어차피 수량*가격이니까?
    BigDecimal amountKrw = quotePriceKrw.multiply(assetQuantity);

       return amountKrw.setScale(0, RoundingMode.HALF_UP).longValue();
        
    }

public static boolean isValid(TransactionDTO tx){

        if(tx.getUid() <= 0) return false;
        if(tx.getTransactedAt() == null) return false;
        if (tx.getTxType() == null) return false;
        if (tx.getTxId() == null || tx.getTxId().isBlank()) return false;

        TransactionType txType = tx.getTxType();
        // 원화 validate
        if(txType == TransactionType.KRW_DEPOSIT || txType == TransactionType.KRW_WITHDRAW){
            // uid, amountKrw, txId 필수 나머지 null assetSymbol/assetQuantity/quotePriceKrw, from/to
            if(tx.getAssetSymbol() != null) return false; 
            if(tx.getAssetQuantity() != null) return false; 
            if(tx.getQuotePriceKrw() != null) return false; 
            if(tx.getFromAddress() != null) return false; 
            if(tx.getToAddress() != null) return false; 

            return true; 

        }

        // 코인 validate
        if(txType == TransactionType.COIN_DEPOSIT ||  txType == TransactionType.COIN_WITHDRAW
            ||txType == TransactionType.TRADE_BUY || txType == TransactionType.TRADE_SELL ){
                //  assetSymbol, assetQuantity, quotePriceKrw, from/to ||assetSymbol, assetQuantity, quotePriceKrw
                if(tx.getAssetSymbol() == null) return false; 
                if(tx.getAssetQuantity() == null) return false; 
                if(tx.getQuotePriceKrw() == null) return false; 
                
            }else {
                return false; // 정의되지 않은 type
            }
            
        // 입출고 validate
        if(txType == TransactionType.COIN_DEPOSIT ||  txType == TransactionType.COIN_WITHDRAW){
             if(tx.getFromAddress() == null) return false; 
             if(tx.getToAddress() == null) return false; 
        }

        // 매수매도 
        if(txType == TransactionType.TRADE_BUY || txType == TransactionType.TRADE_SELL ){
            if(tx.getFromAddress() != null) return false; 
             if(tx.getToAddress() != null) return false; 

        }


    return true;
}
     
}

