import com.itechart.warehouse.dao.ActDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Act;
import com.itechart.warehouse.entity.ActType;
import com.itechart.warehouse.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testContext.xml"})
public class ActDAOTest {
    private ActDAO dao;

    @Autowired
    public void setDao(ActDAO dao){
        this.dao = dao;
    }

    @Test
    @Transactional
    public void testAct() throws GenericDAOException{
        Optional<Act> actOptional = dao.findById(1L);
        Act act = actOptional.get();

        ActType actType = act.getActType();
        Assert.assertEquals(actType.getName(), "Запас");

        User user = act.getUser();
        Assert.assertEquals(user.getFirstName(), "John");
        Assert.assertEquals(user.getLastName(), "Smith");
    }
}
