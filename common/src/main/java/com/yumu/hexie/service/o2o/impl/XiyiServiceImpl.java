/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.service.o2o.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.integration.wechat.entity.common.JsSign;
import com.yumu.hexie.model.localservice.HomeBillItem;
import com.yumu.hexie.model.localservice.HomeBillItemRepository;
import com.yumu.hexie.model.localservice.HomeCart;
import com.yumu.hexie.model.localservice.HomeServiceConstant;
import com.yumu.hexie.model.localservice.O2OServiceBuilder;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceType;
import com.yumu.hexie.model.localservice.bill.YunXiyiBill;
import com.yumu.hexie.model.localservice.bill.YunXiyiBillRepository;
import com.yumu.hexie.model.payment.PaymentConstant;
import com.yumu.hexie.model.payment.PaymentOrder;
import com.yumu.hexie.model.promotion.PromotionConstant;
import com.yumu.hexie.model.promotion.coupon.Coupon;
import com.yumu.hexie.model.settle.SettleBill;
import com.yumu.hexie.model.settle.SettleConstant;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.o2o.BillAssignService;
import com.yumu.hexie.service.o2o.HomeItemService;
import com.yumu.hexie.service.o2o.OperatorService;
import com.yumu.hexie.service.o2o.XiyiService;
import com.yumu.hexie.service.o2o.req.CommonBillReq;
import com.yumu.hexie.service.payment.PaymentService;
import com.yumu.hexie.service.payment.SettleService;
import com.yumu.hexie.service.sales.impl.BaseOrderServiceImpl;
import com.yumu.hexie.service.user.AddressService;
import com.yumu.hexie.service.user.CouponService;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: XiyiServiceImpl.java, v 0.1 2016年4月11日 上午12:40:35  Exp $
 */
@Service("xiyiService")
public class XiyiServiceImpl implements XiyiService {

    protected static final Logger log = LoggerFactory.getLogger(BaseOrderServiceImpl.class);
    @Inject
    private PaymentService paymentService;
    @Inject
    private YunXiyiBillRepository yunXiyiBillRepository;
    @Inject
    private HomeBillItemRepository homeBillItemRepository;
    @Inject
    private AddressService addressService;
    @Inject
    private CouponService couponService;

    @Inject
    private BillAssignService billAssignService;
    @Inject
    private SettleService settleService;
    @Inject
    private OperatorService operatorService;
    @Inject
    private HomeItemService homeItemService;

    /** 
     * @param req
     * @param cart
     * @return
     * @see com.yumu.hexie.service.o2o.XiyiService#createBill(com.yumu.hexie.vo.CreateOrderReq, com.yumu.hexie.model.localservice.HomeCart)
     */
    @Transactional
    @Override
    public YunXiyiBill createBill(User user,CommonBillReq req, HomeCart cart) {
        Date d = DateUtil.parse(req.getReqTime(),"yyyy-MM-dd HH:mm");
        long time = d.getTime()-System.currentTimeMillis();
        if(time>7*24*3600000 || time < 7200000) {
            throw new BizValidateException("服务时间不支持！");
        }
        O2OServiceBuilder<YunXiyiBill> ob = O2OServiceBuilder.init(YunXiyiBill.class);
        ob.initCart(cart).req(req).calculateNoCoupon();
        ob.userId(user.getId());
        
        Address addr = addressService.queryAddressById(req.getAddressId());
        ob.address(addr);
        Coupon coupon = (req.getCouponId() == null || req.getCouponId() == 0)?null:couponService.findOne(req.getCouponId());
        if(couponService.isAvaible(PromotionConstant.COUPON_ITEM_TYPE_SERVICETYPE,
            HomeServiceConstant.SERVICE_TYPE_XIYI, null, ob.getBill().getAmount().floatValue(), coupon, false)){
            ob.coupon(coupon);
        }
        ob.status(HomeServiceConstant.ORDER_STATUS_CREATE);
        //非抢单模式设置商户ID
        ServiceType baseType = homeItemService.findBaseTypeByItem(cart.getItems().get(0).getServiceId());
        ob.getBill().setMerchantId(baseType.getMerchantId());
        
        YunXiyiBill bill = yunXiyiBillRepository.save(ob.getBill());
        for(HomeBillItem item : ob.getBill().getItems()) {
            item.setBillType(HomeServiceConstant.SERVICE_TYPE_XIYI);
            item.setBillId(bill.getId());
        }
        homeBillItemRepository.save(ob.getBill().getItems());
        log.warn("创建洗衣订单" + bill.getId()); 
        return bill;
    }

