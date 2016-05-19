package com.yumu.hexie.service.home;

import com.yumu.hexie.integration.daojia.baojie.BaojieReq;
import com.yumu.hexie.model.localservice.oldversion.YuyueOrder;
import com.yumu.hexie.model.user.User;

/**
 * 保洁服务
 * @author hwb_work
 *
 */
public interface BaojieService {

	/**
	 * 增加预约
	 * @param user
	 * @param req
	 * @param addressId
	 * @return
	 */
	public YuyueOrder addOrder(User user,BaojieReq req,long addressId);
	
}
