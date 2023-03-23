package com.enki.seckillt.mq;


import com.enki.seckillt.entity.User;

/**
 * @author Enki
 * @Version 1.0
 */
public class SeckillMessage {
	private User user;
	private long goodsId;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(long goodsId) {
		this.goodsId = goodsId;
	}
}