    /** 
     * @param bill
     * @return
     * @see com.yumu.hexie.service.o2o.XiyiService#pay(com.yumu.hexie.model.localservice.bill.YunXiyiBill)
     */
    @Override
    public JsSign pay(YunXiyiBill bill, String openId) {
        log.warn("发起支付[BEG]" + bill.getId()); 
        //校验订单状态
        if(!bill.payable()){
            throw new BizValidateException(bill.getId(),"订单状态不可支付，请重新查询确认订单状态！").setError();
        }
        if(bill.getCouponId() != null && bill.getCouponId() != 0) {
            Coupon c = couponService.findOne(bill.getCouponId());
            if(!couponService.lock(bill, c)){
                throw new BizValidateException(bill.getId(),"订单对应的红包已不可使用，请重新下单！").setError();
            }
        }
        
        //获取支付单
        PaymentOrder pay = paymentService.fetchPaymentOrder(bill,openId);
        //发起支付
        bill.pay(pay.getId());
        yunXiyiBillRepository.save(bill);

        log.warn("发起支付[END]" + bill.getId()); 
        return paymentService.requestPay(pay);
    }

    private void paySuccess(YunXiyiBill bill,PaymentOrder pay) {
        log.warn("支付成功[BEG]" + bill.getId()); 
        bill.setStatus(HomeServiceConstant.ORDER_STATUS_PAYED);
        yunXiyiBillRepository.save(bill);
        billAssignService.assignXiyiOrder(bill);
        SettleBill settle = settleService.createSettle(bill, findItems(bill.getId()), pay);
        pay.setSettleId(settle.getId());
        paymentService.save(pay);
        log.warn("支付成功[END]" + bill.getId()); 
    }
    /** 
     * @param payment
     * @see com.yumu.hexie.service.o2o.XiyiService#update4Payment(com.yumu.hexie.model.payment.PaymentOrder)
     */
    @Override
    public void update4Payment(PaymentOrder payment) {
        switch(payment.getStatus()) {
            case PaymentConstant.PAYMENT_STATUS_CANCEL:
            case PaymentConstant.PAYMENT_STATUS_FAIL:
            case PaymentConstant.PAYMENT_STATUS_INIT:
                break;
            case PaymentConstant.PAYMENT_STATUS_SUCCESS:
                YunXiyiBill bill = yunXiyiBillRepository.findOne(payment.getOrderId());
                if(bill.getStatus()==HomeServiceConstant.ORDER_STATUS_CREATE){
                    paySuccess(bill,payment);
                }
                couponService.comsume(bill);
                break;
            default:
                break;
                
        }
    }

    /** 
     * @param billId
     * @see com.yumu.hexie.service.o2o.XiyiService#notifyPayed(long)
     */
    @Async
    @Override
    public void notifyPayed(long billId) {
        log.warn("到家notifyPayed成功[BEG]" + billId); 
        try {
            Thread.sleep(1000);//等待微信端处理完成
        } catch (InterruptedException e) {
        }
        YunXiyiBill bill = yunXiyiBillRepository.findOne(billId);
        if(bill == null || bill.getStatus() != HomeServiceConstant.ORDER_STATUS_CREATE) {
            return;
        }
        PaymentOrder payment = paymentService.queryPaymentOrder(PaymentConstant.TYPE_XIYI_ORDER,billId);
        payment = paymentService.refreshStatus(payment);
        update4Payment(payment);
    }

