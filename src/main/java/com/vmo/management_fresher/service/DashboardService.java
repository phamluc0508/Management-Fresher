package com.vmo.management_fresher.service;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> numberFreshersCenter(Long centerId);
    List<Map<String, Object>> findFreshersByPoint();
    List<Map<String, Object>> findFreshersByAVG();
}
