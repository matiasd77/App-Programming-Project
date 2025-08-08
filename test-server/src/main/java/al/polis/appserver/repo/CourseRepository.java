package al.polis.appserver.repo;

import al.polis.appserver.model.Course;
import al.polis.appserver.model.Student;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Slice<Course> findByCodeContainsOrTitleContainsOrDescriptionContains(
            String code,
            String title,
            String description,
            Pageable pageable);
}
