package al.polis.appserver.mapper;

import al.polis.appserver.dto.CourseDto;
import al.polis.appserver.dto.StudentDto;
import al.polis.appserver.dto.TeacherDto;
import al.polis.appserver.model.Course;
import al.polis.appserver.model.Student;
import al.polis.appserver.model.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    @Mappings({
            @Mapping(target = "teacher", expression = "java(mapTeacher(entity.getTeacher()))"),
            @Mapping(target = "students", expression = "java(mapStudents(entity.getStudents()))")
    })
    CourseDto toDto(Course entity);

    @Mappings({
            @Mapping(target = "teacher", ignore = true),
            @Mapping(target = "students", ignore = true)
    })
    Course toEntity(CourseDto dto);

    List<CourseDto> toDtoList(List<Course> entities);

    List<Course> toEntityList(List<CourseDto> dtos);

    default TeacherDto mapTeacher(Teacher teacher) {
        if (teacher == null) return null;
        TeacherDto dto = new TeacherDto();
        dto.setId(teacher.getId());
        dto.setFirstName(teacher.getFirstName());
        dto.setLastName(teacher.getLastName());
        dto.setTitle(teacher.getTitle());
        dto.setCourses(null); // Avoid circular reference
        return dto;
    }

    default List<StudentDto> mapStudents(List<Student> students) {
        if (students == null) return null;
        return students.stream().map(this::mapStudent).toList();
    }

    default StudentDto mapStudent(Student student) {
        if (student == null) return null;
        StudentDto dto = new StudentDto();
        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setSerialNumber(student.getSerialNumber());
        dto.setCourse(null); // Avoid circular reference
        return dto;
    }
}
