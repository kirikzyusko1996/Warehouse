import com.itechart.warehouse.dao.ActTypeDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.ActType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testContext.xml"})
public class ActTypeDAOTest {
    private ActTypeDAO dao;

    private ActType actType;

    @Autowired
    public void setActTypeDao(ActTypeDAO actTypeDAO) {
        this.dao = actTypeDAO;
    }

    @Before
    public void fillActType(){
        actType = new ActType();
        actType.setName("first");
    }

    @Test
    @Transactional
    @Ignore
    public void testActTypes() throws SQLException, GenericDAOException{
        actType = dao.insert(actType);
        Optional<ActType> optional = dao.findById(actType.getId());
        ActType type = optional.get();

        Assert.assertEquals("first", type.getName());
    }
}
