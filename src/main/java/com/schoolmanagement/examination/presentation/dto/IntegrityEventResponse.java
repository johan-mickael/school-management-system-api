package com.schoolmanagement.examination.presentation.dto;

import java.time.Instant;

import com.schoolmanagement.examination.application.view.IntegrityEventView;

public record IntegrityEventResponse(String type, Instant occurredAt) {

  public static IntegrityEventResponse from(IntegrityEventView v) {
    return new IntegrityEventResponse(v.type(), v.occurredAt());
  }
}
