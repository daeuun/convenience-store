package com.store.api.common.util.date;

import java.time.LocalDateTime;

public class Period {
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;

    public Period(LocalDateTime startAt, LocalDateTime endAt) {
        this.startAt = startAt;
        this.endAt = endAt;
        validatePeriod(startAt, endAt);
    }

    /**
     * 종료일 유효성 검사
     *
     * @param start 시작 날짜
     * @param end 종료 날짜
     */
    public static void validatePeriod(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("종료 시간이 시작 시간보다 빠를 수 없습니다.");
        }
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }
}
