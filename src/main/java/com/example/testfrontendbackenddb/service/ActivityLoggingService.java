package com.example.testfrontendbackenddb.service; // Apna package name daalein

import com.yourpackage.dto.ActivityLogDto;
import com.yourpackage.entity.UserActivityLog;
import com.yourpackage.repository.UserActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityLoggingService {

    @Autowired
    private UserActivityLogRepository logRepository;

    public void saveActivity(Long userId, ActivityLogDto dto) {
        UserActivityLog log = new UserActivityLog();
        log.setUserId(userId);
        log.setAction(dto.getAction());
        log.setPageUrl(dto.getPageUrl());
        log.setDetails(dto.getDetails());
        logRepository.save(log);
    }
}
