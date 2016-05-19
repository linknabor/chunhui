package com.yumu.hexie.web.home;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.daojia.baojie.BaojieReq;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.localservice.oldversion.YuyueOrder;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.home.BaojieService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@Controller(value = "baojieController")
public class BaojieController extends BaseController{
//	private static final Logger Log = LoggerFactory.getLogger(JiuyeController.class);

    @Inject
    private BaojieService service;
    
    /**
     * 创建保洁的预约单和服务单（下一步中需要创建订单：serviceorder）
     * @param req
     * @param user
     * @param addressId
     * @return
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/baojie/createOrder/{addressId}", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<YuyueOrder> createOrder(@RequestBody BaojieReq req,@ModelAttribute(Constants.USER)User user, @PathVariable long addressId) throws Exception {
    	req.setPaymentType(ModelConstant.YUYUE_PAYMENT_TYPE_WEIXIN);//微信支付
    	return BaseResult.successResult(service.addOrder(user, req, addressId));
    }
}

