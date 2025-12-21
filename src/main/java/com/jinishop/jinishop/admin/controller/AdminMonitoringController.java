package com.jinishop.jinishop.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/monitoring")
public class AdminMonitoringController {
    // 관리자 모니터링 페이지 컨트롤러

    private final HealthEndpoint healthEndpoint; // Actuator의 헬스 체크 결과를 가져오는 엔드포인트 (/actuator/health)
    private final MetricsEndpoint metricsEndpoint; // Actuator/Micrometer가 수집하는 메트릭을 코드로 조회하는 API (/actuator/metrics/**)

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMonitoring() {

        // Health
        var health = healthEndpoint.health();

        // JVM 메모리 used/max + 퍼센트
        Double jvmUsed = sumJvmMemory("jvm.memory.used", "heap");
        Double jvmMax  = sumJvmMemory("jvm.memory.max", "heap");
        Double jvmUsedPct = percent(jvmUsed, jvmMax);

        // HikariCP 커넥션 풀
        String pool = firstTagValue("hikaricp.connections.active", "pool"); // ex) HikariPool-2
        Double dbActive  = firstValue("hikaricp.connections.active", poolTag(pool));
        Double dbMax     = firstValue("hikaricp.connections.max", poolTag(pool));
        Double dbPending = firstValue("hikaricp.connections.pending", poolTag(pool));
        Double dbUsedPct = percent(dbActive, dbMax);

        // HTTP 요청 수 / 5xx 수
        Double httpTotalCount = valueByStatistic("http.server.requests", "COUNT", null);
        Double http5xxCount = sumHttp5xxCount();

        // CPU / Threads
        Double processCpu = firstValue("process.cpu.usage", null);
        Double systemCpu  = firstValue("system.cpu.usage", null);
        Double cpuUsagePct = cpuToPct(firstNonNull(processCpu, systemCpu));

        Double liveThreads = firstValue("jvm.threads.live", null);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("health", Map.of("status", health.getStatus().getCode()));

        body.put("jvmUsed", jvmUsed);
        body.put("jvmMax", jvmMax);
        body.put("jvmUsedPct", jvmUsedPct);

        body.put("dbActive", dbActive);
        body.put("dbMax", dbMax);
        body.put("dbPending", dbPending);
        body.put("dbUsedPct", dbUsedPct);

        body.put("httpTotalCount", httpTotalCount);
        body.put("http5xxCount", http5xxCount);

        body.put("cpuUsagePct", cpuUsagePct);
        body.put("liveThreads", liveThreads);

        return ResponseEntity.ok(body);
    }

    // Metric Helpers
    // 태그 리스트
    private List<String> poolTag(String pool) {
        if (pool == null) return null;
        return List.of("pool:" + pool);
    }

    // JVM 메모리 풀(id)별 값을 합쳐서 heap 전체 계산
    private Double sumJvmMemory(String metricName, String area) {
        // 1) base에서 id 목록 가져오기
        var base = metricsEndpoint.metric(metricName, List.of("area:" + area));
        if (base == null || base.getAvailableTags() == null) return null;

        var idTag = base.getAvailableTags().stream()
                .filter(t -> "id".equals(t.getTag()))
                .findFirst().orElse(null);
        if (idTag == null) return firstMeasurementValue(base); // id가 없으면 그냥 base 값 사용

        double sum = 0.0;
        boolean found = false;

        for (String id : idTag.getValues()) {
            var res = metricsEndpoint.metric(metricName, List.of("area:" + area, "id:" + id));
            Double v = valueByStatisticFromDescriptor(res, "VALUE");
            if (v != null) {
                sum += v;
                found = true;
            }
        }
        return found ? sum : null;
    }

    // status 태그 중 5xx만 합산
    private Double sumHttp5xxCount() {
        var base = metricsEndpoint.metric("http.server.requests", null);
        if (base == null || base.getAvailableTags() == null) return 0.0;

        var statusTag = base.getAvailableTags().stream()
                .filter(t -> "status".equals(t.getTag()))
                .findFirst().orElse(null);

        if (statusTag == null) return 0.0;

        double sum = 0.0;
        for (String status : statusTag.getValues()) {
            if (!status.startsWith("5")) continue;
            Double c = valueByStatistic("http.server.requests", "COUNT", List.of("status:" + status));
            if (c != null) sum += c;
        }
        return sum; // 5xx 없으면 0
    }

    // availableTags에서 tagKey의 첫 값만 반환
    private String firstTagValue(String metricName, String tagKey) {
        var base = metricsEndpoint.metric(metricName, null);
        if (base == null || base.getAvailableTags() == null) return null;

        var tag = base.getAvailableTags().stream()
                .filter(t -> tagKey.equals(t.getTag()))
                .findFirst().orElse(null);

        if (tag == null || tag.getValues() == null || tag.getValues().isEmpty()) return null;
        return tag.getValues().iterator().next();
    }

    // measurements의 첫 값
    private Double firstValue(String metricName, List<String> tags) {
        var metric = metricsEndpoint.metric(metricName, tags);
        return firstMeasurementValue(metric);
    }

    private Double firstMeasurementValue(MetricsEndpoint.MetricDescriptor metric) {
        if (metric == null || metric.getMeasurements() == null || metric.getMeasurements().isEmpty()) return null;
        return metric.getMeasurements().get(0).getValue();
    }

    // measurements 중 statistic 이름이 일치하는 값을 선택
    private Double valueByStatistic(String metricName, String statistic, List<String> tags) {
        var metric = metricsEndpoint.metric(metricName, tags);
        return valueByStatisticFromDescriptor(metric, statistic);
    }

    private Double valueByStatisticFromDescriptor(MetricsEndpoint.MetricDescriptor metric, String statistic) {
        if (metric == null || metric.getMeasurements() == null) return null;
        return metric.getMeasurements().stream()
                .filter(m -> m != null && m.getStatistic() != null)
                .filter(m -> statistic.equalsIgnoreCase(m.getStatistic().name()))
                .map(MetricsEndpoint.Sample::getValue)
                .findFirst()
                .orElse(null);
    }

    // 퍼센트 변환
    private Double percent(Double used, Double max) {
        if (used == null || max == null || max == 0.0) return null;
        return (used / max) * 100.0;
    }

    private Double cpuToPct(Double cpuUsage0to1) {
        if (cpuUsage0to1 == null) return null;
        return cpuUsage0to1 * 100.0;
    }

    private Double firstNonNull(Double a, Double b) {
        return a != null ? a : b;
    }
}