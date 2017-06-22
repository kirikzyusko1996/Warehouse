package service;

import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.service.services.WarehouseCompanyService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testContext.xml"})
public class WarehouseCompanyServiceTest {
    @Autowired
    public WarehouseCompanyService service;

    @Test
    public void findAllThatUsedForPeriod() throws Exception{
        Date startDate = Date.valueOf("2017-01-21");
        Date dueDate = Date.valueOf("2017-03-02");

        Set<WarehouseCompany> companies = service.findAllThatUsedForPeriod(startDate, dueDate);

        Assert.assertEquals(companies.size(), 2);
    }
}
