package al.polis.appserver.controller;

import al.polis.appserver.communication.ErrorContext;
import al.polis.appserver.communication.RespSingleDto;
import al.polis.appserver.communication.RespSliceDto;
import al.polis.appserver.dto.*;
import al.polis.appserver.service.StudentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:8100", "http://localhost:4200"}, allowCredentials = "false")
@AllArgsConstructor
@Slf4j
public class StudentController {
    private final StudentService studentService;

    @GetMapping("/health")
    @ResponseBody
    public ResponseEntity<String> health() {
        log.info("Health check endpoint called");
        return ResponseEntity.ok("Backend is running!");
    }

    @PostMapping("/student/upsert")
    @ResponseBody
    public ResponseEntity<RespSingleDto<StudentDto>> upsertStudent(@RequestBody StudentDto student) {
        log.info("Upsert student request received: {}", student);
        
        try {
            // Validate input
            if (student == null) {
                log.error("Student is null");
                return ResponseEntity.badRequest()
                    .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            }
            
            StudentDto res = studentService.upsertStudent(student);
            log.info("Student upserted successfully with ID: {}", res != null ? res.getId() : "null");
            return ResponseEntity.ok(new RespSingleDto<>(res, ErrorContext.readAndClean()));
            
        } catch (Exception ex) {
            log.error("Error upserting student: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
        }
    }

    @PostMapping("/student/filter")
    @ResponseBody
    public ResponseEntity<RespSliceDto<StudentDto>> filterStudents(@RequestBody SimpleStringFilterDto filter) {
        log.info("Filter students request received: {}", filter);
        
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
            
            Slice<StudentDto> res = studentService.filterStudents(filter);
            log.info("Students filtered successfully. Found {} students", 
                res != null ? res.getContent().size() : 0);
            return ResponseEntity.ok(new RespSliceDto<>(res, ErrorContext.readAndClean()));
            
        } catch (Exception ex) {
            log.error("Error filtering students: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespSliceDto<>(null, ErrorContext.readAndClean()));
        }
    }

    @DeleteMapping("/student/{id}")
    public ResponseEntity<RespSingleDto<Void>> deleteStudent(@PathVariable Long id) {
        log.info("Delete student request received for ID: {}", id);
        
        try {
            // Validate input
            if (id == null) {
                log.error("Student ID is null");
                return ResponseEntity.badRequest()
                    .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            }
            
            LongIdDto studentId = new LongIdDto();
            studentId.setId(id);
            
            studentService.deleteStudent(studentId);
            log.info("Student deleted successfully with ID: {}", id);
            return ResponseEntity.ok(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            
        } catch (Exception ex) {
            log.error("Error deleting student: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
        }
    }

    @PostMapping("/associateStudentToCourse")
    @ResponseBody
    public ResponseEntity<RespSingleDto<Void>> associateStudentToCourse(@RequestBody CourseStudentAssocDto assoc) {
        log.info("Associate student to course request received: {}", assoc);
        
        try {
            // Validate input
            if (assoc == null) {
                log.error("Association is null");
                return ResponseEntity.badRequest()
                    .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            }
            
            studentService.associateStudentToCourse(assoc);
            log.info("Student associated to course successfully");
            return ResponseEntity.ok(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            
        } catch (Exception ex) {
            log.error("Error associating student to course: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
        }
    }

    @PostMapping("/removeStudentFromCourse")
    @ResponseBody
    public ResponseEntity<RespSingleDto<Void>> removeStudentFromCourse(@RequestBody CourseStudentAssocDto assoc) {
        log.info("Remove student from course request received: {}", assoc);
        
        try {
            // Validate input
            if (assoc == null) {
                log.error("Association is null");
                return ResponseEntity.badRequest()
                    .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            }
            
            studentService.removeStudentFromCourse(assoc);
            log.info("Student removed from course successfully");
            return ResponseEntity.ok(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            
        } catch (Exception ex) {
            log.error("Error removing student from course: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
        }
    }

    @PostMapping("/student/get")
    @ResponseBody
    public ResponseEntity<RespSingleDto<StudentDto>> getStudent(@RequestBody LongIdDto studentId) {
        log.info("Get student request received: {}", studentId);
        
        try {
            // Validate input
            if (studentId == null || studentId.getId() == null) {
                log.error("Student ID is null");
                return ResponseEntity.badRequest()
                    .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            }
            
            StudentDto res = studentService.getStudent(studentId);
            log.info("Student retrieved successfully with ID: {}", studentId.getId());
            return ResponseEntity.ok(new RespSingleDto<>(res, ErrorContext.readAndClean()));
            
        } catch (Exception ex) {
            log.error("Error getting student: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
        }
    }
}
