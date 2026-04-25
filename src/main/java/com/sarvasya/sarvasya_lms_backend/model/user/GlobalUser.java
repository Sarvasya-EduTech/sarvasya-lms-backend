package com.sarvasya.sarvasya_lms_backend.model.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", schema = "tenant")
@SuperBuilder
@NoArgsConstructor
public class GlobalUser extends BaseUser {
}








