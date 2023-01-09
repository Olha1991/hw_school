package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;


@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable long id) {
        Faculty faculty = facultyService.getFacultyById(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PostMapping
    public Faculty creatFaculty(Faculty faculty) {

        return facultyService.creatFaculty(faculty);
    }

    @PutMapping
    public ResponseEntity<Faculty> updateFaculty(@RequestBody Faculty faculty) {
        return ResponseEntity.ok(facultyService.updateFaculty(faculty));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/color", params = "color")
    public ResponseEntity<Collection<Faculty>> getFacultiesByColor(@RequestParam(required = false) String color) {
        return ResponseEntity.ok(facultyService.getFacultiesByColor(color));
    }


    @GetMapping("/students/{id}")
    public ResponseEntity<Collection<Student>> getFacultyStudents(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.getFacultyStudents(id));
    }

    @GetMapping(value = "/color", params = "searchStr")
    public ResponseEntity<Collection<Faculty>> findFacultiesByColorOrName(@RequestParam(required = false) String searchStr) {
        Collection<Faculty> faculties = facultyService.findFacultiesByNameOrColor(searchStr);
        if (faculties == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculties);
    }

    @GetMapping("/long-faculty-name")
    public ResponseEntity<String> getLongFacultyName(){
        return ResponseEntity.ok(facultyService.getLongFacultyName());
    }

    private final Logger logger = LoggerFactory.getLogger(FacultyController.class);
    @GetMapping("sum")
    public int sum(){
        long time = System.currentTimeMillis();
        Stream.iterate(1, a -> a + 1)
                .limit(1_000_000)
                .parallel()
                .reduce(0, (a, b) -> a + b);
        time = System.currentTimeMillis() - time;
        logger.debug("time = {}", time);
        return (int) time;
    }
}
