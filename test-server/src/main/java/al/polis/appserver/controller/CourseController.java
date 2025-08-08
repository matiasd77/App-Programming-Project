package al.polis.appserver.controller;

import al.polis.appserver.communication.ErrorContext;
import al.polis.appserver.communication.RespSingleDto;
import al.polis.appserver.communication.RespSliceDto;
import al.polis.appserver.dto.CourseDto;
import al.polis.appserver.dto.CourseTeacherAssocDto;
import al.polis.appserver.dto.LongIdDto;
import al.polis.appserver.dto.SimpleStringFilterDto;
import al.polis.appserver.service.CourseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://localhost:8100", "http://localhost:4200"}, allowCredentials = "false")
@Slf4j
@AllArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/course/upsert")
    @ResponseBody
    public ResponseEntity<RespSingleDto<CourseDto>> upsertCourse(@RequestBody CourseDto course) {
        log.info("Upsert course request received: {}", course);
        
        try {
            // Validate input
            if (course == null) {
                log.error("Course is null");
                return ResponseEntity.badRequest()
                    .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            }
            
            CourseDto res = courseService.upsertCourse(course);
            log.info("Course upserted successfully with ID: {}", res != null ? res.getId() : "null");
            return ResponseEntity.ok(new RespSingleDto<>(res, ErrorContext.readAndClean()));
            
        } catch (Exception ex) {
            log.error("Error upserting course: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
        }
    }

    @PostMapping("/course/filter")
    @ResponseBody
    public ResponseEntity<RespSliceDto<CourseDto>> filterCourses(@RequestBody SimpleStringFilterDto filter) {
        log.info("Filter courses request received: {}", filter);
        
        try {
            // Validate input
            if (filter == null) {
                log.error("Filter is null");
                return ResponseEntity.badRequest()
                    .body(new RespSliceDto<>(null, ErrorContext.readAndClean()));
            }
            
            if (filter.getPagination() == null) {
                log.error("Pagination is null in filter");
                return ResponseEntity.badRequest()
                    .body(new RespSliceDto<>(null, ErrorContext.readAndClean()));
            }
            
            Slice<CourseDto> res = courseService.filterCourses(filter);
            log.info("Courses filtered successfully. Found {} courses", 
                res != null ? res.getContent().size() : 0);
            return ResponseEntity.ok(new RespSliceDto<>(res, ErrorContext.readAndClean()));
            
        } catch (Exception ex) {
            log.error("Error filtering courses: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespSliceDto<>(null, ErrorContext.readAndClean()));
        }
    }

    @PostMapping("/course/delete")
    @ResponseBody
    public ResponseEntity<RespSingleDto<Void>> deleteCourse(@RequestBody LongIdDto courseId) {
        log.info("Delete course request received: {}", courseId);
        
        try {
            // Validate input
            if (courseId == null || courseId.getId() == null) {
                log.error("Course ID is null");
                return ResponseEntity.badRequest()
                    .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            }
            
            courseService.deleteCourse(courseId);
            log.info("Course deleted successfully with ID: {}", courseId.getId());
            return ResponseEntity.ok(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            
        } catch (Exception ex) {
            log.error("Error deleting course: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
        }
    }

    @PostMapping("/course/get")
    @ResponseBody
    public ResponseEntity<RespSingleDto<CourseDto>> getCourse(@RequestBody LongIdDto courseId) {
        log.info("Get course request received: {}", courseId);
        
        try {
            // Validate input
            if (courseId == null || courseId.getId() == null) {
                log.error("Course ID is null");
                return ResponseEntity.badRequest()
                    .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            }
            
            CourseDto res = courseService.getCourse(courseId);
            log.info("Course retrieved successfully with ID: {}", courseId.getId());
            return ResponseEntity.ok(new RespSingleDto<>(res, ErrorContext.readAndClean()));
            
        } catch (Exception ex) {
            log.error("Error getting course: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
        }
    }

    @PostMapping("/associateTeacherToCourse")
    @ResponseBody
    public ResponseEntity<RespSingleDto<Void>> associateTeacherToCourse(@RequestBody CourseTeacherAssocDto assoc) {
        log.info("Associate teacher to course request received: {}", assoc);
        
        try {
            // Validate input
            if (assoc == null) {
                log.error("Association is null");
                return ResponseEntity.badRequest()
                    .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            }
            
            courseService.associateTeacherToCourse(assoc);
            log.info("Teacher associated to course successfully");
            return ResponseEntity.ok(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            
        } catch (Exception ex) {
            log.error("Error associating teacher to course: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
        }
    }

    @PostMapping("/removeTeacherFromCourse")
    @ResponseBody
    public ResponseEntity<RespSingleDto<Void>> removeTeacherFromCourse(@RequestBody CourseTeacherAssocDto assoc) {
        log.info("Remove teacher from course request received: {}", assoc);
        
        try {
            // Validate input
            if (assoc == null) {
                log.error("Association is null");
                return ResponseEntity.badRequest()
                    .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            }
            
            courseService.removeTeacherFromCourse(assoc);
            log.info("Teacher removed from course successfully");
            return ResponseEntity.ok(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            
        } catch (Exception ex) {
            log.error("Error removing teacher from course: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
        }
    }
}
