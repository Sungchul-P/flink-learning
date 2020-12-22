# Flink Harness를 사용하여 IDE에서 Stateful Functions 실행

- MyMessages : 입출력 메시지에 사용할 클래스 정의
- MyConstants : Ingress&Egress 식별자와 FunctionType 선언
- MyFunction : StatefulFunction 구현 (출력 메시지를 Egress로 전송)
- MyRouter : FunctionType과 식별자를 기반으로 특정 StateFun으로 라우팅  

- RunnerTest : Flink Harness를 이용하여 임의이 데이터로 StateFun을 테스트

[ RunnerTest 실행 결과 ]
```
MyOutputMessage(do, hello do)
MyOutputMessage(Zt, hello Zt)
MyOutputMessage(9j, hello 9j)
MyOutputMessage(X1, hello X1)
MyOutputMessage(Cu, hello Cu)
MyOutputMessage(OU, hello OU)
MyOutputMessage(7c, hello 7c)
...
```
