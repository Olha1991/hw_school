package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.StudentsForSQL;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        logger.debug("Requesting constructor StudentService");
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student){
        logger.debug("Requesting method createStudent");
        return studentRepository.save(student);
    }

    public Student getStudentById(long id){
        logger.debug("Requesting method getStudentById (studentID = {})", id);
        return studentRepository.findById(id).get();
    }

    public Student updateStudent(Student student){
        logger.debug ("Requesting method updateStudent (studentId = {})", student.getId());
        return studentRepository.save(student);
    }

    public void deleteStudent(long id){
        logger.debug("Requesting method deleteStudent (studentId = {})", id);
        studentRepository.deleteById(id);
    }

    public Collection<Student> getStudentsByAge(int age) {
        logger.debug("Requesting method getStudentsByAge (age = {})", age);
        return studentRepository.findByAge(age);
    }

    public Collection<Student> findByAgeBetween(int min, int max) {
        logger.debug ("Requesting method findByAgeBetween (minAge = {}, maxAge = {})", min, max);
        return studentRepository.findByAgeBetween(min, max);
    }

    public Faculty getStudentFaculty(long id) {
        logger.debug("Requesting method getStudentFaculty(studentID = {})", id);
        Student student = getStudentById(id);
        if (student == null) {
            return null;
        }
        return student.getFaculty();
    }

    public int getAllStudents(){
        logger.debug("Requesting method getAllStudents");
        return studentRepository.getAllStudents();
    }

    public double getAverageAgeOfStudents(){
        logger.debug("Requesting  method getAverageAgeOfStudents");
        return studentRepository.averageAgeOfStudents();
    }

    public Collection<StudentsForSQL> getLastStudents(int limit){
        logger.debug("Requesting method getLastStudents (limit = {})", limit);
        return studentRepository.getLastStudents(limit);
    }

    public List<String> getStudentsByNameStartsWithLetter() {
        return studentRepository.findAll().stream()
                .map(u -> u.getName())
                .filter(s -> s.startsWith("A"))
                .sorted((s1, s2) -> s1.compareTo(s2))
                .map(s -> s.toUpperCase())
                .collect(Collectors.toList());
    }

    public Double getAverageAgeOfStudentsStream(){
        return studentRepository.findAll().stream()
                .mapToDouble(u -> u.getAge())
                .average()
                .orElse(Double.NaN);
    }


}