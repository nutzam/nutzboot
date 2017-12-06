package io.nutz.demo.simple.bean;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

@PK({ "userId", "orderId" })
@Table("t_order${t}")
public class UserOrder {

	@Column("user_id")
	private int userId;

	@Column("order_id")
	private int orderId;

	@Prev(els = @EL("now()"))
	private Date createTime;

	public UserOrder() {
	}

	public UserOrder(int userId, int orderId) {
		super();
		this.userId = userId;
		this.orderId = orderId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
