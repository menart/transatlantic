package express.atc.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import express.atc.backend.integration.cbrf.service.CbrfService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractControllerTest {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected CbrfService cbrfService;

    @SneakyThrows
    protected <T> T loadContent(String fileName, Class<T> tClass) {
        var path = getClass().getClassLoader().getResource(fileName);
        if (path != null) {
            String string = Files.readString(Paths.get(path.toURI()));
            return objectMapper.readValue(string, tClass);
        }
        return null;
    }
}
