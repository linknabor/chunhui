package com.yumu.hexie.service.home.impl;

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yumu.hexie.integration.daojia.baojie.BaojieReq;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.localservice.oldversion.YuyueOrder;
import com.yumu.hexie.model.localservice.oldversion.YuyueOrderRepository;
import com.yumu.hexie.model.market.saleplan.YuyueRuleRepository;
import com.yumu.hexie.model.tohome.BaojieOrder;
import com.yumu.hexie.model.tohome.BaojieOrderRepository;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.model.user.AddressRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.home.BaojieService;

@Service("baojieService")
public class BaojieServiceImpl implements BaojieService {
	private static final Logger log = LoggerFactory.getLogger(BaojieServiceImpl.class);
	
	@Inject
	private YuyueRuleRepository yuyueRuleRepository;
	@Inject 
	private BaojieOrderRepository baojieOrderRepository;
	@Inject 
	private AddressRepository addressRepository;
	@Inject
	private YuyueOrderRepository yuyueOrderRepository;

	@Override
	public YuyueOrder addOrder(User user, BaojieReq req, long addressId) {
//		log.error("userid:"+user.getId()+";yuyueRuleId:"+req.getRuleId()+"start");
		Address address = addressRepository.findOne(addressId);
		List<Object> ruleList = yuyueRuleRepository.queryRuleAndProductInfoByRuleId(req.getRuleId());
		Object[] ruleInfo = (Object[])ruleList.get(0);
		YuyueOrder yOrder = new YuyueOrder();
//		log.error("userid:"+user.getId()+";yuyueRuleId:"+req.getRuleId()+";addYuyueOrder_0");
		//创建预约单
		yOrder.setTel(address.getTel());
		yOrder.setProductType(ModelConstant.YUYUE_PRODUCT_TYPE_BAOJIE);
		yOrder.setMerchantId(((BigInteger)ruleInfo[2]).longValue());
		yOrder.setReceiverName(address.getReceiveName());
		yOrder.setAddress(address.getRegionStr()+address.getDetailAddress());
		yOrder.setStatus(ModelConstant.ORDER_STAUS_YUYUE_INIT);
		yOrder.setPaymentType(req.getPaymentType());
		yOrder.setPayStatus(ModelConstant.YUYUE_PAYSTATUS_INIT);
		yOrder.setProductName((String)ruleInfo[3]);//页面和后台都去规则的名称（商品的名称和规则的名称一般情况下是相同的）
		yOrder.setPrice(req.getPrices());
		yOrder.setWorkTime(req.getExpectedTime());
		yOrder.setUserId(user.getId());
		yOrder.setServiceNo(1);//单次服务
		yOrder.setServiceUsedNo(1);
		yOrder = yuyueOrderRepository.save(yOrder);
//		log.error("userid:"+user.getId()+";yuyueRuleId:"+req.getRuleId()+";addYuyueOrder_1");

		//创建服务单
		BaojieOrder order = new BaojieOrder();
		order.setyOrderId(yOrder.getId());
		order.setServiceTypeName((String)ruleInfo[3]);
		order.setUserId(user.getId());
		order.setStrMobile(address.getTel());
		order.setPayStatus(ModelConstant.YUYUE_PAYSTATUS_UNPAYED);
		order.setServiceStatus(ModelConstant.YUYUE_SERVICE_STATUS_UNUSED);
		order.setPrices(req.getPrices());
		order.setExpectedTime(req.getExpectedTime());
		order.setStrName(address.getReceiveName());
		order.setStrWorkAddr(address.getRegionStr()+address.getDetailAddress());
		order.setCustomerMemo(req.getCustomerMemo());
		order = baojieOrderRepository.save(order);			
//		log.error("userid:"+user.getId()+";yuyueRuleId:"+req.getRuleId()+";addServiceOrder_1");


		return yOrder;
	}

}
