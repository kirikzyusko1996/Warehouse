import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.warehouse.constants.StorageTypeEnum;
import com.itechart.warehouse.constants.UnitEnum;
import com.itechart.warehouse.dao.InvoiceDAO;
import com.itechart.warehouse.dao.InvoiceStatusDAO;
import com.itechart.warehouse.dto.GoodsDTO;
import com.itechart.warehouse.dto.IncomingInvoiceDTO;
import com.itechart.warehouse.dto.OutgoingInvoiceDTO;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.services.*;
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

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
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
    private InvoiceService invoiceService;

    @Autowired
    private WarehouseCustomerCompanyService customerService;

    @Autowired
    private TransportCompanyService transportService;

    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void readIncomingInvoices() throws Exception {
        mockMvc.perform(get("/invoice/incoming")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$"), hasSize(6)));
    }

    @Test
    public void readOutgoingInvoices() throws Exception {
        mockMvc.perform(get("/invoice/outgoing")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$"), hasSize(4)));
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void saveIncomingInvoice() throws Exception {
        IncomingInvoiceDTO invoice = createIncomingInvoice();
        String invoiceJson = new ObjectMapper().writeValueAsString(invoice);

        mockMvc.perform(post("/invoice/incoming")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invoiceJson))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void saveOutgoingInvoice() throws Exception {
        OutgoingInvoiceDTO invoice = createOutgoingInvoice();
        String invoiceJson = new ObjectMapper().writeValueAsString(invoice);

        mockMvc.perform(post("/invoice/outgoing")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invoiceJson))
                .andDo(print())
                .andExpect(status().isCreated());
    }


    @Test
    @Transactional
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void updateInvoiceStatus() throws Exception {
        mockMvc.perform(put("/invoice/1?status=CHECKED")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
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

    private IncomingInvoiceDTO createIncomingInvoice()
            throws DataAccessException, IllegalParametersException {
        IncomingInvoiceDTO invoice = new IncomingInvoiceDTO();
        invoice.setNumber("1");
        invoice.setIssueDate(today());
        /*invoice.setSupplierCompany(customerService.findWarehouseCustomerCompanyByName("Левоправо"));
        invoice.setTransportCompany(transportService.findTransportCompanyByName("Карты"));
        invoice.setTransportNumber("1000");
        invoice.setTransportName("Volvo");
        invoice.setDriver(createDriver());*/
        invoice.setDescription("dnsivvdv");
        invoice.setGoodsQuantity(new BigDecimal("12.5"));
        invoice.setGoodsEntryCount(4000);
        invoice.setGoodsQuantityUnit(createUnit("м"));
        invoice.setGoodsEntryCountUnit(createUnit("л"));
        invoice.setDispatcher(userService.findUserByLogin("kirikzyusko1996"));
        invoice.setGoods(createGoods());

        return invoice;
    }

    private OutgoingInvoiceDTO createOutgoingInvoice()
            throws DataAccessException, IllegalParametersException {
        OutgoingInvoiceDTO invoice = new OutgoingInvoiceDTO();
        invoice.setNumber("99");
        invoice.setIssueDate(today());
//        invoice.setReceiverCompany(customerService.findWarehouseCustomerCompanyByName("Левоправо"));
//        invoice.setTransportCompany(transportService.findTransportCompanyByName("Карты"));
//        invoice.setTransportNumber("1000");
//        invoice.setTransportName("Volvo");
        //invoice.setDriver(createDriver());
        invoice.setDescription("dnsivvdv");
        invoice.setGoodsQuantity(new BigDecimal("12.5"));
        invoice.setGoodsEntryCount(4000);
        invoice.setGoodsQuantityUnit(createUnit("м"));
        invoice.setGoodsEntryCountUnit(createUnit("л"));
        invoice.setManager(userService.findUserByLogin("kirikzyusko1996"));
        invoice.setGoods(createGoods());

        return invoice;
    }

    private Driver createDriver() throws DataAccessException {
        Driver driver = new Driver();
        driver.setId(2L);

        return driver;
    }

    private Unit createUnit(String name) {
        Unit unit = new Unit();
        unit.setName(name);

        return unit;
    }

    private List<GoodsDTO> createGoods() {
        List<GoodsDTO> goodsList = new ArrayList<>();

        GoodsDTO goodsDTO = new GoodsDTO();
        goodsDTO.setName("Test");
//        goodsDTO.setStorageTypeName(StorageTypeEnum.FREEZING_CHAMBER.getName());
//        goodsDTO.setPrice(new BigDecimal(10));
//        goodsDTO.setPriceUnitName(UnitEnum.UNIT_BYN.getName());
//        goodsDTO.setQuantity(new BigDecimal(10));
//        goodsDTO.setQuantityUnitName(UnitEnum.UNIT_PIECE.getName());
//        goodsDTO.setWeight(new BigDecimal(10));
//        goodsDTO.setWeightUnitName(UnitEnum.UNIT_KILOGRAM.getName());

        goodsList.add(goodsDTO);

        return goodsList;
    }

    private Date today() {
        java.util.Date date = new java.util.Date();
        return new Date(date.getTime());
    }
}
