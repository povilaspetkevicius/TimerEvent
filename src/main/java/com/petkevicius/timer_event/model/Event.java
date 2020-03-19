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
}
