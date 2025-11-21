package main.java.com.amlengine.io;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import main.java.com.amlengine.domain.TransactionDTO;
import main.java.com.amlengine.domain.TransactionType;

public class CsvTransactionLoader {
    
    // 나중에 CSV 경로 받아서 List<TransactionDTO> 반환할 예정
    public static List<TransactionDTO> load(String csvPath){
        List<TransactionDTO> ls = new ArrayList<>();

        try (Stream<String> csvLines = Files.lines(Path.of("/Users/rimu/Projects/MyProjects/csvTest/sample_transactions.csv"))) {
            csvLines.forEach(e -> {System.out.println(e);});
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

        
                       
        
    // TODO: Files.lines()로 CSV 파일 읽기
    // TODO: split(",")로 snake_case 컬럼 분리
    // TODO: Long.parseLong / BigDecimal / LocalDateTime.parse 변환
    // TODO: TransactionType.valueOf 변환
    // TODO: Validation 로직 적용
    // TODO: amountKrw 계산
    // TODO: TransactionDTO 생성 후 리스트에 추가
    
    
    return ls; }
public static long calculateAmountKrw(TransactionType type, BigDecimal assetQuantity, BigDecimal quotePriceKrw, long amountKrw) {
    

    // TODO: type, quantity, price, rawAmount를 받아서 amountKrw 계산
    //  - RW_*  : CSV amount_krw 그대로 사용
    //  - COIN_* / TRADE_* : quote_price_krw * asset_quantity 계산해서 반올림
    return 0L; // TODO: 임시 리턴값 (나중에 진짜 계산으로 교체)

}
    


    
}

