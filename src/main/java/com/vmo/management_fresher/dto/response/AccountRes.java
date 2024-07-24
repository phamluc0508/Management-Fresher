package com.vmo.management_fresher.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRes {
    private String id;
    private String username;
    private String role;
}
