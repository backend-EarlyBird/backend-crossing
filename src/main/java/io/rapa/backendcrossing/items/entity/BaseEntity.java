<<<<<<<< Updated upstream:src/main/java/io/rapa/backendcrossing/users/domain/entity/BaseEntity.java
package io.rapa.backendcrossing.users.domain.entity;
========
package io.rapa.backendcrossing.items.entity;
>>>>>>>> Stashed changes:src/main/java/io/rapa/backendcrossing/items/entity/BaseEntity.java

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@MappedSuperclass
public class BaseEntity {
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    protected Instant createdAt;
}
