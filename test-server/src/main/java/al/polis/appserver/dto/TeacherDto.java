package al.polis.appserver.dto;

import lombok.*;

import java.util.List;

@Data
public class TeacherDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String title;
    private List<CourseDto> courses;
}
