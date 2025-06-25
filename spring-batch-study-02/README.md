# Chunk 와 Tasklet

Spring Batch 의 Step 은 크게 두가지 유형으로 나뉨.

- Chunk 지향 처리 (Chunk-Oriented Processing)
- Tasklet 지향 처리 (Tasklet-Oriented Processing)

## 태스크릿 지향 처리

대량 데이터 처리(읽고-처리하고-쓰기)하는 ETL 작업이 아닌,  
단순한 시스템 작업이나 유틸성 작업이 필요할 때 사용.

- 매일 새벽 불필요한 로그 파일 삭제
- 특정 디렉토리에 오래된 파일을 아카이브
- 사용자에게 단순한 알림, 이메일 발송
- 외부 API 호출 후 결과를 단순히 저장하거나 로깅

위와 같이 단일 비즈니스 로직에 초점을 맞춘 작업들에서 주로 쓰임.

### `org.springframework.batch.core.step.tasklet.Tasklet`

```java
public interface Tasklet {
	RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception;
}

```

Tasklet 인터페이스의 `execute()` 구현하여 원하는 로직을 작성함.  
적절한 `RepeatStatus` 를 반환해 Spring Batch 에 반복 여부를 설정함.

- `RepeatStatus.FINISHED` : Step 의 처리가 성공이든 실패든 해당 Step 이 완료 되었음을 의미
- `RepeatStatus.CONTINUABLE` : `Tasklet`의 `execute()`메서드가 추가로 더 실행되어야 함을 Spring Batch Step에 알리는 신호. Step의 종료는 보류되고, 필요한 만큼 `execute()` 메서드를 반복 호출   

### 정리

- 단순 작업에 적합
- `Tasklet` 인터페이스를 구현해서 처리됨
- `RepeatStatus` 로 실행을 제어함
- `Tasklet.execute()` 전후로 트랜잭션 지원

> 예제 코드 실행  
> `./gradlew bootRun --args='--spring.batch.job.name=taskletJob'`

## 청크 지향 처리

대부분의 배치 작업은 읽기-처리-쓰기 라는 공통된 패턴이 보임.  
Spring Batch 도 이 패턴을 따르는데, 해당 방식을 Spring Batch 에서는 청크 지향 처리 라고 부름.

청크는 데이터를 일정 단위로 쪼갠 덩어리를 말하며, Spring Batch 에서는 이 청크를 대상으로 읽기-처리-쓰기를 진행함.

청크 단위로 작업을 수행하는건 다음과 같은 이점을 가짐.

- 메모리 사용량이 안정적임.
- 트랜잭션이 청크 단위로 나뉘기 떄문에 복구가 쉽고 빠르며, 안정적임.

### 청크 지향 처리의 요소

#### `org.springframework.batch.item.ItemReader`

```java
public interface ItemReader<T> {
    T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException;
}
```

`read()` 메서드는 단일 요소를 반환함.  
데이터 소스에서 데이터를 하나씩 순차적으로 읽어오며, `null`을 반환하면 스텝이 종료됨.

Spring Batch 는 파일, 데이터베이스, 메시지큐 등 다양한 데이터 소스에 대한 표준 구현체를 제공함.

#### `org.springframework.batch.item.ItemProcessor`

```java
public interface ItemProcessor<I, O> {
    O process(I item) throws Exception;
}
```

`process()` 메서드는 입력 데이터(`I`)를 원하는 형태(`O`)로 변환하거나, 필터링이 가능함.  
필터링이 필요하다면 `null`을 반환하면 되고, 데이터 가공이 필요하지 않다면 `ItemProcessor` 는 생략 가능함.

#### `org.springframework.batch.item.ItemWriter`

```java
public interface ItemWriter<T> {
    void write(Chunk<? extends T> chunk) throws Exception;
}
```

`ItemReader`, `ItemProcessor` 와는 달리 `write()` 메서드는 단일 요소를 쓰는게 아닌 `Chunk` 단위로 묶어서 한 번에 데이터를 씀.  
데이터 소스에서 데이터를 하나씩 순차적으로 읽어오며, `null`을 반환하면 스텝이 종료됨.

Spring Batch 는 파일, 데이터베이스, 메시지큐 등 다양한 데이터 소스에 대한 표준 구현체를 제공함.

### `Reader`-`Processor`-`Writer` 패턴의 장점

- 완벽한 책임 분리
- 재사용성 극대화
- 높은 유연성
- 대용량 처리의 표준

### 청크 크기가 10일 경우 흐름

1. `ItemReader.read()` 메서드가 10번 호출되며 하나의 청크가 생성됨.
2. `ItemProcessor.process()` 메서드가 10번 호출되며 청크의 데이터를 하나씩 처리함.
3. 10개의 데이터가 1개의 청크로 묶여서 `ItemWriter.write()` 메서드에 전달되며, 한번에 쓰기가 진행됨.
4. 1-3 의 과정이 모든 데이터를 처리할 때 까지 반복됨. (`ItemReader.read()` 가 `null`을 반환하면 종료됨)

### 적절한 청크 사이즈는?

정답은 없음. 다음의 두 가지 트레이드오프와 업무 요구사항, 그리고 데이터의 양을 고려해서 적절히 선택해야 함.

#### 청크 사이즈가 클 때

그만큼 메모리에 많은 데이터를 한 번에 로드하게 되고,  
트랜잭션의 경계가 커지므로, 문제 발생시 롤백되는 데이터의 양도 많아짐.

#### 청크 사이즈가 작을 때

트랜잭션의 경계가 작아져서 문제 발생시 롤백되는 데이터가 최소화되고,  
그만큼 읽기/쓰기 I/O가 자주 발생하게 됨.  
건당 10ms가 소요되는 요청을 100만번 호출하면? 약 2.77시간(10ms × 100만 = 10,000,000ms)이 소요됨.


> 예제 코드 실행  
> `./gradlew bootRun --args='--spring.batch.job.name=chunkJob'`