package com.fsyy.jpademo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.Repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.NoSuchElementException;
import java.util.Optional;

@SpringBootApplication
@Slf4j
public class JpaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaDemoApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(PersonRepository repository, StudentRepository studentRepository) {
        return args -> {

            Person person = new Person();
            person.setName("John");

            repository.save(person);
            Person saved = repository.findById(person.getId()).orElseThrow(NoSuchElementException::new);
            log.info("saved is : {}", saved);

            Student student = new Student();
            student.setName("Bob");

            studentRepository.save(student);
            Student savedStu = studentRepository.findById(student.getId()).orElseThrow(NoSuchElementException::new);
            log.info("savedStu is : {}", savedStu);
        };
    }

}


@Entity
@Data
class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    // getters and setters omitted for brevity
}

interface PersonRepository extends Repository<Person, Long> {

    Person save(Person person);

    Optional<Person> findById(long id);
}

@Entity
@Data
class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="student_generator")
    private Long id;
    private String name;

    // getters and setters omitted for brevity
}

interface StudentRepository extends Repository<Student, Long> {

    Student save(Student person);

    Optional<Student> findById(long id);
}

