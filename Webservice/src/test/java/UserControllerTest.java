import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.warehouse.constants.UserRoleEnum;
import com.itechart.warehouse.dto.UserDTO;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test of act controller.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testContext.xml"})
@WebAppConfiguration
public class UserControllerTest {
    @Autowired
    private WebApplicationContext context;


    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testUserDelete() throws Exception {
        mockMvc.perform(delete("/user/delete/10")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELETED"));
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testUserUpdate() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setLastName("TEST");
        userDTO.setLogin("root");
        userDTO.setPassword("root");
//        userDTO.setWarehouseId(Long.valueOf(5));
        String[] rolesArray = new String[]{UserRoleEnum.ROLE_OWNER.toString()};
        List rolesList = Arrays.asList(rolesArray);
        userDTO.setRoles(rolesList);
        ObjectMapper mapper = new ObjectMapper();
        String jsonUserDTO = mapper.writeValueAsString(userDTO);
        mockMvc.perform(put("/user/save/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUserDTO)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UPDATED"));
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testUsersGet() throws Exception {
        mockMvc.perform(get("/user?page=1&count=10")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testUserGet() throws Exception {
        mockMvc.perform(get("/user/2")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isNotEmpty());
    }


    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testUserSave() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setLastName("TEST");
        userDTO.setLogin("root");
        userDTO.setPassword("root");
//        userDTO.setWarehouseId(Long.valueOf(5));
        String[] rolesArray = new String[]{UserRoleEnum.ROLE_OWNER.toString()};
        List rolesList = Arrays.asList(rolesArray);
        userDTO.setRoles(rolesList);
        String jsonUserDTO = new ObjectMapper().writeValueAsString(userDTO);
        mockMvc.perform(post("/user/save")
                .content(jsonUserDTO)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }


}
