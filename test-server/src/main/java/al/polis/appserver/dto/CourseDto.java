package al.polis.appserver.dto;

import lombok.*;

import java.util.List;

@Data
public class CourseDto {
    private Long id;
    private String code;
    private String title;
    private String description;
    private Integer year;
    private TeacherDto teacher;
    private List<StudentDto> students;
}
