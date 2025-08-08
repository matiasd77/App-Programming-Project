package al.polis.appserver.mapper;

import al.polis.appserver.dto.CourseDto;
import al.polis.appserver.dto.TeacherDto;
import al.polis.appserver.model.Course;
import al.polis.appserver.model.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    TeacherMapper INSTANCE = Mappers.getMapper(TeacherMapper.class);

    @Mappings({
            @Mapping(target = "courses", expression = "java(mapCourses(entity.getCourses()))")
    })
    TeacherDto toDto(Teacher entity);

    @Mappings({
            @Mapping(target = "courses", ignore = true)
    })
    Teacher toEntity(TeacherDto dto);

    List<TeacherDto> toDtoList(List<Teacher> entities);

    List<Teacher> toEntityList(List<TeacherDto> dtos);

    default List<CourseDto> mapCourses(List<Course> courses) {
        if (courses == null) return null;
        return courses.stream().map(this::mapCourse).toList();
    }

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
