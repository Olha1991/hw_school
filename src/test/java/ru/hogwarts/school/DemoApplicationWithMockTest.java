package ru.hogwarts.school;





import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = FacultyController.class)
public class DemoApplicationWithMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private StudentRepository studentRepository;

    @SpyBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getFacultyTest() throws Exception{
        Long id = 1L;
        String name = "Name";
        String color = "Black";

        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);

        when(facultyRepository.findById(id)).thenReturn(Optional.of(faculty));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/faculty/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    public void getFacultiesByColorTest() throws Exception {
        final String COLOR = "Black";

        Long id1 = 1L;
        String name1 = "Name1";
        String color1 = COLOR;
        Long id2 = 2L;
        String name2 = "Name2";
        String color2 = COLOR;

        Faculty faculty1 = new Faculty();
        faculty1.setId(id1);
        faculty1.setName(name1);
        faculty1.setColor(color1);
        Faculty faculty2 = new Faculty();
        faculty2.setId(id2);
        faculty2.setName(name2);
        faculty2.setColor(color2);

        when(facultyRepository.findByColor(COLOR)).thenReturn(Set.of(faculty1, faculty2));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/color")
                        .queryParam("color", COLOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(faculty1, faculty2))));
    }

    @Test
    public void  findFacultiesByColorOrNameTest() throws Exception {
        final String SEARCH_STR = "Name1Test";

        Long id1 = 1L;
        String name1 = "Name1Test";
        String color1 = "Gold";
        Long id2 = 2L;
        String name2 = "Name2Test";
        String color2 = "Black";

        Faculty faculty1 = new Faculty();
        faculty1.setId(id1);
        faculty1.setName(name1);
        faculty1.setColor(color1);
        Faculty faculty2 = new Faculty();
        faculty2.setId(id2);
        faculty2.setName(name2);
        faculty2.setColor(color2);

        when(facultyRepository.findByNameContainsIgnoreCaseOrColorIgnoreCase(SEARCH_STR, SEARCH_STR)).thenReturn(Set.of(faculty1, faculty2));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/color")
                        .queryParam("searchStr", SEARCH_STR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(faculty1, faculty2))));
    }

    @Test
    public void creatFacultyTest() throws Exception {
        Long id = 1L;
        String name = "Name";
        String color = "Black";

        JSONObject facultyObj = new JSONObject();
        facultyObj.put("name", name);
        facultyObj.put("color", color);

        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);

        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
        when(facultyRepository.findById(id)).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyObj.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    public void updateFacultyTest() throws Exception {
        Long id = 1L;
        String oldName = "Name";
        String oldColor = "Black";
        String newName = "NameNew";
        String newColor = "Red";

        JSONObject facultyObj = new JSONObject();
        facultyObj.put("id", id);
        facultyObj.put("name", newName);
        facultyObj.put("color", newColor);

        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(oldName);
        faculty.setColor(oldColor);

        Faculty updatedFaculty = new Faculty();
        updatedFaculty.setId(id);
        updatedFaculty.setName(newName);
        updatedFaculty.setColor(newColor);

        when(facultyRepository.findById(id)).thenReturn(Optional.of(faculty));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(updatedFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyObj.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.color").value(newColor));
    }

    @Test
    public void deleteFacultyTest() throws Exception {
        Long id = 1L;
        String name = "Name";
        String color = "Black";

        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);

        when(facultyRepository.findById(id)).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(facultyRepository, atLeastOnce()).deleteById(id);
    }

}
