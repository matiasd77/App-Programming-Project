package al.polis.appserver.controller;

import al.polis.appserver.communication.ErrorContext;
import al.polis.appserver.communication.RespSingleDto;
import al.polis.appserver.communication.RespSliceDto;
import al.polis.appserver.dto.*;
import al.polis.appserver.mapper.TeacherMapper;
import al.polis.appserver.service.TeacherService;
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
public class TeacherController {

    private final TeacherService teacherService;
    private final TeacherMapper teacherMapper;

    @PostMapping("/teacher/upsert")
    @ResponseBody
    public ResponseEntity<RespSingleDto<TeacherDto>> upsertTeacher(@RequestBody TeacherDto teacher) {
        log.info("Upsert teacher request received: {}", teacher);
        
        try {
            // Validate input
            if (teacher == null) {
                log.error("Teacher is null");
                return ResponseEntity.badRequest()
                    .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            }
            
            TeacherDto res = teacherService.upsertTeacher(teacher);
            log.info("Teacher upserted successfully with ID: {}", res != null ? res.getId() : "null");
            return ResponseEntity.ok(new RespSingleDto<>(res, ErrorContext.readAndClean()));
            
        } catch (Exception ex) {
            log.error("Error upserting teacher: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
        }
    }

    @PostMapping("/teacher/filter")
    @ResponseBody
    public ResponseEntity<RespSliceDto<TeacherDto>> filterTeachers(@RequestBody SimpleStringFilterDto filter) {
        log.info("Filter teachers request received: {}", filter);
        
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
            
            Slice<TeacherDto> res = teacherService.filterTeachers(filter);
            log.info("Teachers filtered successfully. Found {} teachers", 
                res != null ? res.getContent().size() : 0);
            return ResponseEntity.ok(new RespSliceDto<>(res, ErrorContext.readAndClean()));
            
        } catch (Exception ex) {
            log.error("Error filtering teachers: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespSliceDto<>(null, ErrorContext.readAndClean()));
        }
    }

    @PostMapping("/teacher/delete")
    @ResponseBody
    public ResponseEntity<RespSingleDto<Void>> deleteTeacher(@RequestBody LongIdDto teacherId) {
        log.info("Delete teacher request received: {}", teacherId);
        
        try {
            // Validate input
            if (teacherId == null || teacherId.getId() == null) {
                log.error("Teacher ID is null");
                return ResponseEntity.badRequest()
                    .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            }
            
            teacherService.deleteTeacher(teacherId);
            log.info("Teacher deleted successfully with ID: {}", teacherId.getId());
            return ResponseEntity.ok(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            
        } catch (Exception ex) {
            log.error("Error deleting teacher: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
        }
    }

    @PostMapping("/teacher/get")
    @ResponseBody
    public ResponseEntity<RespSingleDto<TeacherDto>> getTeacher(@RequestBody LongIdDto teacherId) {
        log.info("Get teacher request received: {}", teacherId);
        
        try {
            // Validate input
            if (teacherId == null || teacherId.getId() == null) {
                log.error("Teacher ID is null");
                return ResponseEntity.badRequest()
                    .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
            }
            
            TeacherDto res = teacherService.getTeacher(teacherId);
            log.info("Teacher retrieved successfully with ID: {}", teacherId.getId());
            return ResponseEntity.ok(new RespSingleDto<>(res, ErrorContext.readAndClean()));
            
        } catch (Exception ex) {
            log.error("Error getting teacher: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespSingleDto<>(null, ErrorContext.readAndClean()));
        }
    }
}
