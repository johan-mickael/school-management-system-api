package com.schoolmanagement.examination.application.command.startattempt;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.examination.application.view.ExamAttemptView;
import com.schoolmanagement.examination.domain.AttemptId;
import com.schoolmanagement.examination.domain.Exam;
import com.schoolmanagement.examination.domain.ExamAttempt;
import com.schoolmanagement.examination.domain.ExamAttemptRepository;
import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.examination.domain.ExamRepository;
import com.schoolmanagement.examination.domain.ExamStatus;
import com.schoolmanagement.examination.domain.exception.AttemptAlreadyExists;
import com.schoolmanagement.examination.domain.exception.ExamNotOpenForAttempt;
import com.schoolmanagement.examination.domain.exception.UserNotLinkedToStudent;
import com.schoolmanagement.iam.domain.User;
import com.schoolmanagement.iam.domain.UserId;
import com.schoolmanagement.iam.domain.UserRepository;
import com.schoolmanagement.student.domain.StudentId;

@Service
public class StartAttemptHandler {
  private final ExamAttemptRepository attempts;
  private final ExamRepository exams;
  private final UserRepository users;

  public StartAttemptHandler(ExamAttemptRepository attempts, ExamRepository exams, UserRepository users) {
    this.attempts = attempts;
    this.exams = exams;
    this.users = users;
  }

  @Transactional
  public ExamAttemptView handle(StartAttemptCommand command) {
    ExamId examId = ExamId.of(command.examId());
    Exam exam = exams.getById(examId);
    if (exam.status() != ExamStatus.OPEN) {
      throw new ExamNotOpenForAttempt(examId);
    }

    User user = users.findById(UserId.of(command.currentUserId()))
        .orElseThrow(() -> new UserNotLinkedToStudent(command.currentUserId()));
    if (user.personId() == null) {
      throw new UserNotLinkedToStudent(command.currentUserId());
    }
    StudentId studentId = new StudentId(user.personId().value());

    if (attempts.findByExamIdAndStudentId(examId, studentId).isPresent()) {
      throw new AttemptAlreadyExists(examId, studentId);
    }

    ExamAttempt attempt = ExamAttempt.start(AttemptId.generate(), examId, studentId);
    attempts.save(attempt);

    return ExamAttemptView.from(attempt);
  }
}
