package main.java.com.amlengine.domain;

public final class TxTypeHelper {
    public static boolean isKrw(TransactionType type) {
        return switch (type) {
            case KRW_DEPOSIT, KRW_WITHDRAW -> true;
            default -> false;
        };
    }

    public static boolean isCoin(TransactionType type){
        return switch (type) {
            case COIN_DEPOSIT, COIN_WITHDRAW -> true; 
            default -> false;
        };
    }

    public static boolean isTrade(TransactionType type){
        return switch (type) {
            case TRADE_BUY, TRADE_SELL -> true; 
            default -> false;
        };
    }

    public static boolean isDeposit(TransactionType type){
        return switch (type) {
            case KRW_DEPOSIT, COIN_DEPOSIT -> true; 
            default -> false;
        };
    }

    public static boolean isWithdraw(TransactionType type){
        return switch (type) {
            case KRW_WITHDRAW, COIN_WITHDRAW -> true; 
            default -> false;
        };
    }
}


    public static boolean isKrTransactionType.KRW_WITHDRAW
    public static boolean isKrTransactionType.COIN_DEPOSIT
    public static boolean isKrTransactionType.COIN_WITHDRAW
    public static boolean isKrTransactionType.TRADE_BUY
    public static boolean isKrTransactionType.TRADE_SELL
    
}
