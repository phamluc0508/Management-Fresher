package com.vmo.management_fresher.service;

import com.vmo.management_fresher.dto.request.AuthenticationReq;
import com.vmo.management_fresher.dto.response.AuthenticationRes;

public interface AuthenticationService {
    AuthenticationRes login(AuthenticationReq request);
    Boolean introspect(String token);
}
