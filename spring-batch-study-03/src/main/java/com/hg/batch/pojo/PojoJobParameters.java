package com.hg.batch.pojo;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class PojoJobParameters {

    // 필드 의존성 주입
    @Value("#{jobParameters['id']}")
    private String id;
    private int count;
    private final String username;

    // 생성자 의존성 주입
    public PojoJobParameters(@Value("#{jobParameters['username']}") String username) {
        this.username = username;
    }

    // setter 의존성 주입
    @Value("#{jobParameters['count']}")
    public void setCount(int count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "PojoJobParameters{" +
                "id='" + id + '\'' +
                ", count=" + count +
                ", username='" + username + '\'' +
                '}';
    }
}
