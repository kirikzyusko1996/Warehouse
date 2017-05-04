import com.itechart.warehouse.dao.WarehouseCustomerCompanyDAO;
import com.itechart.warehouse.entity.WarehouseCustomerCompany;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
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
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testContext.xml"})
@WebAppConfiguration
public class WarehouseCustomerCompanyControllerTest {
    private MediaType contentType;
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        this.contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
                MediaType.APPLICATION_JSON.getSubtype(),
                Charset.forName("utf8"));
    }

    @Test
    public void readCustomers() throws Exception {
        mockMvc.perform(get("/customer/")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].name", is("Dreamland")));
    }

    @Test
    public void readLastThreeCustomers() throws Exception {
        mockMvc.perform(get("/customer/?page=7")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].name", is("SpaceX")));
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void saveCustomer() throws Exception {
        WarehouseCustomerCompany customer = new WarehouseCustomerCompany();
        customer.setName("Basic");

        String customerJson = json(customer);

        mockMvc.perform(post("/customer/")
                .contentType(contentType)
                .content(customerJson))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void updateCustomer() throws Exception {
        WarehouseCustomerCompany customer = new WarehouseCustomerCompany();
        customer.setName("Updated");

        String customerJson = json(customer);

        mockMvc.perform(put("/customer/2")
                .contentType(contentType)
                .content(customerJson))
                .andDo(print())
                .andExpect(status().isOk());
    }

    // todo only admin can remove customers
    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void deleteCustomer() throws Exception {
        mockMvc.perform(delete("/customer/1")
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void deleteNonExistedCustomer() throws Exception {
        mockMvc.perform(delete("/customer/100")
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
