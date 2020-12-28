# Shopping Cart Example

사용자가 장바구니에 상품을 담고(AddToCart), 결제(CheckOut)를 진행하면 재고(Inventory) 관리시스템에서 상품의 재고를 확인하여 
출고 가능 여부를 반환하고 최종 완료되면 영수증(Receipt)을 발급하는 예제입니다.  
(Ingress&Egress는 카프카를 사용하고, StateFun은 Embedded module로 구성했습니다)

- UserShoppingCart : 상품 추가 및 결제 요청 로직 구현
- Inventory : 재고 관리 로직 구현 
- Identifiers : Ingress&Egress 관련 설정 
- [AddToCart | CheckOut | Restock]Router : 메시지를 특정 함수로 매핑
- ShoppingCartModule : StateFun 과 Ingress&Egress 를 바인딩하여 파이프라인 생성

## 예제 실행

### 애플리케이션 빌드

Dockerfile에서 target 디렉터리의 JAR 파일이 필요하므로 빌드가 선행되어야 합니다.

```bash
mvn clean package
```

### 도커 이미지 빌드

```bash
docker-compose build
docker-compose up
```

### 카프카 메시지 확인

1) "restock-item" : 상품의 재고를 등록합니다. ("socks", 100) 
2) "add-to-cart" : 사용자의 장바구니에 상품을 저장합니다. ("kim", "socks", 10)
3) "checkout-item" : 특정 사용자의 결제를 요청합니다. ("kim")
4) "receipts" : 결제 완료된 상품 정보가 출력됩니다.

```bash
docker-compose exec kafka-broker kafka-console-producer.sh \
     --broker-list localhost:9092 \
     --topic restock-item
```
```bash
docker-compose exec kafka-broker kafka-console-producer.sh \
     --broker-list localhost:9092 \
     --topic add-to-cart
```
```bash
docker-compose exec kafka-broker kafka-console-producer.sh \
     --broker-list localhost:9092 \
     --topic checkout-item
```
```bash
docker-compose exec kafka-broker kafka-console-consumer.sh \
     --bootstrap-server localhost:9092 \
     --isolation-level read_committed \
     --from-beginning \
     --topic receipts
```

### Protocol Buffer(Optional)

```bash
protoc -I=src/main/protobuf --java_out=src/main/java src/main/protobuf/shoppingcart.proto
```