    /** 
     * @param billId
     * @param operatorId
     * @see com.yumu.hexie.service.o2o.XiyiService#accept(long, long)
     */
    @Override
    public void accept(long billId, long userId) {
        log.warn("洗衣accept[BEG]" + billId); 
        YunXiyiBill bill = yunXiyiBillRepository.findOne(billId);
        if(bill == null || bill.getStatus() != HomeServiceConstant.ORDER_STATUS_PAYED) {
            return;
        }
        bill.setStatus(HomeServiceConstant.ORDER_STATUS_ACCEPT);
        ServiceOperator op = operatorService.findByTypeAndUserId(HomeServiceConstant.SERVICE_TYPE_XIYI, userId);
        if(op != null) {
            O2OServiceBuilder.init(bill).operator(op);
            yunXiyiBillRepository.save(bill);
        }
        //FIXME 发送通知
    }

    @Override
    public void cancel(long billId, long userId) {
        log.warn("洗衣取消[BEG]" + billId); 
        YunXiyiBill bill = yunXiyiBillRepository.findOne(billId);
        checkOwner(userId, bill);
        if(bill == null || bill.getStatus() != HomeServiceConstant.ORDER_STATUS_CREATE) {
            throw new BizValidateException("该订单无法取消！");
        }
        bill.cancelByUser();
        couponService.unlock(bill.getCouponId());
        yunXiyiBillRepository.save(bill);
        log.warn("洗衣取消[END]" + billId); 
    }

    @Override
    public void signed(long billId, long userId) {
        log.warn("洗衣签收[BEG]" + billId); 
        YunXiyiBill bill = yunXiyiBillRepository.findOne(billId);
        checkOwner(userId, bill);
        if(bill == null || bill.getStatus() != HomeServiceConstant.ORDER_STATUS_SERVICED
                || bill.getStatus() != HomeServiceConstant.ORDER_STATUS_BACKED) {
            return;
        }
        bill.signed();
        yunXiyiBillRepository.save(bill);
        log.warn("洗衣取消[END]" + billId); 
    }

    private void checkOwner(long userId, YunXiyiBill bill) {
        if(bill.getUserId() != userId) {
            throw new BizValidateException("无法操作他人订单！");
        }
    }
    /** 
     * @param billId
     * @see com.yumu.hexie.service.o2o.XiyiService#received(long)
     */
    @Override
    public void received(long billId) {
    }

    /** 
     * @param billId
     * @see com.yumu.hexie.service.o2o.XiyiService#serviced(long)
     */
    @Override
    public void serviced(long billId) {
    }

    /** 
     * @param billId
     * @see com.yumu.hexie.service.o2o.XiyiService#sended(long)
     */
    @Override
    public void sended(long billId) {
    }


    @Override
    public List<YunXiyiBill> queryBills(long userId, int page) {
        return yunXiyiBillRepository.findByUserId(userId,new PageRequest(page, 20,new Sort(Direction.DESC, "id"))).getContent();
    }

    /** 
     * @param id
     * @return
     * @see com.yumu.hexie.service.o2o.XiyiService#queryById(long)
     */
    @Override
    public YunXiyiBill queryById(long id) {
        return yunXiyiBillRepository.findOne(id);
    }

    public List<HomeBillItem> findItems(long billId){
        return homeBillItemRepository.findByBillIdAndBillType(billId, HomeServiceConstant.SERVICE_TYPE_XIYI);
    }


    private static final long XIYI_TIMEOUT = 3600000l;
    /** 
     * @param billId
     * @see com.yumu.hexie.service.o2o.XiyiService#timeout(long)
     */
    @Override
    public void timeout(long billId) {
        YunXiyiBill bill = yunXiyiBillRepository.findOne(billId);

        log.warn("洗衣超时[BEG]" + billId);
        PaymentOrder payment = paymentService.queryPaymentOrder(PaymentConstant.TYPE_XIYI_ORDER,billId);
        if(payment != null) {
            payment = paymentService.refreshStatus(payment);
            update4Payment(payment);
        }
        if((payment == null || payment.getStatus() == PaymentConstant.PAYMENT_STATUS_INIT) 
                && bill.getCreateDate() + XIYI_TIMEOUT > System.currentTimeMillis()) {

            log.warn("洗衣超时[BEG]" + billId); 
            paymentService.cancelPayment(PaymentConstant.TYPE_XIYI_ORDER,billId);
            couponService.unlock(bill.getCouponId());
            bill.cancelBySystem();
            yunXiyiBillRepository.save(bill);
            log.warn("洗衣超时[END]" + billId); 
        }
    }

}
