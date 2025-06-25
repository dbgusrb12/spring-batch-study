# JobParameters

`JobParameters` 는 배치 작업에 전달되는 입력 값으로, 이 값은 배치가 어떤 조건에서 어떤 데이터를 다룰지를 결정하는 데 핵심적인 역할을 함.

예를 들어, 매일 실행되는 배치 작업에서 날짜나 파일 경로 같은 값은 매번 바뀌는데,  
이럴 때 `JobParameters`를 사용하면 동일한 `Job`을 입력 값만 바꿔서 유연하게 실행할 수 있음.

`JobParameters`는 `-D` 옵션과 달리 단순한 값 전달을 넘어서, 배치의 실행과 제어를 관리하는 핵심 메커니즘.

## 프로퍼티와 `JobParameters` 의 차이

### 입력값 동적 변경

프로퍼티는 애플리케이션 시작 시 주입되는 정적인 값이므로,  
별도의 부가적인 처리 없이 실행 중인 애플리케이션에 동적으로 값을 변경할 수는 없음.

### 메타데이터

Spring Batch 는 `JobParameters` 의 모든 값을 메타데이터 저장소에 기록하며,  
이를 통해 `Job` 인스턴스 식별 및 재시작 처리, `Job` 실행 이력 추적 등의 기능에 사용함.

프로퍼티의 경우 Spring Batch 메타데이터로 기록되지 않기 때문에, 위의 기능 사용이 불가능해지고,  
이는 배치 작업의 운영과 제어를 제한하는 요소가 됨.

> Spring Batch는 `JobRepository`를 통해 `Job`과 `Step`의 실행 이력을 메타데이터 저장소에 기록한다.

## `JobParameters` 구성 요소

- `parameterName`: 배치 Job에서 파라미터를 찾을 때 사용 할 key 값. 해당 이름으로 Job 내에서 파라미터에 접근 가능함.
- `parameterValue`: 파라미터의 실제 값
- `parameterType`: 파라미터의 타입 기본값은 String 타입이다. (`java.lang.String`, `java.lang.Integer` 와 같은 fully qualified name 사용)
- `identificationFlag`: Spring Batch 에 해당 파라미터가 JobInstance 식별에 사용될 파라미터 인지 여부를 전달하는 값. 기본값은 true이며, true면 식별에 사용된다는 의미.

`Job`에 여러 개의 파라미터를 전달하려면 아래 예제처럼 파라미터를 공백으로 구분하여 전달.
> `./gradlew bootRun --args='--spring.batch.job.name=basicJob basicId=SAMPLE-ID,java.lang.String count=5,java.lang.Integer'`

> `./gradlew bootRun --args='--spring.batch.job.name=dateTimeJob executionDate=2025-06-25,java.time.LocalDate startTime=2025-06-25T00:00:01,java.time.LocalDateTime'`

> `./gradlew bootRun --args='--spring.batch.job.name=enumJob color=RED,com.hg.batch.enumeration.Color'`

> `./gradlew bootRun --args='--spring.batch.job.name=pojoJob id=SAMPLE-ID,java.lang.String count=5,java.lang.Integer username=hyungyu,java.lang.String'`

> `./gradlew bootRun --args="--spring.batch.job.name=jsonJob name='{\"value\":\"SAMPLE-NAME,SAMPLE-NAME2\",\"type\":\"java.lang.String\"}'"`

