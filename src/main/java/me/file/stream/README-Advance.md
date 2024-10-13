# 스트림 시작 1

자바가 가진 데이터를 `hello.dat`라는 파일에 저장하려면 어떻게 해야 하나?

자바 프로세스가 가지고 있는 데이터를 밖으로 보내려면 출력 스르팀을 사용하면 되고, 반대로 외부 데이터르 자바 프로세스 안으로 가져오려면 입력 스트림을 사용하면 됨

각 스트림은 단방향으로 흐름

## 스트림 시작 - 예제 1

> StreamStartMainV1 참조
> 

**new FileOutputStream("temp/hello.dat")**

- 파일에 데이터를 출력하는 스트림
- 파일이 없으면 파일을 자롱으로 만들고 데이터를 해당 파일에 저장
- 폴더를 만들지는 않기 때문에 폴더는 미리 만들어 두어야함

**write()**

- byte 단위로 값을 출력
- 65, 65, 67 출력

**new FileInputStream("temp/hello.dat")**

- 파일에서 데이터를 읽어오는 스트림

**read()**

- 파일에서 데이터를 byte 단위로 하나씩 읽어옴
- 순서대로 65, 66, 67을 읽어옴
- 파일의 끝에 도달해서 더이상 읽을 내용이 없다면 -1 반환
  - 파일의 끝(EOF, End of File)

**close()**

- 파일에 접근하는 것은 자바 입장에서 외부 자원을 사용하는 것임
- 자바에서 내부 객체는 자동으로 GC가 되지만 외부 자원은 사용 후 반드시 닫아주어야 함

**실행 결과**

```shell
65
66
67
-1
```

- 입력한 순서대로 축력되는 것 확인, 마지막 파일의 끝에 도달해서 -1이 출력

**실행 결과 - temp/hello.dat**

```shell
ABC
```

- `hello.dat`에 분명 byte로 65, 66, 67을 저장, 왜 개발툴이나 텍스트 편집기에서 열어보면 ABC라고 표현되는가?
- read()로 읽어서 출력한 경우에는 65, 66, 67이 정상 출력
- 개발툴이나 텍스트 편집기는 UTF-8 또는 MS949 문자 집합을 사용해서 byte 단위의 데이터를 문자로 디코딩해서 보여줌

**참고: 파일 append옵션**

`FileOutputStream`의 생성자에는 `append` 라는 옵션이 존재

```java
new FileOutputStream("temp/hello.dat", true);
```

- `true`: 기존 파일의 끝에 이어서 씀
- `false`: 기존 파일의 데이터를 지우고 처음부터 다시 씀(기본값)

## 스트림 시작 - 예제 2

파일의 데이터를 읽을 때 파일의 끝까지 읽어야 한다면 다음과 같이 반복문을 사용

> StreamStartMainV2 참조

- 입력 스트림의 read() 메소드는 파일의 끝에 도달하면 -1을 반환, 따라서 -1을 반환할 때까지 반복문을 사용하면 파일의 데이터를 모두 읽을 수 있음

**실행 결과**

```shell
65
66
67
```

**참고 - read()가 int를 반환하는 이유**

- 부호 없는 바이트 표현:
  - 자바에서 byte는 부호 있는 8비트 값(-128 ~ 127)
  - int로 반환함으로써 0에서 255까지의 모든 가능한 바이트 값을 부호 없이 표현할 수 있음
- EOF(End of File)표시:
  - byte를 표현하려면 256 종류의 값을 모두 사용해야 함
  - 자바의 byte는 -128에서 127까지 256종류의 값만 가실 수 있어, EOF를 위한 특별한 값을 할당하기 어려움
  - int는 0-255까지 모든 가능한 바이트 값을 표현하고, 여기에 추가로 -1을 반환하여 스트림의 끝(EOF)를 나타낼수 있음
- 참고로 write()의 경우도 비슷한 이유로 int 타입을 입력 받음
