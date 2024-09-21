# CAS(Compare And Swap) 연산

## 락 기반 방식의 문제점

`SynchronizedInteger`와 같은 클래스는 데이터를 보호하기 위해 락을 사용
락은 `synchronized`, `Lock(ReentrantLock)`등을 사용하는 것을 의미
락은 특정 자원을 보호하기 위해 스레드가 해당 자원에 대한 접근하는 것을 제한
락이 걸려 있는 동안 다른 스레드는 해당 자원에 접근할 수 없고, 락이 해제될 때까지 대기해야 함
락 기반 접근에서는 락을 획득하고 해제하는 데 시간이 소요됨

락을 사용하는 예
- 락이 있는지 확인
- 락을 획득하고 임계 영역에 들어감
- 작업을 수행
- 락을 반납

락을 획득하고 반납하는 과정을 반복
직관적이지만 상대적으로 무거운 방식

## CAS

락을 걸지 않고 원작적인 연산을 수행하는 방법으로 이를 CAS(Compare And Swap, Compare And Set) 연산이라 함
락을 사용하지 않으므로 락 프리(lock free) 기법이라 함

> **참고**
>
> CAS 연산은 락을 완전히 대체하는 것이 아닌 **작은 단위의 일부 영역**에 적용 가능
>
> 기본은 락을 사하고, 특별한 경우 CAS 적용할 수 있음

**compareAndSet(0, 1)**

`atomicInteger`가 가지고 있는 값이 현재 0이면 이 값을 1로 변경하라는 매운 단순한 메소드
- 만약 `atomicInteger`의 값이 현재 0이라면 `atomicInteger`의 값을 1로 변경하고 `true` 반환
- 만약 `atomicInteger`의 값이 현재 0이 아니라면 `atomicInteger`의 값을 변경하지 않고  `false` 반환
 
가장 중요한 것은 이 메소드는 **원자적으로 실행**된다는 것
이 메소드가 제공하는 기능이 CAS(compareAndSet) 연산

## 실행 순서 분석

### CAS 성공 케이스

```mermaid
stateDiagram
    direction LR
    s1: CPU Core
    s2: Main Memory
    s3: x0001, value = 0
    s4: 원자적 연산
    [*] --> Thread
    Thread --> s1 : 0. CAS(x001, 0, 1)
    state s4 {
        state s2 {
            s3
        }
    }
    s1 --> s2 : 1. x001 확인
    s1 --> s3 : 2. 0이면 1로 변경
```

- `AtomicInteger` 내부에 있는 `value` 값이 0이라면 1로 변경하고 싶음
- `compareAndSet(0, 1)`을 호출, 매개변수 왼쪽 기대값, 오른쪽 변경값
- `CAS`연산은 메모리에 있는 값이 기대하는 이라면 원하는 값으로 변경
- 메모리에 있는 `value` 값이 0이므로 1로 변경
- 2개의 명령어로 원자적이지 않은 연산으로 보임
  - 먼저 메인 메모리의 값 확인
  - 해당 값이 기대값인 0이라면 1로 변경


### CPU 하드웨어 지원

`CAS`연산은 원자적이지 않은 두개 연산을 CPU 하드웨어 차원에서 특별하게 하나의 원자적인 연산으로 묶어서 제공하는 기능
소프트웨어가 제공하는 것이 아닌 하드웨어에서 제공
현대 CPU 들은 CAS 연산을 위한 명령어를 제공

CPU는 다음 두 과정을 묶어 하나의 원자적 명령어로 만듬
따라서 다른 스레드가 개입할 수 없음
1. x001 값을 확인
2. 읽은 값이 0이면 1로 변경

CPU는 두 과정을 묶어서 하나 원자적인 명령어로 만들기 위해 1번과 2번 사이에 다른 스레드가 x001 값을 변경 못하게 막음
CPU 입장에서 1번과 2번의 과정은 찰나의 순간(성능에 큰영향을 끼치지 않음)

```mermaid
stateDiagram
    direction LR
    s1: CPU Core
    s2: Main Memory
    s3: x0001, value = 1
    s4: 원자적 연산
    [*] --> Thread
    Thread --> s1 : true
    state s4 {
        state s2 {
            s3
        }
    }
```

- `value`의 값이 0 -> 1로 변경
- CAS 연산으로 값이 성공적으로 변경되어 `true` 반환

### CAS 실패 케이스

