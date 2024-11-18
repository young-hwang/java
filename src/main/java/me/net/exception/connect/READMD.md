# 네트워크 예외1 - 연결 예외

네트워크 연결 시 발생할 수 있는 예외 정리

> net.exception.connect.ConnectMain 참조

## java.net.UnknownHostException

호스트를 알 수 없음

- `999.999.999.999` : 이런 IP는 존재하지 않음
- `google.gogo` : 이런 도메인 이름은 존재하지 않음

## java.net.ConnectException: Connection refused

`Connection refused` 메시지는 연결이 거절되었다는 뜻

- 연결이 거절되었다는 것은, 우선은 네트워크를 통해 해당 IP의 서버 컴퓨터에 접속은 했다는 뜻
- 그런데 해당 서버 컴퓨터가 45678 포트를 사용하지 않기 때문에 TCP 연결을 거절
- IP에 해당하는 서버는 켜져있지만, 사용하는 PORT가 없을 때 주로 발생
- 네트워크 방화벽 등에서 무단 연결로 인지하고 연결을 막을 때도 발생
- 서버 컴퓨터의 OS는 이때 TCP RST(Reset)라는 패킷을 보내서 연결을 거절
- 클라이언트가 연결 시도 중에 RST 패킷을 받으면 이 예외가 발생

## 윈도우 OS

윈도우의 경우 다음과 같이 `Connection refused` 뒤에 `connect` 라는 메시지가 하나 더 붙는 차이가 있음

- `java.net.ConnectException: Connection refused: connect`

## TCP RST(Reset) 패킷

TCP 연결에 문제가 있다는 뜻

- 이 패킷을 받으면 받은 대상은 바로 연결을 해제해야 함

# 네트워크 예외2 - 타임아웃

## TCP 연결 타임아웃 - OS 기본

> net.exception.connect.ConnectTimeoutMain1 참조

- 사설 IP 대역(주로 공유기에서 사용하는 IP 대역)의 `192.168.1.250` 을 사용
- 해당 IP로 연결 패킷을 보내지만 IP를 사용하는 서버가 없으므로 TCP 응답이 오지 않음
- 또는 해당 IP로 연결 패킷을 보내지만 해당 서버가 너무 바쁘거나 문제가 있어서 연결 응답 패킷을 보내지 못하는 경우도 있음
- 그렇다면 이때 무한정 기다려야 할까?

### OS 기본 대기 시간

TCP 연결을 시도했는데 연결 응답이 없다면 OS에 설정된 timed out 발생

- Windows: 약 21초
- Linux: 약 75초 에서 180초 사이
- Mac: 75초

해당 시간이 자나면 `java.net.ConnectionException: Operation timed out`이 발생

```shell
java.net.ConnectException: Operation timed out
	at java.base/sun.nio.ch.Net.connect0(Native Method)
	...
```

TCP 연결을 클라이언트가 이렇게 오래 대기하는 것은 좋은 방법이 아님

연결이 안되면 고객에게 빠르게 현재 연결에 문제가 있다고 알려주는 것이 더 나은 방법

## TCP 연결 타임아웃 - 직접 설정

TCP 연결 타임아웃 시간을 직접 설정해 보기

> net.exception.connect.ConnectTimeoutMain2 참조
 
### new Socket()

`Socket` 객체를 생성할 때 인자로 IP, PORT를 모두 전달하면 생성자에서 바로 TCP 연결을 시도

하지만 IP, PORT를 모두 빼고 객체를 생성하면, 객체만 생성되고, 아직 연결은 시도하지 않음

추가적으로 필요한 설정을 더 한 다음에 `socket.connect()` 를 호출하면 그때 TCP 연결을 시도함

이 방식을 사용하면 추가적인 설정을 더 할 수 있는데, 대표적으로 타임아웃을 설정할 수 있음

```java
public void connect(SocketAddress endpoint, int timeout) throws IOException {...}
```

`InetSocketAddress` : `SocketAddress` 의 자식으로 IP, PORT 기반의 주소를 객체로 제공

