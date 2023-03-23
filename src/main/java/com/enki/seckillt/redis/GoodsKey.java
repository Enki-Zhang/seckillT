package com.enki.seckillt.redis;
/**
 * @author Enki
 * @Version 1.0
 */
public class GoodsKey extends BasePrefix{

	private GoodsKey(String prefix) {
		super(prefix);
	}
	public static GoodsKey getGoodsList = new GoodsKey("gl");
	public static GoodsKey getGoodsDetail = new GoodsKey("gd");
	public static GoodsKey getSeckillGoodsStock= new GoodsKey( "gs");
}
