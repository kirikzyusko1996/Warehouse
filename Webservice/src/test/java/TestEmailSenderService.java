import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.mail.EmailSenderService;
import com.itechart.warehouse.mail.Template;
import com.itechart.warehouse.service.services.UserService;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;

/**
 * Test of email sender service.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testContext.xml"})
@WebAppConfiguration
public class TestEmailSenderService {

    private EmailSenderService emailSenderService;
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setEmailSenderService(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
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

        emailSenderService.sendEmail(template, multipartFile);

    }


}