`timeout` : 밀리초 단위로 연결 타임아웃을 지정할 수 있음

타임아웃 시간이 지나도 연결이 되지 않으면 다음 예외가 발생

`java.net.SocketTimeoutException: Connect timed out`

## TCP 소켓 타임아웃 - read 타임아웃

타임아웃 중에 또 하나 중요한 타임아웃이 존재

바로 소켓 타임아웃 또는 read 타임 아웃이라고 부르는 타임아웃

앞에서 설명한 연결 타임아웃은 TCP 연결과 관련이 되어있음

연결이 잘 된 이후에 클라이언트가 서버에 어떤 요청을 했다고 가정할 때 서버가 계속 응답을 주지 않는다면, 무 한정 기다려야 하는 것일까?

서버에 사용자가 폭주하고 매우 느려져서 응답을 계속 주지 못하는 상황이라면 어떻게 해야할까?

이런 경우에 사용하는 것이 바로 소켓 타임아웃(read 타임아웃)임

> net.exception.connect.SoTimeoutServer 참조
> net.exception.connect.SoTimeoutClient 참조

- `socket.setSoTimeout()` 을 사용하면 밀리초 단위로 타임아웃 시간을 설정할 수 있음
- 여기서는 3초를 설정
-  3초가 지나면 다음 예외가 발생
 
`java.net.SocketTimeoutException: Read timed out`

타임아웃 시간을 설정하지 않으면 `read()` 는 응답이 올 때 까지 무한 대기

### 클라이언트 실행 결과

```shell
java.net.SocketTimeoutException: Read timed out
	at java.base/sun.nio.ch.NioSocketImpl.timedRead(NioSocketImpl.java:278)
	...
```

### 실무

자주 발생하는 장애 원인 중 하나가 바로 연결 타임아웃, 소켓 타임아웃(read 타임 아웃)을 누락하기 때문에 발생

서버도 외부에 존재하는 데이터를 네트워크를 통해 불러와야 하는 경우가 있음

예를 들어서 주문을 처리하는 서버가 있는데, 주문 서버는 외부에 있는 서버를 통해 고객의 신용카드 결제를 처리해야 한다고 가정

신용카드를 처리하는 회사가 3개 존재

고객 주문 서버 신용카드A 회사 서버(정상)

고객 주문 서버 신용카드B 회사 서버(정상)

고객 주문 서버 신용카드C 회사 서버(문제)

신용카드 A, 신용카드 B 서버는 문제가 없고, 신용카드C 회사 서버에 문제가 발생해서 응답을 주지 못하는 상황이라고 가정

주문 서버는 계속 신용카드 C 회사 서버의 응답을 기다리게 됨

여기서 문제는 신용카드C의 결제에 대해서 주문 서버도 고객에게 응답을 주지 못하고 계속 대기하게 됨

신용카드C로 주문하는 고객이 누적 될 수록 주문 서버의 요청은 계속 쌓이게 되고, 신용카드 C 회사 서버의 응답을 기다리는 스레드도 점점 늘어남

결국 주문 서버에 너무 많은 사용자가 접속하게 되면서 주문 서버에 장애가 발생

결과적으로 신용카드C 때문에 신용카드A, 신용카드B를 사용하는 고객까지 모두 주문을 할 수 없는 사태가 벌어짐

**이런 장애는 신용카드 C 회사의 문제일까? 아니면 주문 서버 개발자의 문제일까?**

만약 주문 서버에 연결, 소켓 타임아웃을 적절히 설정했다면, 신용카드 C 회사 서버가 연결이 오래 걸리거나 응답을 주지 않을 때 타임아웃으로 처리할 수 있음

이렇게 되면 요청이 쌓이지 않기 때문에, 주문 서버에 장애가 발생하지 않음

타임아웃이 발생하는 신용카드 C 사용자에게는 현재 문제가 있다는 안내를 하면됨

나머지 신용카드A, 신용카드 B는 정상적으로 작동

**외부 서버와 통신을 하는 경우 반드시 연결 타임아웃과 소켓 타임아웃을 지정하자.**


