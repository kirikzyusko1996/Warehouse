import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.warehouse.entity.WarehouseCustomerCompany;
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

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testContext.xml"})
@WebAppConfiguration
public class WarehouseCustomerCompanyControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void readCustomers() throws Exception {
        mockMvc.perform(get("/customer/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].name", is("Dreamland")));
    }

    @Test
    public void readLastThreeCustomers() throws Exception {
        mockMvc.perform(get("/customer/?page=7")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].name", is("SpaceX")));
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void saveCustomer() throws Exception {
        String customerJson = buildCustomerInJson("Basic");

        mockMvc.perform(post("/customer/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void updateCustomer() throws Exception {
        String customerJson = buildCustomerInJson("Updated");

        mockMvc.perform(put("/customer/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson))
                .andDo(print())
                .andExpect(status().isOk());
    }

    // todo only admin can remove customers
    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void deleteCustomer() throws Exception {
        mockMvc.perform(delete("/customer/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void deleteNonExistedCustomer() throws Exception {
        mockMvc.perform(delete("/customer/100")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private String buildCustomerInJson(String name) throws JsonProcessingException{
        WarehouseCustomerCompany customer = new WarehouseCustomerCompany();
        customer.setName(name);

        return new ObjectMapper().writeValueAsString(customer);
    }
}
