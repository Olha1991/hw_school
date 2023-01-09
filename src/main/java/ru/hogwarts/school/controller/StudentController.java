package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentsForSQL;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final AvatarService avatarService;

    public StudentController(StudentService studentService, AvatarService avatarService) {
        this.studentService = studentService;
        this.avatarService = avatarService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Long id){
        Student student = studentService.getStudentById(id);
        if(student == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student){
        Student createdStudent = studentService.createStudent(student);
        return ResponseEntity.ok(createdStudent);
    }

    @PutMapping
    public ResponseEntity<Student> updateStudent(@RequestBody Student student){
        Student student1 = studentService.updateStudent(student);
        if (student1 == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student1);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Student> removeStudent(@PathVariable Long id){
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/age")
    public ResponseEntity<Collection<Student>> getStudentsByAge(@RequestParam Integer age) {
        return ResponseEntity.ok(studentService.getStudentsByAge(age));
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> findByAgeBetween(
            @RequestParam Integer min,
            @RequestParam Integer max) {
        return ResponseEntity.ok(studentService.findByAgeBetween(min, max));
    }

    @GetMapping("/faculty/{id}")
    public ResponseEntity<Faculty> getStudentFaculty(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentFaculty(id));
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id, @RequestParam MultipartFile avatar) throws IOException, IOException {
        if (avatar.getSize() > 1024 * 300) {
            return ResponseEntity.badRequest().body("File is too big");
        }
        avatarService.uploadAvatar(id, avatar);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/avatar/preview")
    public ResponseEntity<byte[]> downloadAvatar(@PathVariable Long id) {
        Avatar avatar = avatarService.findAvatar(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
    }

    @GetMapping(value = "/{id}/avatar")
    public void downloadAvatar(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Avatar avatar = avatarService.findAvatar(id);

        Path path = Path.of(avatar.getFilePath());

        try (InputStream is = Files.newInputStream(path);
             OutputStream os = response.getOutputStream();) {
            response.setStatus(200);
            response.setContentType(avatar.getMediaType());
            response.setContentLength((int) avatar.getFileSize());
            is.transferTo(os);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Integer> getAllStudents(){

        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/average-age-of-students")
    public ResponseEntity<Double> getAverageAgeOfStudents(){
        return ResponseEntity.ok(studentService. getAverageAgeOfStudents());
    }

    @GetMapping("/last-students")
    public ResponseEntity<Collection<StudentsForSQL>> getLastStudents(@RequestParam Integer limit) {
        return ResponseEntity.ok(studentService.getLastStudents(limit));
    }

    @GetMapping("/name-starts")
    public ResponseEntity<List<String>> getStudentsByNameStartsWith() {
        return ResponseEntity.ok(studentService.getStudentsByNameStartsWithLetter());
    }

    @GetMapping("/average-age-of-students-stream")
    public ResponseEntity<Double> getAverageAgeOfStudentsStream(){
        return ResponseEntity.ok(studentService.getAverageAgeOfStudentsStream());
    }
}
