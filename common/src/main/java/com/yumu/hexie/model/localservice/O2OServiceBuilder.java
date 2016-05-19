/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.model.localservice;

import java.math.BigDecimal;
import java.util.Date;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.OrderNoUtil;
import com.yumu.hexie.model.promotion.coupon.Coupon;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.service.o2o.req.CommonBillReq;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: HomeServiceBuilder.java, v 0.1 2016年3月24日 下午5:45:19  Exp $
 */
public class O2OServiceBuilder<T extends BaseO2OService>  {

    private T hs;
    public static <T extends BaseO2OService> O2OServiceBuilder<T> init(Class<T> t){
        O2OServiceBuilder<T> hb = new O2OServiceBuilder<T>();
        try {
            hb.hs = t.newInstance();
            hb.hs.setOrderNo(OrderNoUtil.generateO2OOrderNo());
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }
        return hb;
    }
    public static <T extends BaseO2OService> O2OServiceBuilder<T> init(T hs){
        O2OServiceBuilder<T> hb = new O2OServiceBuilder<T>();
        hb.hs = hs;
        return hb;
    }
    public T getBill() {
        return hs;
    }

    public O2OServiceBuilder<T> initCart(HomeCart cart) {
        if(cart.getItems().size() == 0) {
            return this;
        }
        hs.setItemType(cart.getBaseType());
        hs.setItemId(cart.getItems().get(0).getServiceId());
        hs.setBillLogo(cart.getItems().get(0).getLogo());
        if(cart.getItems().size() == 1) {
            hs.setProjectName(cart.getItems().get(0).getTitle());
        } else {
            hs.setProjectName(cart.getItems().get(0).getTitle()+"等服务");
        }
        hs.setItems(cart.getItems());
        return this;
    }

    public O2OServiceBuilder<T> req(CommonBillReq req) {
        hs.setAddressId(req.getAddressId());
        hs.setCouponId(req.getCouponId());
        hs.setMemo(req.getMemo());
        
        hs.setRequireDate(DateUtil.parse(req.getReqTime(),"yyyy-MM-dd HH:mm"));
        return this;
    }

    public O2OServiceBuilder<T> userId(long userId) {
        hs.setUserId(userId);
        return this;
    }
    public O2OServiceBuilder<T> logo(String logo) {
        hs.setBillLogo(logo);
        return this;
    }
    public O2OServiceBuilder<T> address(Address addr) {
        hs.setAddressId(addr.getId());
        hs.setAddress(addr.getRegionStr()+addr.getDetailAddress());
        hs.setTel(addr.getTel());
        hs.setReceiverName(addr.getReceiveName());
        hs.setXiaoquId(addr.getXiaoquId());
        hs.setXiaoquName(addr.getXiaoquName());
        return this;
    }
    public O2OServiceBuilder<T> operator(ServiceOperator op) {
        hs.setOperatorCompanyName(op.getCompanyName());
        hs.setServiceDate(new Date());
        hs.setOperatorId(op.getId());
        hs.setOperatorName(op.getName());
        hs.setOperatorTel(op.getTel());
        return this;
    }

    public O2OServiceBuilder<T> calculateNoCoupon() {
        BigDecimal amount = new BigDecimal(0);
        for(HomeBillItem item  : hs.getItems()) {
            amount = amount.add(item.getPrice().multiply(BigDecimal.valueOf(item.getCount())));
        }
        hs.setAmount(amount);
        hs.setRealAmount(amount);
        return this;
    }

    public O2OServiceBuilder<T> coupon(Coupon coupon) {
        hs.setCoupon(coupon);
        hs.setCouponId(coupon.getId());
        hs.setRealAmount(hs.getAmount().subtract(BigDecimal.valueOf(coupon.getAmount())));
        hs.setRealAmount(hs.getRealAmount().max(BigDecimal.valueOf(0.01)));
        return this;
    }
    public O2OServiceBuilder<T> status(int status) {
        hs.setStatus(status);
        return this;
    }
}
