package com.enki.seckillt.redis;
/**
 * redis userKey前缀
 * @author Enki
 * @Version 1.0
 */
public class UserKey extends BasePrefix{

	private UserKey(String prefix) {
		super(prefix);
	}
	public static UserKey getById = new UserKey("id");
	public static UserKey getByName = new UserKey("name");
}
