# Spring Batch 크게 보기

## Job

하나의 완전한 배치 처리를 의미함.  
실제로 우리가 흔히 접하는 배치 작업은 모두 job으로 표현됨.

- 매일 심야에 수행되는 "일일 매출 집계"
- 매주 일요일마다 처리되는 "휴면 회원 정리"
- 매월 1일에 실행되는 "정기 결제"

등등

## Step

Job 을 구성하는 실행 단위를 의미함.  
예를 들면 "일일 매출 집계" Job 은 다음과 같은 Step 으로 진행됨.

1. 매출 집계 Step
2. 알림 발송 Step
3. 캐시 갱신 Step

하나의 Job 은 여러 개의 Step 으로 구성되어 있으며,  
Job 이 성공적으로 완료되기 위해서는 모든 Step 이 정상적으로 수행되어야 함.

## Spring Batch 가 제공하는 영역

- `Job`, `Step`: Spring Batch가 제공하는 핵심 컴포넌트
- `JobLauncher`: Job을 실행하고 실행에 필요한 파라미터를 전달하는 역할. 배치 작업 실행의 시작점이라고 볼 수 있음. 
- `JobRepository`: 배치 처리의 모든 메타데이터를 저장하고 관리하는 핵심 저장소로 Job과 Step의 실행 정보(시작/종료 시간, 상태, 결과 등)를 기록함. 저장된 정보들은 모니터링이나 문제 발생 시 재실행 등에 활용됨.
- `ExecutionContext`: Job과 Step 실행 중의 상태 정보를 key-value 형태로 담는 객체. Job과 Step 간의 데이터 공유나 Job 재시작 시 상태 복원에 사용된다 
- 데이터 처리 컴포넌트 구현체: Spring Batch는 데이터를 '읽기-처리-쓰기' 방식으로 처리하며, 이를 위한 다양한 구현체를 제공함.
  - `ItemReader` 구현체: `JdbcCursorItemReader`, `JpaPagingItemReader`, `MongoCursorItemReader`, ...
  - `ItemWriter` 구현체: `JdbcBatchItemWriter`, `JpaPagingItemReader`, `MongoItemWriter`, ...

## 개발자가 제어하는 영역

### Job/Step구성

`@Configuration` 을 사용해 `Job`과 `Step`의 실행 흐름을 정의한다.  
각 `Step`의 실행 순서와 조건을 설정하고, Spring 컨테이너에 등록해 배치 잡의 동작을 구성한다.  
Spring의 DI(의존성 주입)를 활용해 `ItemReader`, `ItemProcessor`, `ItemWriter` 등 배치 작업에 필요한 컴포넌트들을 조합하고 배치 플로우를 완성한다.  

### 데이터 처리 컴포넌트 활용

Spring Batch 에서는 `ItemReader`, `ItemWriter`와 같은 데이터 처리 컴포넌트의 구현체를 제공한다.    
하지만 파일의 포맷이나 SQL 쿼리 조건 등 세부 로직은 개발자가 직접 지정해야 한다.  
예를 들어, `FlatFileItemReader`로 CSV 파일을 읽을 경우, CSV의 각 컬럼을 자바 객체의 프로퍼티와 매핑하는 방식은 개발자가 직접 지정해야 한다.

### 단순 작업 처리

모든 배치 잡이 데이터를 읽고-처리하고-쓰는 방식으로만 구성되지는 않는다.  
때로는 파일 복사, 디렉토리 정리, 알림 발송과 같은 단순 작업이 필요하다.  
Spring Batch는 이러한 작업을 직접 구현할 수 있는 포인트를 제공하며, 이를 구현하는 것은 개발자의 영역이다.

### 커스텀 데이터 처리 컴포넌트

Spring Batch는 다양한 데이터 소스를 다룰 수 있는 기본 `ItemReader`, `ItemWriter` 구현체를 제공하지만, 모든 데이터베이스나 포맷을 커버하진 않는다.   
Spring Batch가 제공하지 않는 데이터 소스를 다뤄야 하는 경우 `ItemReader`나 `ItemWriter`를 개발자가 직접 구현해야 한다.  
읽은 데이터를 가공하고 필터링하는 역할을 하는 `ItemProcessor`는 비즈니스 로직의 핵심을 담당하므로, 개발자가 직접 구현해야 한다.
