package birthday.reminder.service;

import birthday.reminder.service.entity.BirthDayReminderEntity;
import birthday.reminder.service.repository.BirthDayReminderRepository;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@WebAppConfiguration
@SpringBootTest(classes = BirthDayReminderApplication.class)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class BirthDayReminderApplicationTest {

    private MockMvc mvc;

    @MockBean
    private BirthDayReminderRepository repository;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldCreateBirthDayReminder_havingValidCreateReminderRequest() throws Exception {
        MvcResult mvcResult = sendCreateBirthDayReminderRequest("input/valid-create-reminder-request.json");

        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(201);
        verify(repository).save(Mockito.any(BirthDayReminderEntity.class));
    }

    @Test
    public void shouldReturnBadRequestResponse_whenInvalidRequestReceived() throws Exception {
        MvcResult mvcResult = sendCreateBirthDayReminderRequest("input/invalid-create-reminder-request.json");

        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(400);
        verifyNoInteractions(repository);
    }

    @Test
    public void shouldReturnConflictResponse_whenTryingToCreateReminderThatAlreadyExists() throws Exception {
        when(repository.findByChatIdAndRemindedUserChatId(1, 3)).thenReturn(new BirthDayReminderEntity());

        MvcResult mvcResult = sendCreateBirthDayReminderRequest("input/valid-create-reminder-request-for-conflict.json");

        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(409);
        verify(repository, never()).save(Mockito.any(BirthDayReminderEntity.class));
    }

    @NotNull
    private MvcResult sendCreateBirthDayReminderRequest(String path) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post("/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(loadClassPathFile(path))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
    }

    private String loadClassPathFile(String path) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(path);
        try (InputStream inputStream = classPathResource.getInputStream()) {
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer, Charset.defaultCharset());
            return writer.toString();
        }
    }
}
