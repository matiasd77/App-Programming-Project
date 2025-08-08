package al.polis.appserver.repo;

import al.polis.appserver.model.Student;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Slice<Student> findByFirstNameContainsOrLastNameContains(
            String firstName,
            String lastName,
            Pageable pageable);
}
