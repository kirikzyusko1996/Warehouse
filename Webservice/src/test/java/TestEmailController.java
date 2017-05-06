import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.warehouse.entity.InvoiceStatus;
import com.itechart.warehouse.mail.Template;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test of email controller methods.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testContext.xml"})
@WebAppConfiguration
public class TestEmailController {
    @Autowired
    private WebApplicationContext context;


    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testEmailSend() throws Exception {
        Path fileLocation = Paths.get("C:\\TMP\\images.jpg");
        byte[] data = Files.readAllBytes(fileLocation);
        MockMultipartFile multipartFile = new MockMultipartFile("image.jpg", "images.jpg","image/jpeg", data);
        Template template = new Template();
        ArrayList<Long> userIdList = new ArrayList<>();
        userIdList.add(Long.valueOf(9));
        userIdList.add(Long.valueOf(10));
        template.setReceiverIds(userIdList);
        template.setSubject("Поздравление с днем рождения");
        template.setDate(new Date(LocalDate.now().toDate().getTime()));
        template.setBackgroundColor("#c4c0c0");
        ObjectMapper mapper = new ObjectMapper();
        String jsonTemplate = mapper.writeValueAsString(template);

        mockMvc.perform(fileUpload("/email").file(multipartFile)
                .content(jsonTemplate)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }


}
