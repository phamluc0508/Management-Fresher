package com.vmo.management_fresher.service;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> numberFreshersCenter(String uid, Long centerId);

    List<Map<String, Object>> findFreshersByPoint(String uid);

    List<Map<String, Object>> findFreshersByAVG(String uid);
}
