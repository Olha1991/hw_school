package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;

@Service
public class FacultyService {

    private final Logger logger = LoggerFactory.getLogger(FacultyService.class);
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository) {
        logger.debug("Requesting constructor FacultyService");
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    public Faculty creatFaculty (Faculty faculty){
        logger.debug("Requesting method createFaculty");
        return facultyRepository.save(faculty);
    }
    public Faculty getFacultyById(long id){
        logger.debug("Requesting method getFacultyById (facultyId = {})", id);
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty updateFaculty(Faculty faculty){
        logger.debug("Requesting method updateFaculty (facultyId = {})", faculty.getId());
        if (facultyRepository.findById(faculty.getId()).orElse(null) == null) {
            return null;
        }
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(Long id){
        logger.debug("Requesting method deleteFaculty (facultyId = {})", id);
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> getFacultiesByColor(String color) {
        logger.debug("Requesting method getFacultiesByColor (color = {})", color);
        return facultyRepository.findByColor(color);
    }

    public Collection<Faculty> findFacultiesByNameOrColor(String searchStr) {
        logger.debug("Requesting method findFacultiesByNameOrColor (searchStr = {})", searchStr);
        return facultyRepository.findByNameContainsIgnoreCaseOrColorIgnoreCase(searchStr, searchStr);
    }

    public Collection<Student> getFacultyStudents(long id) {
        logger.debug("Requesting method getFacultyStudents (facultyId = {})", id);
        Faculty faculty = getFacultyById(id);
        if (faculty == null) {
            return null;
        }
        return studentRepository.findByFacultyId(faculty.getId());
    }

    public String getLongFacultyName() {
        return facultyRepository.findAll().stream()
                .max(Comparator.comparingInt(e -> e.getName().length()))
                .orElseThrow(NotFoundException::new)
                .getName();
    }
}
