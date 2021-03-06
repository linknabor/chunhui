package com.yumu.hexie.model.promotion.coupon;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.model.BaseModel;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.promotion.PromotionConstant;
import com.yumu.hexie.model.user.User;
@Entity
public class Coupon extends BaseModel {

	private static final long serialVersionUID = -3121621621439075670L;

	//这两项确定一个现金券
	private long seedId;
	private long userId;
	//这两项随机获取
	private long ruleId;
	
	private boolean empty = false;
	private Long orderId;
	private Date useStartDate;
	private Date expiredDate;//现金券超时日期
	private int status = ModelConstant.COUPON_STATUS_AVAILABLE;//0可用 1已使用 2超时
	
	private Date usedDate;//使用日期

	@Transient
	private boolean selected = false;
	/**冗余信息**/
	@Column(updatable=false)
	private String title;//现金券名称
	@Column(updatable=false)
	private float amount;//现金券金额
	
	private String userHeadImg;
	private String userName;
	
	private int seedType;
	private String seedStr;
	private String couponDesc;
	/**冗余信息**/
	
	/**************现金券适用范围**************/
	private float usageCondition;//最小金额
	private boolean availableForAll = true;//与以下三条互斥
	
	private Long merchantId;
	private Long productId;
	private Integer onSaleType;//特卖商品类型
	
	private int itemType = PromotionConstant.COUPON_ITEM_TYPE_ALL;//全部，商品项，服务项，服务类型
    private Integer unsupportSaleType;
	/**************现金券适用范围**************/

	private String suggestUrl;
	@Transient
	public String getUseStartDateStr(){
		return DateUtil.dtFormat(useStartDate, "yyyy.MM.dd");
	}

	@Transient
	public String getUseEndDateStr(){
		return DateUtil.dtFormat(expiredDate, "yyyy.MM.dd");
	}
	@Transient
	public String getLeftDayDes(){
		if(status == ModelConstant.COUPON_STATUS_USED) {
			return "已使用";
		}
		int days = DateUtil.getDurationDays(System.currentTimeMillis(),expiredDate.getTime());
		if(days<0) {
			return "已过期";
		} else if(days == 0) {
			return "今天到期";
		} else {
			return days+"天后过期";
		}
	}
	public Coupon(){}
	public static Coupon emptyCoupon(CouponSeed seed,User user) {
		Coupon c = new Coupon();
		c.seedId = seed.getId();
		c.seedType = seed.getSeedType();
		c.setEmpty(true);
		c.userId = user.getId();
		c.setUserName(user.getName());
		c.setUserHeadImg(user.getHeadimgurl());
		return c;
	}
	public Coupon(CouponSeed seed,CouponRule rule,User user) {
		seedId = seed.getId();
		seedType = seed.getSeedType();
		
		ruleId = rule.getId();
		
		if(rule.getExpiredDays()>0) {
			useStartDate = new Date();
			expiredDate = DateUtil.addDate(new Date(),rule.getExpiredDays());
		} else {
			useStartDate = rule.getUseStartDate();
			expiredDate = rule.getUseEndDate();
		}
		
		title = rule.getTitle();
		amount = rule.getAmount();
		usageCondition = rule.getUsageCondition();//最小金额
		availableForAll = rule.isAvailableForAll();//与以下三条互斥
		couponDesc = rule.getCouponDesc();
		
		merchantId = rule.getMerchantId();
		productId = rule.getProductId();
		onSaleType = rule.getOnSaleType();//特卖商品类型
		itemType = rule.getItemType();
	    unsupportSaleType = rule.getUnsupportSaleType();
		
		suggestUrl = rule.getSuggestUrl();

		this.userId = user.getId();
		setUserName(user.getName());
		setUserHeadImg(user.getHeadimgurl());
	}

	@Transient
	public void lock(long orderId) {
		this.orderId = orderId;
		this.status = ModelConstant.COUPON_STATUS_LOCKED;
	}
	@Transient
	public void unlock() {
		this.orderId = 0l;
		this.status = ModelConstant.COUPON_STATUS_AVAILABLE;
	}
	@Transient
	public void cousume(long orderId) {
		this.orderId = orderId;
		this.status = ModelConstant.COUPON_STATUS_USED;
		this.usedDate = new Date();
	}
	public void timeout(){
		this.orderId = 0l;
		this.status = ModelConstant.COUPON_STATUS_TIMEOUT;
	}
	public long getSeedId() {
		return seedId;
	}
	public void setSeedId(long seedId) {
		this.seedId = seedId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Date getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public Date getUsedDate() {
		return usedDate;
	}
	public void setUsedDate(Date usedDate) {
		this.usedDate = usedDate;
	}
	public String getUserHeadImg() {
		return userHeadImg;
	}
	public void setUserHeadImg(String userHeadImg) {
		this.userHeadImg = userHeadImg;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public Integer getOnSaleType() {
		return onSaleType;
	}
	public void setOnSaleType(Integer onSaleType) {
		this.onSaleType = onSaleType;
	}
	public Date getUseStartDate() {
		return useStartDate;
	}
	public void setUseStartDate(Date useStartDate) {
		this.useStartDate = useStartDate;
	}
	public Long getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}
	public int getSeedType() {
		return seedType;
	}
	public void setSeedType(int seedType) {
		this.seedType = seedType;
	}
	public boolean isEmpty() {
		return empty;
	}
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getSeedStr() {
		return seedStr;
	}

	public void setSeedStr(String seedStr) {
		this.seedStr = seedStr;
	}

	public String getCouponDesc() {
		return couponDesc;
	}

	public void setCouponDesc(String couponDesc) {
		this.couponDesc = couponDesc;
	}
	public String getCreateDateStr(){
		return DateUtil.dtFormat(new Date(getCreateDate()), "yyyy-MM-dd HH:mm");
	}

	public String getSuggestUrl() {
		return suggestUrl;
	}

	public void setSuggestUrl(String suggestUrl) {
		this.suggestUrl = suggestUrl;
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
