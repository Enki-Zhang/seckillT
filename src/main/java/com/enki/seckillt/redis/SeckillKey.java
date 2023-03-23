package com.enki.seckillt.redis;
/**
 * @author Enki
 * @Version 1.0
 */
public class SeckillKey extends BasePrefix{

	private SeckillKey(String prefix) {
		super(prefix);
	}
	public static SeckillKey isGoodsOver = new SeckillKey("go");
	public static SeckillKey getSeckillPath = new SeckillKey("mp");
}
