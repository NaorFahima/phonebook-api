package com.example.phonebook.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.MongoId;
import java.time.Instant;

public abstract class BaseObject {

    @MongoId
    private String id;
    @CreatedDate
    private Instant createAt;
    @LastModifiedDate
    private Instant updateAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public Instant getCreateAt() {
        return createAt;
    }
    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }
    public Instant getUpdateAt() {
        return updateAt;
    }
    public void setUpdateAt(Instant updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return "BaseObject{" +
                "id='" + id + '\'' +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                '}';
    }
}
