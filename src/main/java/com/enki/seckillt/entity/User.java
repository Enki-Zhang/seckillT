package com.enki.seckillt.entity;

import lombok.*;

import java.util.Date;

/**
 * @author Enki
 * @Version 1.0
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
	private int id;
	private String userName;
	private String phone;
	private String password;
	private String salt;
	private String head;
	private int loginCount;
	private Date registerDate;
	private Date lastLoginDate;
}
