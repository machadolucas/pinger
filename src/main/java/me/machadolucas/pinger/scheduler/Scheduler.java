package me.machadolucas.pinger.scheduler;

import me.machadolucas.pinger.entity.TargetEntity;
import me.machadolucas.pinger.repository.TargetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Component
public class Scheduler {

    private static final long MILLISECONDS_IN_HOUR = 3600000;

    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TargetRepository targetRepository;

    @Cacheable("targets")
    private List<TargetEntity> getTargets() {
        return targetRepository.findAll();
    }

    @Scheduled(fixedRate = 6 * MILLISECONDS_IN_HOUR, initialDelay = 15000)
    public void callTargets() {
        List<TargetEntity> targets = getTargets();
        if (targets.size() > 0) {
            targets.forEach(target -> {
                ResponseEntity<String> responseEntity = restTemplate.getForEntity(target.getUrl(), String.class);
                log.info("m=callTargets,statusCode={},url={}", responseEntity.getStatusCodeValue(), target.getUrl());
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    target.setLastSuccess(new Date());
                } else {
                    target.setLastFail(new Date());
                }
            });

            targetRepository.save(targets);
        }
    }

    @Scheduled(fixedRate = 3 * 24 * MILLISECONDS_IN_HOUR, initialDelay = 15000)
    public void clearTargetCache() {
        log.info("m=clearTargetCache");
        resetTargetsCache();
    }

    @CacheEvict(value = "targets", allEntries = true)
    public void resetTargetsCache() {
    }
}
