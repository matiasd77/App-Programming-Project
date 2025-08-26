package al.polis.appserver.service.impl;

import al.polis.appserver.communication.ErrorContext;
import al.polis.appserver.communication.ServerErrorEnum;
import al.polis.appserver.dto.*;
import al.polis.appserver.exception.TestServerRuntimeException;
import al.polis.appserver.mapper.TeacherMapper;
import al.polis.appserver.model.Course;
import al.polis.appserver.model.Student;
import al.polis.appserver.model.Teacher;
import al.polis.appserver.repo.CourseRepository;
import al.polis.appserver.repo.TeacherRepository;
import al.polis.appserver.service.TeacherService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final TeacherMapper teacherMapper;

    @Override
    @Transactional
    public TeacherDto upsertTeacher(TeacherDto teacher) {
        if (teacher == null) {
            ErrorContext.addStatusMessage(ServerErrorEnum.TEACHER_MISSING);
            throw new TestServerRuntimeException("Teacher is null");
        }
        Teacher entity = teacherMapper.toEntity(teacher);
        Teacher res = teacherRepository.save(entity);
        TeacherDto dto = teacherMapper.toDto(res);
        return dto;
    }

    @Override
    public Slice<TeacherDto> filterTeachers(SimpleStringFilterDto filter) {
        if (filter == null || filter.getPagination() == null) {
            ErrorContext.addStatusMessage(ServerErrorEnum.TEACHER_MISSING);
            throw new TestServerRuntimeException("Filter is null or has no pagination info.");
        }
        String criterion = "";
        Slice<Teacher> teachers = null;
        if (filter.getFilter() == null || filter.getFilter().isEmpty()) {
            teachers = teacherRepository.findAll(PageRequest.of(0, 20));
        } else {
            criterion = filter.getFilter();
            teachers = teacherRepository
                    .findByFirstNameContainsOrLastNameContains(
                            criterion,
                            criterion,
                            PageRequest.of(
                                    filter.getPagination().getPageNumber(),
                                    filter.getPagination().getPageSize()));
        }

        List<TeacherDto> dtos = teachers.stream().map(teacherMapper::toDto).toList();
        Slice<TeacherDto> result = new SliceImpl<>(dtos, teachers.getPageable(), teachers.hasNext());
        return result;
    }

    @Override
    @Transactional
    public void deleteTeacher(LongIdDto teacherId) {
        if (teacherId == null || teacherId.getId() == null) {
            ErrorContext.addStatusMessage(ServerErrorEnum.TEACHER_MISSING);
            throw new TestServerRuntimeException("Teacher id is null or missing");
        }

        // Check if teacher exists
        Teacher teacher = teacherRepository.findById(teacherId.getId()).orElse(null);
        if (teacher == null) {
            ErrorContext.addStatusMessage(ServerErrorEnum.TEACHER_NOT_FOUND);
            throw new TestServerRuntimeException("Teacher with id " + teacherId.getId() + " not found");
        }

        // Check if teacher has any courses by querying the courses table
        List<Course> teacherCourses = courseRepository.findByTeacher_Id(teacherId.getId());
        if (teacherCourses != null && !teacherCourses.isEmpty()) {
            ErrorContext.addStatusMessage(ServerErrorEnum.DELETE_TEACHER_NOT_ALLOWED);
            throw new TestServerRuntimeException("Teacher has " + teacherCourses.size() + " course(s) and cannot be deleted. Please remove all course assignments first.");
        }

        // If we reach here, it's safe to delete the teacher
        try {
            teacherRepository.delete(teacher);
        } catch (Exception ex) {
            log.error("Error deleting teacher with ID {}: {}", teacherId.getId(), ex.getMessage());
            ErrorContext.addStatusMessage(ServerErrorEnum.DELETE_TEACHER_NOT_ALLOWED);
            throw new TestServerRuntimeException("Failed to delete teacher due to database constraint: " + ex.getMessage());
        }
    }

    @Override
    public TeacherDto getTeacher(LongIdDto teacherId) {
        if (teacherId == null || teacherId.getId() == null) {
            ErrorContext.addStatusMessage(ServerErrorEnum.TEACHER_MISSING);
            throw new TestServerRuntimeException("Teacher id is null " + teacherId);
        }

        Teacher teacher = teacherRepository.findById(teacherId.getId()).orElse(null);
        if (teacher == null) {
            ErrorContext.addStatusMessage(ServerErrorEnum.TEACHER_NOT_FOUND);
            throw new TestServerRuntimeException("Teacher id not found " + teacherId);
        }

        TeacherDto res = teacherMapper.toDto(teacher);
        return res;
    }
}
