import com.itechart.warehouse.dao.*;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit test of DAO classes.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testContext.xml"})
public class GoodsDaoTest {

    private Goods goods;
    private Act act;
    private ActType actType;
    private GoodsStatus goodsStatus;
    private GoodsStatusName goodsStatusName;
    private Unit unit;

    private GoodsDao goodsDao;
    private UnitDao unitDao;
    private ActDao actDao;
    private ActTypeDao actTypeDao;
    private GoodsStatusDao goodsStatusDao;
    private GoodsStatusNameDao goodsStatusNameDao;

    @Autowired
    public void setGoodsDao(GoodsDao goodsDao) {
        this.goodsDao = goodsDao;
    }

    @Autowired
    public void setUnitDao(UnitDao unitDao) {
        this.unitDao = unitDao;
    }

    @Autowired
    public void setActDao(ActDao actDao) {
        this.actDao = actDao;
    }

    @Autowired
    public void setActTypeDao(ActTypeDao actTypeDao) {
        this.actTypeDao = actTypeDao;
    }

    @Autowired
    public void setGoodsStatusDao(GoodsStatusDao goodsStatusDao) {
        this.goodsStatusDao = goodsStatusDao;
    }

    @Autowired
    public void setGoodsStatusNameDao(GoodsStatusNameDao goodsStatusNameDao) {
        this.goodsStatusNameDao = goodsStatusNameDao;
    }

    public GoodsDaoTest() {
        unit = new Unit();
        unit.setName("кг");

        goods = new Goods();
        goods.setName("Молоко");
        goods.setPrice(new BigDecimal(100));
        goods.setQuantity(new BigDecimal(10));
        goods.setWeight(new BigDecimal(10));


        goodsStatus = new GoodsStatus();
        goodsStatus.setDate(new Timestamp(new Date().getTime()));

        goodsStatusName = new GoodsStatusName();
        goodsStatusName.setName("Зарегистрирован");

        act = new Act();
        act.setDate(new Timestamp(new Date().getTime()));

        actType = new ActType();
        actType.setName("Акт списания");

    }

    @Test
    @Transactional
    public void testGoodsDao() throws GenericDAOException {
        goodsDao.insert(goods);
        Optional<Goods> optional = goodsDao.findById(goods.getId());
        Goods fetchedGoods = optional.get();
        assertEquals("Молоко", fetchedGoods.getName());
        goods.setName("Хлеб");
        optional = goodsDao.findById(goods.getId());
        assertEquals("Хлеб", optional.get().getName());
        goodsDao.delete(goods);
        fetchedGoods = null;
        optional = goodsDao.findById(goods.getId());
        try {
            fetchedGoods = optional.get();
        } catch (Exception e) {

        }
        assertNull(fetchedGoods);
    }

    @Test
    @Transactional
    public void testUnitDao() throws GenericDAOException {
        unitDao.insert(unit);
        Optional<Unit> optional = unitDao.findById(unit.getId());
        Unit fetchedUnit = optional.get();
        assertEquals("кг", fetchedUnit.getName());
        unit.setName("литр");
        optional = unitDao.findById(unit.getId());
        fetchedUnit = optional.get();
        assertEquals("литр", fetchedUnit.getName());
        unitDao.delete(unit);
        optional = unitDao.findById(unit.getId());
        try {
            fetchedUnit = optional.get();
        } catch (Exception e) {

        }
        assertNull(fetchedUnit);
    }

    @Test
    @Transactional
    public void testActDao() {
//        actDao.create(act);
//        Act fetchedAct = actDao.findById(act.getId());
//        assertEquals(act.getDate(), fetchedAct.getDate());
//        act.setDate(new Timestamp(new Date().getTime()));
//        fetchedAct = actDao.findById(act.getId());
//        assertEquals(act.getDate(), fetchedAct.getDate());
//        actDao.delete(act);
//        fetchedAct = actDao.findById(act.getId());
//        assertNull(fetchedAct);
    }

    @Test
    @Transactional
    public void testActTypeDao() {
//        actTypeDao.create(actType);
//        ActType fetchedActType = actTypeDao.findById(actType.getId());
//        assertEquals("Акт списания", fetchedActType.getName());
//        actType.setName("Акт несоответствия");
//        fetchedActType = actTypeDao.findById(actType.getId());
//        assertEquals("Акт несоответствия", fetchedActType.getName());
//        actTypeDao.delete(actType);
//        fetchedActType = actTypeDao.findById(actType.getId());
//        assertNull(fetchedActType);
    }

    @Test
    @Transactional
    public void testGoodsStatusDao() {
//        goodsStatusDao.create(goodsStatus);
//        GoodsStatus fetchedGoodsStatus = goodsStatusDao.findById(goodsStatus.getId());
//        assertEquals(goodsStatus.getDate(), fetchedGoodsStatus.getDate());
//        goodsStatus.setDate(new Timestamp(new Date().getTime()));
//        fetchedGoodsStatus = goodsStatusDao.findById(goodsStatus.getId());
//        assertEquals(goodsStatus.getDate(), fetchedGoodsStatus.getDate());
//        goodsStatusDao.delete(goodsStatus);
//        fetchedGoodsStatus = goodsStatusDao.findById(goodsStatus.getId());
//        assertNull(fetchedGoodsStatus);
    }

    @Test
    @Transactional
    public void testGoodsStatusNameDao() {
//        goodsStatusNameDao.create(goodsStatusName);
//        GoodsStatusName fetchedGoodsStatusName = goodsStatusNameDao.findById(goodsStatusName.getId());
//        assertEquals("Зарегистрирован", fetchedGoodsStatusName.getName());
//        goodsStatusName.setName("Проверен");
//        fetchedGoodsStatusName = goodsStatusNameDao.findById(goodsStatusName.getId());
//        assertEquals("Проверен", fetchedGoodsStatusName.getName());
//        goodsStatusNameDao.delete(goodsStatusName);
//        fetchedGoodsStatusName = goodsStatusNameDao.findById(goodsStatusName.getId());
//        assertNull(fetchedGoodsStatusName);
    }
}
