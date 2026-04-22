package com.sarvasya.sarvasya_lms_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users", schema = "tenant")
@SuperBuilder
@NoArgsConstructor
public class GlobalUser extends BaseUser {
}
