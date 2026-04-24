package com.sarvasya.sarvasya_lms_backend.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@EqualsAndHashCode(callSuper = true)
public class User extends BaseUser {

    @Column(name = "class_id")
    private UUID classId;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "degree_id")
    private UUID degreeId;
}








