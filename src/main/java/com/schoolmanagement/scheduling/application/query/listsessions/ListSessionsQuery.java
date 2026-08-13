package com.schoolmanagement.scheduling.application.query.listsessions;

import java.time.LocalDate;

public record ListSessionsQuery(String promotionId, LocalDate date) {
}
