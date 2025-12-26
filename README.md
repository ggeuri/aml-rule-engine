# Transaction AML Rule Engine & Alert Dashboard (v1.3)

> KRW·Coin 입·출금 + 매수/매도(6종 거래)에 대해  
> **금액·국가·패턴 기반 AML 룰을 적용 → Alert 생성 → 자동 배정 → Dashboard에서 조회**까지 한 번에 보는  
> **Java 기반 AML Rule Engine 데모 프로젝트**입니다.

---

## 1. 프로젝트 개요

### 🎯 목적

- 실무 AML 시스템에서 사용하는 **거래 도메인 모델링**을 Java 코드로 구현
- **Rule → Engine → Alert → Assignment → Dashboard**까지 하나의 파이프라인으로 연결
- 1개월 이내 구현 가능한 현실적 스코프 안에서,  
  실무 경험(가상자산/PG사 AML)을 **개발 관점**으로 재현

### 📦 주요 컴포넌트

- **CsvTransactionLoader** (`com.amlengine.io`)
  - CSV → `TransactionDTO` 리스트로 로딩
  - 필수값 검증 / 타입별 Validation / `amountKrw` 계산 포함
- **Rule 3종** (`com.amlengine.rule.impl`)
  - `IO002HighAmountAfterDepositRule` : 입금 직후 고액 출금
  - `IO003RapidWithdrawRule` : 단기간 반복 출금
  - `Cu001ForeignCountryRule` : 해외 국가 코드 출금 탐지
- **RuleEngine** (`com.amlengine.engine`)
  - 거래 리스트 + 룰 리스트 → `AlertDTO` 리스트 생성
  - **중복 방지 키**: `ruleId:txId`
- **AlertAssignmentService** (`com.amlengine.assignment`)
  - 상태가 `PENDING`인 Alert를 **Round Robin 방식**으로 담당자에게 자동 배정
- **AlertStatsService** (`com.amlengine.stats`)
  - 룰 / 위험도 / 상태 / 담당자 기준 Alert 통계
- **AlertJsonExporter + Dashboard** (`com.amlengine.io` + 정적 HTML)
  - Alert 리스트를 `alerts_boundary.json`으로 저장
  - 정적 HTML 대시보드에서 필터링/상세 조회

> 메인 실행 진입점: **`com.amlengine.app.AppMain`**

---

## 2. Tech Stack

- **Language**: Java 21
- **Build**: (IDE 프로젝트 기반, 필요 시 Gradle/Maven으로 확장 가능)
- **Dashboard**: HTML + CSS + Vanilla JS
- **CSV 처리**: `String.split(",", -1)` 기반 수동 파싱  
  (필요 시 OpenCSV / Jackson으로 대체 가능)

---

## 3. 디렉터리 구조

현재 프로젝트 구조는 다음과 같습니다.

