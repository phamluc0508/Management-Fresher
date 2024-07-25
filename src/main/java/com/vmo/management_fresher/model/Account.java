package com.vmo.management_fresher.model;

import jakarta.persistence.*;

import com.vmo.management_fresher.base.entity.BaseEntity;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Account extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "username", unique = true)
    private String username;

    private String password;

    @ManyToOne
    private Role role;
}
