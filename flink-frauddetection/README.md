
# Fraud Detection with the DataStream API

- 플링크의 데이터 스트림 API 를 사용하여 상태 저장(Stateful) 스트리밍 애플리케이션을 작성하는 방법을 알아봅니다. 

## Fraud Detection 기준

- 범죄자들은 훔친 신용 카드 번호가 유효한지 확인하기 위해 1 달러 이하의 소액 구매를 통해 테스트 합니다.
- 테스트가 성공하는 경우, 본인이 판매 등록한 물품을 구매하여 부정 이득을 얻습니다.
- 즉, 1달러 이하 소액 구매 이후 짧은 시간 이내에 큰 금액의 구매가 발생한 경우를 탐지하여 경고해야 합니다. 

## Fraud Transaction(이상 거래) 예제

![](./fraud-transactions.svg)

## FraudDetectionJob 실행 결과

```
18:29:19,644 INFO  org.apache.flink.walkthrough.common.sink.AlertSink           [] - Alert{id=3}
18:29:24,760 INFO  org.apache.flink.walkthrough.common.sink.AlertSink           [] - Alert{id=3}
18:29:29,905 INFO  org.apache.flink.walkthrough.common.sink.AlertSink           [] - Alert{id=3}
18:29:35,034 INFO  org.apache.flink.walkthrough.common.sink.AlertSink           [] - Alert{id=3}
18:29:40,161 INFO  org.apache.flink.walkthrough.common.sink.AlertSink           [] - Alert{id=3}
```