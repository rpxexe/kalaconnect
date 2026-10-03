package com.kalaconnect.repository;

import com.kalaconnect.dto.AdminDashboardMetricsDto;
import com.kalaconnect.model.AdminAction;

import java.util.List;

public interface AdminRepository {

    AdminDashboardMetricsDto getDashboardMetrics();

    void recordAdminAction(AdminAction action);

    List<AdminAction> findRecentActions(int limit);
}
