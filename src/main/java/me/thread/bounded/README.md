# 생산자 소비자 문제(producer-consumer problem)

- 생산자(Producer): 데이터를 생성하는 역할
- 소비자(Consumer): 생성된 데이터를 사용하는 역할
- 버퍼(Buffer): 생산자가 생성한 데이터를 일시적으로 저장하는 공간. 한정된 크기를 가지며 생산자와 소비자 사이 버퍼를 통해 데이터를 주고 받음

## 문제 상황

- 생성자가 너무 빠를 때: 버퍼가 가득차서 더 이상 데이터를 넣을 수 없을 때 까지 생산자가 데이터를 생성. 버퍼가 가득찬 경우 생산자는 버퍼에 빈 공간이 생길때 까지 기다려야 함
- 소비자가 너무 빠를 때: 버퍼가 비어서 더 이상 소비할 데이터가 없을 때까지 소비자가 데이터를 처리. 버퍼가 비었을 때 소비자는 버퍼에 새로운 데이터가 들어올 때까지 기다려야 함

생산자 소비자 문제(producer-consumer problem): 생산자 스레드와 소비자 스레드가 특정 자원을 함께 생산하고 소비하면서 발생하는 문제
한정된 버퍼 문제(bounded-buffer problem): 중간에 버퍼의 크기가 한정되어 있기 때문에 발생.

## Sample

생산자 소비자 문제 예제 만들기

```java
public interface BoundedQueue {
    void put(String data);
    String take();
}
```

- BoundedQueue: 버퍼 역할을 하는 큐의 인터페이스
- put(data): 버퍼에 데이터를 보관(생성자 스레드가 호출, 데이터 생성)
- take(): 버퍼에 보관된 데이터 가져감(소비자 스레드가 호출, 데이터 소비)

```java
public class BoundedQueueV1 implements BoundedQueue{
    private final Queue<String> queue = new ArrayDeque<>();
    private final int max;

    public BoundedQueueV1(int max) {
        this.max = max;
    }

    @Override
    public synchronized void put(String data) {
        if (queue.size() == this.max) {
            log("[put] 큐가 가득 참, 버림: " + data);
            return;
        }
        queue.add(data);
    }

    @Override
    public synchronized String take() {
        if (queue.isEmpty()) {
            return null;
        }
        return queue.poll();
    }

    @Override
    public String toString() {
        return queue.toString();
    }
}
```

- BoundedQueueV1: 한정된 버퍼 역할을 하는 가장 단순한 구현체
- Queue, ArrayDeque: 데이터를 중간에 보관하는 버퍼 큐(Queue), 구현체로 ArrayDequeue 사용
- int max: 한정된 버퍼으므로 버퍼에 저장할 수 있는 최대 크기 지정
- put(): 큐에 데이터 저장. 큐가 가득찬 경우 데이터 버림
- take(): 큐의 데이터 가져가기. 큐에 데이터 없는 경우 null
- toString(): 버퍼 역할을 하는 queue 정보를 출력

**임계 영역**

핵심 공유 자원은 queue(ArrayDequeue) 임
여러 스레드가 접근할 예정이므로 synchronized를 사용해서 한번에 하나의 스레드만 put() 또는 take()를 실행할 수 있도록 안전한 임계 영역 생성

## Object - wait, notify

synchronized 를 사용한 임계 영역 안에서 락을 가지고 무한 대기하는 문제는 Object 클래스에 해결 방안 존재
Object 클래스는 wait(), notify()라는 메소드를 제공
Object 는 모든 자바 객체의 부모이기 때문에 자바의 기본 기능이라 생각하면 됨

**wait(), notify()**

- Object.notify()
  - 현재 스레드가 가진 락을 반납하고 대기(WAITING)
  - 현재 스레드를 대기(WAITING) 상태로 전환. 현재 스레드가 synchronized 블록이나 메서드에서 락을 소유하고 있을 때만 호출할 수 있음
  - 호출한 스레드는 락을 반납하고 다른 스레드가 해당 락을 획득할 수 있도록 함
  - 대기 상태로 전환된 스레드는 다른 스레드가 notify() 또는 notifyAll() 을 호출할 때까지 대기 상태를 유지
- Object.notify()
  - 대기 중인 스레드 중 하나를 깨움
  - synchronized 블록이나 메소드에서 호출되어야 함
  - 깨운 스레드는 락을 다시 획득할 기회를 얻게 되며 만약 대기 중인 스레드가 여러 개라면 그 중 하나만 깨움
- Object.notifyAll()
  - 대기 중인 모든 스레드를 깨움
  - synchronized 블록이나 메소드에서 호출되어야 함
  - 모든 대기중인 스레드가 락을 획득할 수 있는 기획를 얻게 됨
  - 모든 스레드를 깨워야 할 필요가 있을 경우 유용

**스레드 대기 집합(wait set)**

synchronized 임계 영역 안에서 Object.wait() 호출하면 스레드는 대기(WAITING) 상태에 들어감
대기 상태에 들어간 스레드를 관리하는 것을 대기 집합(wait set) 이라함
모든 객체는 각자의 대기 집함을 가지고 있음

모든 객체는 락(모니터 락)과 대기 집합을 가짐
둘은 한쌍으로 사용
락을 획득한 객체의 대기 집합을 사용해야 함
- synchronized 메소드를 적용하면 해당 인스턴스의 락을 사용
