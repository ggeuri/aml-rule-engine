package com.amlengine.domain;

public final class TxTypeHelper {

    private TxTypeHelper(){};

    public static boolean isKrw(TransactionType type) {
        return switch (type) {
            case KRW_DEPOSIT, KRW_WITHDRAW -> true;
            default -> false;
        };
    }

    public static boolean isCoin(TransactionType type){
        return switch (type) {
            case COIN_DEPOSIT, COIN_WITHDRAW, TRADE_BUY, TRADE_SELL -> true; 
            default -> false;
        };
    }

    public static boolean isCoinIO(TransactionType type){
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

