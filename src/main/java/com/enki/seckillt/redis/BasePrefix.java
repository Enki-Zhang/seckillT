package com.enki.seckillt.redis;
/**
 * @author Enki
 * @Version 1.0
 */
public abstract class BasePrefix implements KeyPrefix{
	private String prefix;

	public BasePrefix( String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		String className = getClass().getSimpleName();
		return className+":" + prefix;
	}

}
