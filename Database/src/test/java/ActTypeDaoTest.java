import com.itechart.warehouse.dao.ActTypeDao;
import com.itechart.warehouse.entity.Act;
import com.itechart.warehouse.entity.ActType;
import com.itechart.warehouse.exceptions.GenericDAOException;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testContext.xml"})
public class ActTypeDaoTest {
    private ActTypeDao dao;

    private ActType actType;

    @Autowired
    public void setActTypeDao(ActTypeDao actTypeDao) {
        this.dao = actTypeDao;
    }

    @Before
    public void fillActType(){
        actType = new ActType();
        actType.setName("first");
    }

    @Test
    @Transactional
    public void testActTypes() throws SQLException, GenericDAOException{
        actType = dao.insert(actType);
        Optional<ActType> optional = dao.findById(actType.getId());
        ActType type = optional.get();

        Assert.assertEquals("first", type.getName());
    }
}
