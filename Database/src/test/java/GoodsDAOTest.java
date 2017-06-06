import com.itechart.warehouse.constants.GoodsStatusEnum;
import com.itechart.warehouse.dao.*;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.maven.artifact.versioning.Restriction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test of DAO classes.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testContext.xml"})
public class GoodsDAOTest {

    private Goods goods;
    private Act act;
    private ActType actType;
    private GoodsStatus goodsStatus;
    private GoodsStatusName goodsStatusName;
    private PriceUnit unit;

    private GoodsDAO goodsDAO;
    private PriceUnitDAO priceUnitDAO;
    private ActDAO actDAO;
    private ActTypeDAO actTypeDAO;
    private GoodsStatusDAO goodsStatusDAO;
    private GoodsStatusNameDAO goodsStatusNameDAO;

    @Autowired
    public void setGoodsDAO(GoodsDAO goodsDAO) {
        this.goodsDAO = goodsDAO;
    }

    @Autowired
    public void setUnitDAO(PriceUnitDAO priceUnitDAO) {
        this.priceUnitDAO = priceUnitDAO;
    }

    @Autowired
    public void setActDAO(ActDAO actDAO) {
        this.actDAO = actDAO;
    }

    @Autowired
    public void setActTypeDAO(ActTypeDAO actTypeDAO) {
        this.actTypeDAO = actTypeDAO;
    }

    @Autowired
    public void setGoodsStatusDAO(GoodsStatusDAO goodsStatusDAO) {
        this.goodsStatusDAO = goodsStatusDAO;
    }

    @Autowired
    public void setGoodsStatusNameDAO(GoodsStatusNameDAO goodsStatusNameDAO) {
        this.goodsStatusNameDAO = goodsStatusNameDAO;
    }

    //    public GoodsDAOTest() {
//        unit = new Unit();
//        unit.setName("кг");
//
//        goods = new Goods();
//        goods.setName("Молоко");
//        goods.setPrice(new BigDecimal(100));
//        goods.setQuantity(new BigDecimal(10));
//        goods.setWeight(new BigDecimal(10));
//
//
//        goodsStatus = new GoodsStatus();
//        goodsStatus.setDate(new Timestamp(new Date().getTime()));
//
//        goodsStatusName = new GoodsStatusName();
//        goodsStatusName.setName("Зарегистрирован");
//
//        act = new Act();
//        act.setDate(new Timestamp(new Date().getTime()));
//
//        actType = new ActType();
//        actType.setName("Акт списания");
//
//    }
//
//    @Test
//    @Transactional
//    public void testGoodsDao() throws GenericDAOException {
//        goodsDAO.insert(goods);
//        Optional<Goods> optional = goodsDAO.findById(goods.getId());
//        Goods fetchedGoods = optional.get();
//        assertEquals("Молоко", fetchedGoods.getName());
//        goods.setName("Хлеб");
//        optional = goodsDAO.findById(goods.getId());
//        assertEquals("Хлеб", optional.get().getName());
//        goodsDAO.delete(goods);
//        fetchedGoods = null;
//        optional = goodsDAO.findById(goods.getId());
//        try {
//            fetchedGoods = optional.get();
//        } catch (Exception e) {
//
//        }
//        assertNull(fetchedGoods);
//    }
//
//    @Test
//    @Transactional
//    public void testUnitDao() throws GenericDAOException {
//        unitDAO.insert(unit);
//        Optional<Unit> optional = unitDAO.findById(unit.getId());
//        Unit fetchedUnit = optional.get();
//        assertEquals("кг", fetchedUnit.getName());
//        unit.setName("литр");
//        optional = unitDAO.findById(unit.getId());
//        fetchedUnit = optional.get();
//        assertEquals("литр", fetchedUnit.getName());
//        unitDAO.delete(unit);
//        optional = unitDAO.findById(unit.getId());
//        try {
//            fetchedUnit = optional.get();
//        } catch (Exception e) {
//
//        }
//        assertNull(fetchedUnit);
//    }
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
//        actTypeDao.create(type);
//        ActType fetchedActType = actTypeDao.findById(type.getId());
//        assertEquals("Акт списания", fetchedActType.getStatus());
//        type.setName("Акт несоответствия");
//        fetchedActType = actTypeDao.findById(type.getId());
//        assertEquals("Акт несоответствия", fetchedActType.getStatus());
//        actTypeDao.delete(type);
//        fetchedActType = actTypeDao.findById(type.getId());
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
//        assertEquals("Зарегистрирован", fetchedGoodsStatusName.getStatus());
//        goodsStatusName.setName("Проверен");
//        fetchedGoodsStatusName = goodsStatusNameDao.findById(goodsStatusName.getId());
//        assertEquals("Проверен", fetchedGoodsStatusName.getStatus());
//        goodsStatusNameDao.delete(goodsStatusName);
//        fetchedGoodsStatusName = goodsStatusNameDao.findById(goodsStatusName.getId());
//        assertNull(fetchedGoodsStatusName);
    }

    @Test
    @Transactional
    @Ignore
    public void testFindByWarehouseIdAndCurrentStatus() throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(GoodsStatusName.class);
        criteria.add(Restrictions.eq("name", GoodsStatusEnum.MOVED_OUT.toString()));
        List<GoodsStatusName> fetchedStatusName = goodsStatusNameDAO.findAll(criteria, -1, 1);
        List<Goods> goods = goodsDAO.findByWarehouseIdAndCurrentStatus(Long.valueOf(3), fetchedStatusName.get(0), 0, 100);
        System.out.print(goods);
        assertTrue(!goods.isEmpty());

    }

    @Test
    @Transactional
    @Ignore
    public void testFindByWarehouseId() throws GenericDAOException {
        List<Goods> goods = goodsDAO.findByWarehouseId(Long.valueOf(3), 0, 100);
        assertTrue(!goods.isEmpty());

    }


    @Test
    @Transactional
    @Ignore
    public void testGetGoodsByIdMethod() throws GenericDAOException {
        Goods goods = goodsDAO.getById(1L);
        assertEquals(goods.getName(), "Молоко");

    }

    @Test
    @Transactional
    public void testGetPriceUnitByName() throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(PriceUnit.class);
        criteria.add(Restrictions.eq("name", "долл"));
        List<PriceUnit> units = priceUnitDAO.findAll(criteria, -1, 1);
        assertTrue(CollectionUtils.isNotEmpty(units));

    }

}
