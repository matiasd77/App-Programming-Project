package al.polis.appserver.dto;

import lombok.Data;

@Data
public class StudentDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String serialNumber;
    private CourseDto course;
}
