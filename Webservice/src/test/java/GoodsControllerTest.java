import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.warehouse.constants.GoodsStatusEnum;
import com.itechart.warehouse.constants.StorageTypeEnum;
import com.itechart.warehouse.constants.UnitEnum;
import com.itechart.warehouse.dto.GoodsDTO;
import com.itechart.warehouse.dto.GoodsSearchDTO;
import com.itechart.warehouse.dto.GoodsStatusDTO;
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
        GoodsSearchDTO searchDTO = new GoodsSearchDTO();
        searchDTO.setName("Test");
        searchDTO.setStorageTypeName(StorageTypeEnum.FREEZING_CHAMBER.getName());
        searchDTO.setPrice(new BigDecimal(10));
        searchDTO.setPriceUnitName(UnitEnum.UNIT_BYN.getName());
        searchDTO.setQuantity(new BigDecimal(10));
        searchDTO.setQuantityUnitName(UnitEnum.UNIT_PIECE.getName());
        searchDTO.setWeight(new BigDecimal(10));
        searchDTO.setWeightUnitName(UnitEnum.UNIT_KILOGRAM.getName());
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
