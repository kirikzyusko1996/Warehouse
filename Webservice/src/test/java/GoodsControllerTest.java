import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.warehouse.constants.GoodsStatusEnum;
import com.itechart.warehouse.constants.StorageTypeEnum;
import com.itechart.warehouse.constants.UnitEnum;
import com.itechart.warehouse.dto.GoodsDTO;
import com.itechart.warehouse.dto.GoodsSearchDTO;
import com.itechart.warehouse.dto.GoodsStatusDTO;
import com.itechart.warehouse.dto.GoodsStatusSearchDTO;
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test of goods controller.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testContext.xml"})
@WebAppConfiguration
public class GoodsControllerTest {

    @Autowired
    private WebApplicationContext context;


    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testGoodsUpdate() throws Exception {
        GoodsDTO goodsDTO = new GoodsDTO();
        goodsDTO.setName("Test");
        goodsDTO.setStorageTypeName(StorageTypeEnum.FREEZING_CHAMBER.getName());
        goodsDTO.setPrice(new BigDecimal(10));
        goodsDTO.setPriceUnitName(UnitEnum.UNIT_BYN.getName());
        goodsDTO.setQuantity(new BigDecimal(10));
        goodsDTO.setQuantityUnitName(UnitEnum.UNIT_PIECE.getName());
        goodsDTO.setWeight(new BigDecimal(10));
        goodsDTO.setWeightUnitName(UnitEnum.UNIT_KILOGRAM.getName());
        ObjectMapper mapper = new ObjectMapper();
        String jsonGoodsDTO = mapper.writeValueAsString(goodsDTO);
        mockMvc.perform(put("/goods/save/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonGoodsDTO)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UPDATED"));
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testGoodsGet() throws Exception {
        mockMvc.perform(get("/goods/1?page=1&count=10")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testGoodsDelete() throws Exception {
        mockMvc.perform(delete("/goods/delete/1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELETED"));
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testGoodsSave() throws Exception {
        GoodsDTO goodsDTO = new GoodsDTO();
        goodsDTO.setName("Test");
        goodsDTO.setStorageTypeName(StorageTypeEnum.FREEZING_CHAMBER.getName());
        goodsDTO.setPrice(new BigDecimal(10));
        goodsDTO.setPriceUnitName(UnitEnum.UNIT_BYN.getName());
        goodsDTO.setQuantity(new BigDecimal(10));
        goodsDTO.setQuantityUnitName(UnitEnum.UNIT_PIECE.getName());
        goodsDTO.setWeight(new BigDecimal(10));
        goodsDTO.setWeightUnitName(UnitEnum.UNIT_KILOGRAM.getName());
        ObjectMapper mapper = new ObjectMapper();
        String jsonGoodsDTO = mapper.writeValueAsString(goodsDTO);

        mockMvc.perform(post("/goods/1/save")
                .content(jsonGoodsDTO)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testGoodsStatusSetting() throws Exception {
        GoodsStatusDTO statusDTO = new GoodsStatusDTO();
        statusDTO.setStatusName(GoodsStatusEnum.REGISTERED.toString());
        statusDTO.setStatusNote("Some note");
        ObjectMapper mapper = new ObjectMapper();
        String jsonGoodsStatusDTO = mapper.writeValueAsString(statusDTO);

        mockMvc.perform(post("/goods/status/2")
                .content(jsonGoodsStatusDTO)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testGoodsSearch() throws Exception {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");

        GoodsSearchDTO searchDTO = new GoodsSearchDTO();
        searchDTO.setName("Хле");
        searchDTO.setStorageTypeName(StorageTypeEnum.UNHEATED_PLACE.getName());
        searchDTO.setMinPrice(new BigDecimal(1.0));//1.30
        searchDTO.setMaxPrice(new BigDecimal(10.2));
        searchDTO.setMinQuantity(new BigDecimal(10.0));//1000
        searchDTO.setMaxQuantity(new BigDecimal(10000.0));
        searchDTO.setMinWeight(new BigDecimal(10));//500
        searchDTO.setMaxWeight(new BigDecimal(1000));
        searchDTO.setPriceUnitName(UnitEnum.UNIT_BYN.getName());
        searchDTO.setQuantityUnitName(UnitEnum.UNIT_PIECE.getName());
        searchDTO.setWeightUnitName(UnitEnum.UNIT_KILOGRAM.getName());

        searchDTO.setCurrentStatus(GoodsStatusEnum.REGISTERED.toString());//registered, checked,stored

        GoodsStatusSearchDTO statusSearchDTO = new GoodsStatusSearchDTO();
        statusSearchDTO.setStatusName(GoodsStatusEnum.CHECKED.toString());
        statusSearchDTO.setFromDate(new Timestamp(formatter.parseDateTime("11-02-2017").toDate().getTime()));
        statusSearchDTO.setToDate(new Timestamp(formatter.parseDateTime("15-05-2018").toDate().getTime()));
        statusSearchDTO.setUserLastName("Зюсько");
        statusSearchDTO.setUserFirstName("Кирилл");

        GoodsStatusSearchDTO statusSearchDTO2 = new GoodsStatusSearchDTO();
        statusSearchDTO2.setStatusName(GoodsStatusEnum.STORED.toString());
        statusSearchDTO2.setFromDate(new Timestamp(formatter.parseDateTime("11-02-2017").toDate().getTime()));
        statusSearchDTO2.setToDate(new Timestamp(formatter.parseDateTime("15-05-2018").toDate().getTime()));
        statusSearchDTO2.setUserLastName("Зюсько");
        statusSearchDTO2.setUserFirstName("Кирилл");

        List<GoodsStatusSearchDTO> statuses = new ArrayList<>();
        statuses.add(statusSearchDTO);
        statuses.add(statusSearchDTO2);

        searchDTO.setStatuses(statuses);

        ObjectMapper mapper = new ObjectMapper();
        String jsonGoodsSearchDTO = mapper.writeValueAsString(searchDTO);

        mockMvc.perform(get("/goods/search?page=1&count=10")
                .content(jsonGoodsSearchDTO)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testPuttingGoodsIntoCell() throws Exception {
        Long[] cellsArray = new Long[]{Long.valueOf(1), Long.valueOf(2)};
        List<Long> cells = Arrays.asList(cellsArray);
        GoodsDTO goodsDTO = new GoodsDTO();
        goodsDTO.setCells(cells);
        String jsonGoodsDTO = new ObjectMapper().writeValueAsString(goodsDTO);
        mockMvc.perform(put("/goods/2/put")
                .content(jsonGoodsDTO)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UPDATED"));
    }


    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testRemovingGoodsFromStorage() throws Exception {
        mockMvc.perform(put("/goods/remove/3")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UPDATED"));
    }


}
