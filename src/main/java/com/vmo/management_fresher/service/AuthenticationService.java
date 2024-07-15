package com.vmo.management_fresher.service;

import com.vmo.management_fresher.dto.request.AuthenticationReq;
import com.vmo.management_fresher.dto.response.AuthenticationRes;

public interface AuthenticationService {
    AuthenticationRes login(AuthenticationReq request);
    Boolean introspect(String token);
    String logout(String uid, String token);
    AuthenticationRes refreshToken(String uid, String token);
    Boolean checkAdminRole(String uid);
    Boolean checkDirectorRole(String uid);
    Boolean checkFresherRole(String uid);
    Boolean checkIsMyself(String uid, Long employeeId);
    Boolean checkDirectorFresher(String directorAccId, Long fresherId);
    Boolean checkDirectorCenter(String directorAccId, Long centerId);
    Boolean checkEmployeeCenter(String employeeAccId, Long centerId);
}
