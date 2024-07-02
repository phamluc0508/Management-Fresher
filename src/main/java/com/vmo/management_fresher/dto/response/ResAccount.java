package com.vmo.management_fresher.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResAccount {
    private String id;
    private String username;
    private Set<String> roles;
}
