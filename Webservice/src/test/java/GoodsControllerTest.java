import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.itechart.warehouse.constants.StorageTypeEnum;
import com.itechart.warehouse.dto.GoodsDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
            ObjectMapper mapper = new ObjectMapper();
            String jsonGoodsDTO = mapper.writeValueAsString(goodsDTO);
        mockMvc.perform(put("/goods/save/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonGoodsDTO)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }



    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testGoodsFind() throws Exception {
        mockMvc.perform(get("/goods/1?count=10")
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
                .andExpect(status().isNoContent());
    }


    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsService")
    public void testGoodsSave() throws Exception {
        GoodsDTO goodsDTO = new GoodsDTO();
        goodsDTO.setName("Test");
        goodsDTO.setStorageTypeName(StorageTypeEnum.FREEZING_CHAMBER.getName());
        goodsDTO.setPrice(new BigDecimal(10));
        goodsDTO.setPriceUnitName("руб");
        goodsDTO.setQuantity(new BigDecimal(10));
        goodsDTO.setQuantityUnitName("шт");
        goodsDTO.setWeight(new BigDecimal("10"));
        goodsDTO.setWeightUnitName("кг");
        ObjectMapper mapper = new ObjectMapper();
        String jsonGoodsDTO = mapper.writeValueAsString(goodsDTO);

        mockMvc.perform(post("/goods/1/save")
                .content(jsonGoodsDTO)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isCreated());
    }


}
