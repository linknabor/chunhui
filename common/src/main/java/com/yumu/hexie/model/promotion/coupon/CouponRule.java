package com.yumu.hexie.model.promotion.coupon;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.springframework.beans.BeanUtils;

import com.yumu.hexie.model.BaseModel;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.promotion.PromotionConstant;

/**
 * <pre>
 * 现金券使用规则
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: CouponRule.java, v 0.1 2016年4月11日 上午10:17:16  Exp $
 */
@Entity
public class CouponRule extends BaseModel {

	private static final long serialVersionUID = -5765862603557177494L;

    private long seedId;

    /** 基础信息 **/
	private int status = ModelConstant.COUPON_RULE_STATUS_AVAILABLE;//1有效 2无效
	
	private String title;
    @Column(updatable=false)
    private float amount;//现金券金额,金额设定即不可修改
    private String couponDesc;
    private String suggestUrl;

    /** 基础信息 **/

    /** 汇总信息 **/
    private Integer receivedCount = 0;
    private Integer usedCount = 0;
    /** 汇总信息 **/

    /**************现金券适用范围**************/
    private float usageCondition;//最小金额
    private boolean availableForAll = true;//与以下三条互斥
    
    private int itemType = PromotionConstant.COUPON_ITEM_TYPE_ALL;//全部，商品项，服务项，服务类型
    private Long productId;
    private Long merchantId;
    private Integer onSaleType;//特卖商品类型
    private Integer unsupportSaleType;
    /**************现金券适用范围**************/

    /**************使用时间**************/
    private int expiredDays;//现金券超时天数
    
    private Date useStartDate;
    private Date useEndDate;

    /**************使用时间**************/

    


    /** 发放 **/
	private int totalCount = 0;
	private Date startDate;//发放时间
	private Date endDate;//发放时间
    /** 发放 **/
	


	public CouponRule copy(long seedId){
		CouponRule cr = new CouponRule();
		BeanUtils.copyProperties(this, cr);
		cr.setId(0);
		cr.setSeedId(seedId);
		return cr;
	}
	@Transient
	public boolean isValid(){
		return status == ModelConstant.COUPON_RULE_STATUS_AVAILABLE;
	}
	@Transient
	public void invalid(){
		status = ModelConstant.COUPON_RULE_STATUS_INVALID;
	}
	@Transient
	public Long getParentId(){
		return seedId;
	}
	@Transient
	public float getTotalAmount(){
		return getAmount() * totalCount;
	}
	@Transient
	public float getUsedAmount(){
		return getAmount() * usedCount;
	}

	@Transient
	public float getReceivedAmount(){
		return getAmount() * receivedCount;
	}
	@Transient
	public void addReceived(){
		receivedCount ++;
	}
	@Transient
	public void addUsed(){
		usedCount ++;
	}
	@Transient
	public void update(CouponRule rule) {
		setStatus(rule.getStatus());
		setTotalCount(rule.getTotalCount());
		
		setExpiredDays(rule.getExpiredDays());
    	setStartDate(rule.getStartDate());
    	setEndDate(rule.getEndDate());
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getSeedId() {
		return seedId;
	}
	public void setSeedId(long seedId) {
		this.seedId = seedId;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getReceivedCount() {
		return receivedCount;
	}
	public void setReceivedCount(int receivedCount) {
		this.receivedCount = receivedCount;
	}
	public int getUsedCount() {
		return usedCount;
	}
	public void setUsedCount(int usedCount) {
		this.usedCount = usedCount;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getExpiredDays() {
		return expiredDays;
	}

	public void setExpiredDays(int expiredDays) {
		this.expiredDays = expiredDays;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getUseStartDate() {
		return useStartDate;
	}
	public void setUseStartDate(Date useStartDate) {
		this.useStartDate = useStartDate;
	}
	public Date getUseEndDate() {
		return useEndDate;
	}
	public void setUseEndDate(Date useEndDate) {
		this.useEndDate = useEndDate;
	}
    public float getAmount() {
        return amount;
    }
    public void setAmount(float amount) {
        this.amount = amount;
    }
    public String getCouponDesc() {
        return couponDesc;
    }
    public void setCouponDesc(String couponDesc) {
        this.couponDesc = couponDesc;
    }
    public String getSuggestUrl() {
        return suggestUrl;
    }
    public void setSuggestUrl(String suggestUrl) {
        this.suggestUrl = suggestUrl;
    }
    public float getUsageCondition() {
        return usageCondition;
    }
    public void setUsageCondition(float usageCondition) {
        this.usageCondition = usageCondition;
    }
    public boolean isAvailableForAll() {
        return availableForAll;
    }
    public void setAvailableForAll(boolean availableForAll) {
        this.availableForAll = availableForAll;
    }
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public Long getMerchantId() {
        return merchantId;
    }
    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
    public Integer getOnSaleType() {
        return onSaleType;
    }
    public void setOnSaleType(Integer onSaleType) {
        this.onSaleType = onSaleType;
    }
    public void setReceivedCount(Integer receivedCount) {
        this.receivedCount = receivedCount;
    }
    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }
    public int getItemType() {
        return itemType;
    }
    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
    public Integer getUnsupportSaleType() {
        return unsupportSaleType;
    }
    public void setUnsupportSaleType(Integer unsupportSaleType) {
        this.unsupportSaleType = unsupportSaleType;
    }
	
}
