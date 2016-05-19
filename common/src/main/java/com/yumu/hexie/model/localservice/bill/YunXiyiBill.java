/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.model.localservice.bill;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.yumu.hexie.model.localservice.BaseO2OService;
import com.yumu.hexie.model.localservice.HomeServiceConstant;
import com.yumu.hexie.model.payment.PaymentConstant;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: YunXiyiBill.java, v 0.1 2016年3月29日 下午4:03:41  Exp $
 */
@Entity
public class YunXiyiBill extends BaseO2OService {

    private static final long serialVersionUID = -5251807583039360700L;
    
    private String receiveOperator;
    private String receiveOperatorTel;
    private String sendOperatorName;
    private String sendOperatorTel;
    

    public boolean payable(){
        return getStatus() == HomeServiceConstant.ORDER_STATUS_CREATE;
    }
    public void pay(long paymentId){
        setPaymentId(paymentId);
    }

    public void cancelByUser(){
        setStatus(HomeServiceConstant.ORDER_STATUS_CANCELED_USER);
        setCancelTime(new Date());
    }
    
    public void cancelBySystem() {
        setStatus(HomeServiceConstant.ORDER_STATUS_CANCELED_TIMEOUT);
        setCancelTime(new Date());
    }
    
    public void signed(){
        setStatus(HomeServiceConstant.ORDER_STATUS_SIGNED);
        setUserConfirmTime(new Date());
    }

    @Transient
    @Override
    public int getPaymentOrderType() {
        return PaymentConstant.TYPE_XIYI_ORDER;
    }
    @Transient
    @Override
    public int getOrderType() {
        return HomeServiceConstant.SERVICE_TYPE_XIYI;
    }
    public String getReceiveOperator() {
        return receiveOperator;
    }

    public void setReceiveOperator(String receiveOperator) {
        this.receiveOperator = receiveOperator;
    }

    public String getReceiveOperatorTel() {
        return receiveOperatorTel;
    }

    public void setReceiveOperatorTel(String receiveOperatorTel) {
        this.receiveOperatorTel = receiveOperatorTel;
    }

    public String getSendOperatorName() {
        return sendOperatorName;
    }

    public void setSendOperatorName(String sendOperatorName) {
        this.sendOperatorName = sendOperatorName;
    }

    public String getSendOperatorTel() {
        return sendOperatorTel;
    }

    public void setSendOperatorTel(String sendOperatorTel) {
        this.sendOperatorTel = sendOperatorTel;
    }

}
