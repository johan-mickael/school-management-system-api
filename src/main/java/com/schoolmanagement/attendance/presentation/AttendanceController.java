package com.schoolmanagement.attendance.presentation;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.attendance.application.command.justifyattendance.JustifyAttendanceCommand;
import com.schoolmanagement.attendance.application.command.justifyattendance.JustifyAttendanceHandler;
import com.schoolmanagement.attendance.application.command.signattendance.SignAttendanceCommand;
import com.schoolmanagement.attendance.application.command.signattendance.SignAttendanceHandler;
import com.schoolmanagement.attendance.application.query.getsessionattendance.GetSessionAttendanceHandler;
import com.schoolmanagement.attendance.application.query.getsessionattendance.GetSessionAttendanceQuery;
import com.schoolmanagement.attendance.application.query.getstudentattendancehistory.GetStudentAttendanceHistoryHandler;
import com.schoolmanagement.attendance.application.query.getstudentattendancehistory.GetStudentAttendanceHistoryQuery;
import com.schoolmanagement.attendance.presentation.dto.AttendanceRecordResponse;
import com.schoolmanagement.attendance.presentation.dto.JustifyAttendanceRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Attendance")
public class AttendanceController {

  private final SignAttendanceHandler sign;
  private final JustifyAttendanceHandler justify;
  private final GetSessionAttendanceHandler sessionAttendance;
  private final GetStudentAttendanceHistoryHandler studentHistory;

  public AttendanceController(
      SignAttendanceHandler sign,
      JustifyAttendanceHandler justify,
      GetSessionAttendanceHandler sessionAttendance,
      GetStudentAttendanceHistoryHandler studentHistory) {
    this.sign = sign;
    this.justify = justify;
    this.sessionAttendance = sessionAttendance;
    this.studentHistory = studentHistory;
  }

  @PostMapping("/api/v1/sessions/{sessionId}/attendance/sign")
  public AttendanceRecordResponse sign(@PathVariable String sessionId, Authentication authentication) {
    return AttendanceRecordResponse.from(
        sign.handle(new SignAttendanceCommand(sessionId, authentication.getName())));
  }

  @PostMapping("/api/v1/sessions/{sessionId}/attendance/{studentId}/justify")
  public AttendanceRecordResponse justify(
      @PathVariable String sessionId,
      @PathVariable String studentId,
      @Valid @RequestBody JustifyAttendanceRequest request) {
    return AttendanceRecordResponse.from(
        justify.handle(new JustifyAttendanceCommand(sessionId, studentId, request.justification())));
  }

  @GetMapping("/api/v1/sessions/{sessionId}/attendance")
  public List<AttendanceRecordResponse> getSessionAttendance(@PathVariable String sessionId) {
    return sessionAttendance.handle(new GetSessionAttendanceQuery(sessionId)).stream()
        .map(AttendanceRecordResponse::from)
        .toList();
  }

  @GetMapping("/api/v1/students/{studentId}/attendance")
  public List<AttendanceRecordResponse> getStudentAttendance(@PathVariable String studentId) {
    return studentHistory.handle(new GetStudentAttendanceHistoryQuery(studentId)).stream()
        .map(AttendanceRecordResponse::from)
        .toList();
  }
}
