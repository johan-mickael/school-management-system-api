package com.schoolmanagement.course.presentation;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.course.application.command.assignteachertocourse.AssignTeacherToCourseCommand;
import com.schoolmanagement.course.application.command.assignteachertocourse.AssignTeacherToCourseHandler;
import com.schoolmanagement.course.application.command.createcourse.CreateCourseCommand;
import com.schoolmanagement.course.application.command.createcourse.CreateCourseHandler;
import com.schoolmanagement.course.application.query.getcourse.GetCourseHandler;
import com.schoolmanagement.course.application.query.getcourse.GetCourseQuery;
import com.schoolmanagement.course.application.query.listcoursesbypromotion.ListCoursesByPromotionHandler;
import com.schoolmanagement.course.application.query.listcoursesbypromotion.ListCoursesByPromotionQuery;
import com.schoolmanagement.course.application.view.CourseView;
import com.schoolmanagement.course.presentation.dto.AssignTeacherToCourseRequest;
import com.schoolmanagement.course.presentation.dto.CourseResponse;
import com.schoolmanagement.course.presentation.dto.CreateCourseRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "Courses")
public class CourseController {

  private final CreateCourseHandler create;
  private final GetCourseHandler get;
  private final ListCoursesByPromotionHandler listByPromotion;
  private final AssignTeacherToCourseHandler assignTeacher;

  public CourseController(
      CreateCourseHandler create,
      GetCourseHandler get,
      ListCoursesByPromotionHandler listByPromotion,
      AssignTeacherToCourseHandler assignTeacher) {
    this.create = create;
    this.get = get;
    this.listByPromotion = listByPromotion;
    this.assignTeacher = assignTeacher;
  }

  @PostMapping
  public ResponseEntity<CourseResponse> create(@Valid @RequestBody CreateCourseRequest request) {
    CreateCourseCommand createCourseCommand = new CreateCourseCommand(
        request.code(),
        request.title(),
        request.coefficient(),
        request.promotionId(),
        request.teacherId());
    CourseView view = create.handle(createCourseCommand);

    return ResponseEntity
        .created(URI.create("/api/v1/courses/" + view.id()))
        .body(CourseResponse.from(view));
  }

  @GetMapping("/{id}")
  public CourseResponse getOne(@PathVariable String id) {
    return CourseResponse.from(get.handle(new GetCourseQuery(id)));
  }

  @GetMapping
  public List<CourseResponse> getByPromotion(@RequestParam String promotionId) {
    return listByPromotion.handle(new ListCoursesByPromotionQuery(promotionId)).stream()
        .map(CourseResponse::from)
        .toList();
  }

  @PostMapping("/{id}/assign-teacher")
  public CourseResponse assignTeacher(
      @PathVariable String id,
      @Valid @RequestBody AssignTeacherToCourseRequest request) {
    return CourseResponse.from(
        assignTeacher.handle(new AssignTeacherToCourseCommand(id, request.teacherId())));
  }
}
