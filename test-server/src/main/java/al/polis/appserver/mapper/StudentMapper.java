package al.polis.appserver.mapper;

import al.polis.appserver.dto.CourseDto;
import al.polis.appserver.dto.StudentDto;
import al.polis.appserver.model.Course;
import al.polis.appserver.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

    @Mappings({
            @Mapping(target = "course", expression = "java(mapCourse(entity.getCourse()))")
    })
    StudentDto toDto(Student entity);

    @Mappings({
            @Mapping(target = "course", ignore = true)
    })
    Student toEntity(StudentDto dto);

    List<StudentDto> toDtoList(List<Student> entities);

    List<Student> toEntityList(List<StudentDto> dtos);

    default CourseDto mapCourse(Course course) {
        if (course == null) return null;
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setCode(course.getCode());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setYear(course.getYear());
        dto.setTeacher(null); // Avoid circular reference
        dto.setStudents(null); // Avoid circular reference
        return dto;
    }
}
