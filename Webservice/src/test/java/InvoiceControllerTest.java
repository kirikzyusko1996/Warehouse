import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.warehouse.dao.InvoiceDAO;
import com.itechart.warehouse.dao.InvoiceStatusDAO;
import com.itechart.warehouse.entity.Goods;
import com.itechart.warehouse.entity.Invoice;
import com.itechart.warehouse.entity.InvoiceStatus;
import com.itechart.warehouse.service.services.GoodsService;
import com.itechart.warehouse.service.services.InvoiceService;
import org.junit.Assert;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testContext.xml"})
@WebAppConfiguration
public class InvoiceControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private InvoiceStatusDAO invoiceStatusDAO;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @Transactional
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void updateInvoiceStatus() throws Exception {
        mockMvc.perform(put("/invoice/1?status=CHECKED")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        InvoiceStatus status = invoiceStatusDAO.findById(1L).get();

        Assert.assertEquals(status.getStatusName().getName(), "CHECKED");
    }

    // todo only warehouse owner can remove invoices
    // throws result as has references on it
    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void deleteInvoice() throws Exception {
        mockMvc.perform(delete("/invoice/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void readGoodsOfInvoice() throws Exception {
        mockMvc.perform(get("/invoice/2/goods"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath(("$[0].name"), is("Молоко")));


    }
}
