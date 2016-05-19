/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.service.user.req;

import java.io.Serializable;

/**
 * <pre>
 * 地址请求
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: AddressReq.java, v 0.1 2016年4月28日 下午3:06:58  Exp $
 */
public class AddressReq implements Serializable {

    private static final long serialVersionUID = -9139450274091213994L;
    private Long addrId;
    private long xiaoquId;
    private String tel;
    private String name;
    private String detailAddr;
    public long getXiaoquId() {
        return xiaoquId;
    }
    public void setXiaoquId(long xiaoquId) {
        this.xiaoquId = xiaoquId;
    }
    public String getTel() {
        return tel;
    }
    public void setTel(String tel) {
        this.tel = tel;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDetailAddr() {
        return detailAddr;
    }
    public void setDetailAddr(String detailAddr) {
        this.detailAddr = detailAddr;
    }
    public Long getAddrId() {
        return addrId;
    }
    public void setAddrId(Long addrId) {
        this.addrId = addrId;
    }
    
    
}
