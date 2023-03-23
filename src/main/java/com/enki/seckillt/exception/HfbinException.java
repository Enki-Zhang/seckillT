package com.enki.seckillt.exception;


import com.enki.seckillt.result.CodeMsg;

/**
 * @author Enki
 * @Version 1.0
 */
public class HfbinException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private CodeMsg cm;
	
	public HfbinException(CodeMsg cm) {
		super(cm.toString());
		this.cm = cm;
	}
	public CodeMsg getCm() {
		return cm;
	}

}
