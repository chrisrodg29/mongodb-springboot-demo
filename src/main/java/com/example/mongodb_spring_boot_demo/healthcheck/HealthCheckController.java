package com.example.mongodb_spring_boot_demo.healthcheck;

import com.example.mongodb_spring_boot_demo.service.AccountsService;
import com.mongodb.MongoException;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/healthCheck")
@ConditionalOnProperty(value = "databaseConfiguration.mongodb2.uri")
public class HealthCheckController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckController.class);

    private final HealthCheckDao healthCheckDao;

    public HealthCheckController(HealthCheckDao healthCheckDao) {
        this.healthCheckDao = healthCheckDao;
    }

    @GetMapping("performHealthCheck/V1")
    public HealthCheckResponse performHealthCheckV1() {
        try {
            Document response = healthCheckDao.performHealthCheck();
            return new HealthCheckResponse(
                    "OK",
                    HealthCheckDao.HEALTH_CHECK_COMMAND,
                    response
            );
        } catch (MongoException e) {
            LOGGER.error("Cannot reach database", e);
            return new HealthCheckResponse(
                    "CRITICAL",
                    HealthCheckDao.HEALTH_CHECK_COMMAND,
                    e.getMessage()
            );
        }
    }

}
