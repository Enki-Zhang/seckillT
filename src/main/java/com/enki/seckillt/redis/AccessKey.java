package com.enki.seckillt.redis;
/**
 * @author Enki
 * @Version 1.0
 */
public class AccessKey extends BasePrefix{

	private AccessKey( String prefix) {
		super(prefix);
	}
	
	public static AccessKey withExpire = new AccessKey("access");

}
