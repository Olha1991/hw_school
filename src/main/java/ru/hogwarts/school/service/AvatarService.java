package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.FileIsTooBigException;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;


@Service
@Transactional
public class AvatarService {

    private final Logger logger = LoggerFactory.getLogger(AvatarService.class);
    private final int avatarFileSizeLimit = 300;
    @Value("${students.avatar.dir.path}")
    private String avatarsDir;
    private final StudentService studentService;
    private final AvatarRepository avatarRepository;

    public AvatarService(StudentService studentService, AvatarRepository avatarRepository) {
        logger.debug("Requesting constructor AvatarService");
        this.studentService = studentService;
        this.avatarRepository = avatarRepository;
    }
    public void uploadAvatar(long studentId, MultipartFile file) throws IOException{
        logger.debug("Requesting method uploadAvatar(studentId = {})", studentId);
        if (file.getSize() > 1024 * avatarFileSizeLimit) {
            throw new FileIsTooBigException(avatarFileSizeLimit);
        }
        Student student = studentService.getStudentById(studentId);

        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }

        Path filePath =Path.of(avatarsDir,studentId + ". " + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try(InputStream is = file.getInputStream();
            OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
            BufferedInputStream bis = new BufferedInputStream(is, 1024);
            BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ){
            bis.transferTo(bos);
        }

        Avatar avatar = findAvatar(studentId);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(generateImagePreview(filePath));

        avatarRepository.save(avatar);
    }
    private byte[] generateImagePreview(Path filePath) throws IOException {
        try(InputStream is = Files.newInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(is, 1024);
            ByteArrayOutputStream bos = new ByteArrayOutputStream()
        ){
            BufferedImage image = ImageIO.read(bis);

            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();

            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), bos);
            return bos.toByteArray();
        }
    }
    public Avatar findAvatar(Long studentId) {
        logger.debug("Requesting method findAvatar (studentId = {})", studentId);
        return avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    }
    private String getExtension(String fileName) {

        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }



    public Collection<Avatar> getFoundByPage(Integer page, Integer size) {
        logger.debug("Requesting method getFoundByPage (page = {}, size = {})", page, size);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Collection<Avatar> avatars = avatarRepository.findAll(pageRequest).getContent();
        if (avatars.isEmpty()) {
            throw new NotFoundException();
        }
        return avatars;
    }


}
