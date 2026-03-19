package com.example.javaplayground.transactional;

import com.example.javaplayground.transactional.domain.AuditLog;
import com.example.javaplayground.transactional.domain.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String msg, FailFlag flag, int step) {
        auditLogRepository.save(new AuditLog(null, msg, null));
        if (step == 1 && flag == FailFlag.AUDIT_FIRST) throw new RuntimeException("audit 1 error");
        if (step == 2 && flag == FailFlag.AUDIT_SECOND) throw new RuntimeException("audit 2 error");
    }
}
