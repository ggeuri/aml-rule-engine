package main.java.com.amlengine.rule.config;

import java.math.BigDecimal;

public class HighAmountAfterDepositRuleConfig {
    private final BigDecimal percentThreshold;
    private final long absoluteThresholdKrw;
    private final int windowMinutes;

    public HighAmountAfterDepositRuleConfig(BigDecimal percentThreshold, long absoluteThresholdKrw, int windowMinutes) {
        this.percentThreshold = percentThreshold;
        this.absoluteThresholdKrw = absoluteThresholdKrw;
        this.windowMinutes = windowMinutes;
    }
    

    public BigDecimal getPercentThreshold() {
        return percentThreshold;
    }

    public long getAbsoluteThresholdKrw() {
        return absoluteThresholdKrw;
    }

    public int getWindowMinutes() {
    return windowMinutes;
}
}
