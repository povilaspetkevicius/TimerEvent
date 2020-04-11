package com.petkevicius.timer_event.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotEmpty;

@RedisHash("event")
public class Event {

    @Id
    private String id;

    @NonNull
    @NotEmpty
    private String name;

    private Long date;

    public String getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