```text
dashboard/
 ├ alert-dashboard.html
 └ alerts_boundary.json

src/
 └ main/
    └ java/
       └ com/
          └ amlengine/
             ├ app/
             │  └ AppMain.java
             │
             ├ assignment/
             │  └ AlertAssignmentService.java
             │
             ├ domain/
             │  ├ AlertDTO.java
             │  ├ AlertStatus.java
             │  ├ RiskLevel.java
             │  ├ RuleFrequency.java
             │  ├ TransactionDTO.java
             │  ├ TransactionType.java
             │  └ TxTypeHelper.java
             │
             ├ engine/
             │  └ RuleEngine.java
             │
             ├ generator/
             │  ├ GeneratorConfig.java
             │  └ TransactionGenerator.java
             │
             ├ io/
             │  ├ AlertJsonExporter.java
             │  └ CsvTransactionLoader.java
             │
             ├ rule/
             │  ├ Rule.java
             │  ├ config/
             │  │  ├ HighAmountAfterDepositRuleConfig.java
             │  │  ├ HighAmountRuleConfig.java
             │  │  └ RapidWithdrawRuleConfig.java
             │  └ impl/
             │     ├ Cu001ForeignCountryRule.java
             │     ├ IO002HighAmountAfterDepositRule.java
             │     └ IO003RapidWithdrawRule.java
             │
             └ stats/
                └ AlertStatsService.java

resources/
 └ .gitkeep   // (추후 설정/샘플 CSV 등 정적 파일 위치 예정)

⸻

4. 도메인 모델

4-1. TransactionType

package com.amlengine.domain;

public enum TransactionType {
    KRW_DEPOSIT,
    KRW_WITHDRAW,
    COIN_DEPOSIT,
    COIN_WITHDRAW,
    TRADE_BUY,
    TRADE_SELL
}

4-2. TransactionDTO (요약)

package com.amlengine.domain;

public class TransactionDTO {

    private long uid;
    private String txId;
    private LocalDateTime transactedAt;

    private long amountKrw;

    // 코인/매매 전용
    private String assetSymbol;
    private BigDecimal assetQuantity;
    private BigDecimal quotePriceKrw;

    // 온체인 입출고 전용
    private String fromAddress;
    private String toAddress;

    private String ipAddress;
    private String countryCode;
    private TransactionType type;

    // getter / setter ...
}

Validation 규칙 요약 (CsvTransactionLoader.isValid 기준)
	•	공통 필수: uid, transactedAt, type, amountKrw, txId
	•	KRW 입출금 (KRW_DEPOSIT, KRW_WITHDRAW)
	•	amountKrw 필수
	•	코인 관련 필드(assetSymbol, assetQuantity, quotePriceKrw)는 반드시 null
	•	fromAddress, toAddress도 반드시 null
	•	COIN / TRADE (COIN_*, TRADE_*)
	•	assetSymbol, assetQuantity, quotePriceKrw 필수
	•	COIN IO (COIN_DEPOSIT, COIN_WITHDRAW): fromAddress, toAddress 필수
	•	TRADE (TRADE_BUY, TRADE_SELL): fromAddress, toAddress는 항상 null

amountKrw 계산 규칙 (CsvTransactionLoader.calculateRawAmountKrw)
	•	KRW 계열: CSV의 amount_krw를 그대로 사용
	•	COIN / TRADE:
	•	quotePriceKrw × assetQuantity
	•	BigDecimal로 계산 후 HALF_UP 기준 0자리까지 반올림 → long 변환

⸻

4-3. AlertDTO

package com.amlengine.domain;

public class AlertDTO {

    private String alertId;              // ruleId:txId
    private String mainTxId;
    private List<String> relatedTxIds;

    private String ruleId;
    private String ruleName;
    private String ruleDescription;
    private RuleFrequency frequency;

    private LocalDateTime detectedAt;
    private RiskLevel riskLevel;
    private AlertStatus status;

    private String reviewer;
    private LocalDateTime reviewAssignedAt;

    // 거래 요약 정보
    private long uid;
    private long amountKrw;
    private String type;
    private String countryCode;
    private String assetSymbol;

    // getter / setter ...
}

	•	Alert = 룰 매칭 정보 + 거래 요약 정보
	•	Dashboard에서 Alert 리스트만으로
UID, 금액, 타입, 국가코드 등을 한 번에 확인 가능하도록 설계

4-4. 상태 / 위험도 / 룰 빈도

package com.amlengine.domain;

public enum AlertStatus {
    PENDING,
    ASSIGNED,
    IN_REVIEW,
    CLOSED
}

public enum RiskLevel {
    LOW,
    MEDIUM,
    HIGH
}

public enum RuleFrequency {
    REALTIME,
    HOURLY,
    DAILY
}

	•	v1.3에서는 PENDING → ASSIGNED까지 사용
	•	IN_REVIEW, CLOSED는 v2에서 Action Log/Workflow와 연계 예정

⸻

5. Helper & Rule Layer

5-1. TxTypeHelper

package com.amlengine.domain;

public final class TxTypeHelper {

    private TxTypeHelper() {}

    public static boolean isKrw(TransactionType type) { ... }
    public static boolean isCoin(TransactionType type) { ... }
    public static boolean isCoinIO(TransactionType type) { ... }
    public static boolean isTrade(TransactionType type) { ... }
    public static boolean isDeposit(TransactionType type) { ... }
    public static boolean isWithdraw(TransactionType type) { ... }
}

5-2. Rule 인터페이스

package com.amlengine.rule;

public interface Rule {
    String getRuleId();
    String getRuleName();
    String getRuleDescription();
    RiskLevel getRiskLevel();
    RuleFrequency getFrequency();

    boolean match(TransactionDTO tx, List<TransactionDTO> history);
}

5-3. RuleConfig

패키지: com.amlengine.rule.config
	•	HighAmountRuleConfig : 단일 고액 출금(IO-001 예정)
	•	HighAmountAfterDepositRuleConfig : IO-002에서 사용
(percentThreshold, absoluteThresholdKrw)
	•	RapidWithdrawRuleConfig : IO-003에서 사용
(windowMinutes, count)

5-4. 구현 룰 3종 (com.amlengine.rule.impl)
	•	IO002HighAmountAfterDepositRule – 입금 직후 고액 출금
	•	조건(개략)
	•	동일 uid
	•	windowMinutes 내 입금 합계 = sumDeposit
	•	동일 윈도우 내 출금 합계 = sumWithdraw
	•	sumDeposit > 0
	•	sumWithdraw ≥ absoluteThresholdKrw
	•	sumWithdraw / sumDeposit ≥ percentThreshold
	•	RiskLevel: HIGH, Frequency: DAILY
	•	IO003RapidWithdrawRule – 단기간 반복 출금
	•	조건(개략)
	•	동일 uid
	•	최근 windowMinutes 동안 출금 거래 건수 ≥ count
	•	RiskLevel: MEDIUM, Frequency: HOURLY
	•	Cu001ForeignCountryRule – 해외 국가 코드 출금
	•	조건
	•	출금 거래 (KRW_WITHDRAW, COIN_WITHDRAW)
	•	countryCode != "KR"
	•	RiskLevel: HIGH, Frequency: REALTIME

⸻

6. RuleEngine 동작 (com.amlengine.engine.RuleEngine)

public List<AlertDTO> run(List<TransactionDTO> txList) {
    // 1) 시간 순 정렬
    txList.sort((a, b) -> a.getTransactedAt().compareTo(b.getTransactedAt()));
    alerts.clear();
    alertKeys.clear();

    List<TransactionDTO> history = new ArrayList<>();

    for (TransactionDTO tx : txList) {
        history.add(tx);

        for (Rule rule : rules) {
            if (!rule.match(tx, history)) continue;

            String key = rule.getRuleId() + ":" + tx.getTxId();
            if (alertKeys.contains(key)) continue;

            alertKeys.add(key);
            AlertDTO alert = createAlert(tx, rule);
            alerts.add(alert);
        }
    }

    return alerts;
}

	•	중복 방지 키: ruleId:txId
	•	createAlert()에서
	•	detectedAt = tx.transactedAt
	•	uid, amountKrw, type, countryCode, assetSymbol 세팅

⸻

7. Assignment & Stats

7-1. AlertAssignmentService (com.amlengine.assignment)

public int assignRoundRobin(List<AlertDTO> alerts,
                            List<String> reviewers,
                            int startIndex) {
    if (alerts == null || alerts.isEmpty()) return startIndex;
    if (reviewers == null || reviewers.isEmpty()) return startIndex;

    int reviewerCount = reviewers.size();
    int index = startIndex;

    for (AlertDTO alert : alerts) {
        if (alert.getStatus() != AlertStatus.PENDING) continue;

        String reviewer = reviewers.get(index % reviewerCount);
        index++;

        alert.setReviewer(reviewer);
        alert.setStatus(AlertStatus.ASSIGNED);
        alert.setReviewAssignedAt(LocalDateTime.now());
    }

    return index;
}

	•	PENDING → ASSIGNED 상태 전환 담당
	•	나중에 weight 기반 / 우선순위 기반 Assignment로 확장 가능

7-2. AlertStatsService (com.amlengine.stats)

public class AlertStatsService {

    public Map<String, Long> countByRule(List<AlertDTO> alerts) { ... }

    public Map<RiskLevel, Long> countByRiskLevel(List<AlertDTO> alerts) { ... }

    public Map<AlertStatus, Long> countByStatus(List<AlertDTO> alerts) { ... }

    public Map<String, Long> countByReviewer(List<AlertDTO> alerts) { ... }
}

	•	룰 / 위험도 / 상태 / 담당자 기준 Alert 분포 확인
	•	reviewer == null 인 Alert는 담당자 통계에서 제외

⸻

8. Dashboard (정적 UI)
	•	위치: dashboard/alert-dashboard.html + dashboard/alerts_boundary.json
	•	AlertJsonExporter가 생성하는 파일: alerts_boundary.json
	•	정적 HTML 대시보드(dashboard/alert-dashboard.html)에서
fetch("alerts_boundary.json")로 로딩

8-1. 필터
	•	Risk: ALL / HIGH / MEDIUM / LOW
	•	Status: ALL / ASSIGNED / PENDING / CLOSED
	•	Rule: ALL / IO-002 / IO-003 / CU-001
	•	UID: 텍스트 입력 (부분 일치)
	•	Reviewer: 텍스트 입력 (부분 일치)

8-2. 리스트 & 상세
	•	좌측 테이블:
DetectedAt | Rule ID | Rule 설명 | UID | AmountKrw | Risk | Reviewer | Status
	•	우측 상세:
	•	ruleId : ruleDescription
	•	detectedAt (YYYY-MM-DD HH:mm)
	•	UID
	•	Asset
	•	Type
	•	amountKrw
	•	status
	•	Reviewer
	•	TxId
	•	riskLevel
	•	countryCode

⸻

9. CSV 스펙 (snake_case)

uid,
transacted_at,
type,
asset_symbol,
asset_quantity,
quote_price_krw,
amount_krw,
country_code,
ip_address,
from_address,
to_address,
tx_id

	•	파싱: line.split(",", -1) → trailing empty column 유지
	•	빈 문자열은 이후 단계에서 null 또는 "UNKNOWN"으로 변환

⸻

10. Transaction Generator (설계 + 최소 구현)

패키지: com.amlengine.generator
	•	GeneratorConfig
	•	userCount, txPerUser, startAt, durationMinutes
	•	typeRatio, foreignCountryRatio 등
	•	TransactionGenerator
	•	위 설정값을 바탕으로 랜덤 거래 시퀀스를 생성
	•	현재: 기본 랜덤 + 일부 위험 패턴(IO-002 / IO-003 / CU-001) 생성용으로 확장 예정

⸻

11. 실행 방법 (예시 – AppMain)

진입점: com.amlengine.app.AppMain

package com.amlengine.app;

public class AppMain {
    public static void main(String[] args) {

        // 1) CSV 로딩
        List<TransactionDTO> txList =
            CsvTransactionLoader.load("data/sample_transactions.csv");

        // 2) Rule 구성
        Rule io002 = new IO002HighAmountAfterDepositRule(config002);
        Rule io003 = new IO003RapidWithdrawRule(config003);
        Rule cu001 = new Cu001ForeignCountryRule();
        List<Rule> rules = List.of(io002, io003, cu001);

        // 3) RuleEngine 실행
        RuleEngine engine = new RuleEngine(rules);
        List<AlertDTO> alerts = engine.run(txList);

        // 4) Assignment
        List<String> reviewers = List.of("analyst1", "analyst2", "analyst3");
        AlertAssignmentService assignService = new AlertAssignmentService();
        assignService.assignRoundRobin(alerts, reviewers, 0);

        // 5) 통계 출력
        AlertStatsService statsService = new AlertStatsService();
        Map<String, Long> byRule = statsService.countByRule(alerts);
        Map<RiskLevel, Long> byRisk = statsService.countByRiskLevel(alerts);
        // System.out.println(...) 등으로 확인

        // 6) JSON Export
        AlertJsonExporter exporter = new AlertJsonExporter();
        exporter.exportToFile(alerts, Path.of("dashboard/alerts_boundary.json"));
    }
}


⸻

12. 현재 버전(v1.3) 상태

✅ 구현 완료
	•	CSV Loader + Validation + amountKrw 계산
	•	Rule 3종 (IO-002, IO-003, CU-001)
	•	RuleEngine (중복키: ruleId:txId)
	•	Round Robin Assignment (PENDING → ASSIGNED)
	•	AlertStatsService (룰/위험도/상태/담당자 기준 집계)
	•	정적 Dashboard (필터 + 리스트 + 상세)

🔧 이후 확장 아이디어
	•	TransactionGenerator로 시나리오형 테스트 데이터 생성 강화
	•	IN_REVIEW / CLOSED 상태 전환 + Action Log
	•	Rule/Config 외부화 (DB / YAML / Rule 관리 UI)
	•	Kafka, DB 영속화, Spring Boot 기반 REST API
	•	uid별 Sliding Window 캐시, Daily Suppression 등

⸻