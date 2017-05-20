import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.warehouse.constants.ActTypeEnum;
import com.itechart.warehouse.dto.ActDTO;
import com.itechart.warehouse.dto.ActSearchDTO;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Timestamp;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test of act controller.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testContext.xml"})
@WebAppConfiguration
public class ActControllerTest {
    @Autowired
    private WebApplicationContext context;


    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testActGet() throws Exception {
        mockMvc.perform(get("/act?page=1&count=10")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testActUpdate() throws Exception {
        ActDTO actDTO = new ActDTO();
        Long[] idArray = new Long[]{Long.valueOf(5), Long.valueOf(7)};
        actDTO.setGoodsIdList(Arrays.asList(idArray));
        actDTO.setType(ActTypeEnum.ACT_OF_LOSS.toString());
        ObjectMapper mapper = new ObjectMapper();
        String jsonActDTO = mapper.writeValueAsString(actDTO);
        mockMvc.perform(put("/act/save/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonActDTO)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UPDATED"));
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testActDelete() throws Exception {
        mockMvc.perform(delete("/act/delete/3")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELETED"));
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testActSave() throws Exception {
        ActDTO actDTO = new ActDTO();
        actDTO.setType(ActTypeEnum.ACT_OF_THEFT.toString());
        Long[] idArray = new Long[]{Long.valueOf(5), Long.valueOf(7)};
        actDTO.setGoodsIdList(Arrays.asList(idArray));
        String jsonActDTO = new ObjectMapper().writeValueAsString(actDTO);

        mockMvc.perform(post("/act/save")
                .content(jsonActDTO)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }


    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testActSearch() throws Exception {
        ActSearchDTO searchDTO = new ActSearchDTO();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        searchDTO.setFromDate(new Timestamp(formatter.parseDateTime("10-05-2007").toDate().getTime()));
        searchDTO.setToDate(new Timestamp(formatter.parseDateTime("10-10-2017").toDate().getTime()));
        searchDTO.setType(ActTypeEnum.WRITE_OFF_ACT.toString());
        searchDTO.setCreatorFirstName("Кири");
        searchDTO.setCreatorLastName("Зюськ");
        searchDTO.setCreatorPatronymic("Дмитри");


        ObjectMapper mapper = new ObjectMapper();
        String jsonGoodsSearchDTO = mapper.writeValueAsString(searchDTO);

        mockMvc.perform(get("/act/search?page=1&count=10")
                .content(jsonGoodsSearchDTO)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$").isNotEmpty());
    }


}
