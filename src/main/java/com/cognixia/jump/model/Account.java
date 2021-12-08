package com.cognixia.jump.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;

@Entity
public class Account implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static enum AccountType {
		CHECKING, SAVING, CREDIT
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Min(0)
	@Column(columnDefinition = "integer default 0")
	private Integer balance;
	
	// When type column created, will save enum as a String ( VARCHAR(255) )
	@Enumerated(EnumType.STRING)
	private AccountType type;
	
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;
	
	public Account() {
		this(-1L, 0, AccountType.CHECKING);
	}

	public Account(Long id, @Min(0) Integer balance, AccountType type) {
		super();
		this.id = id;
		this.balance = balance;
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}

	public AccountType getType() {
		return type;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

	// only need setter to make sure link between account and customer occurs (establish foreign key)
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", balance=" + balance + ", type=" + type + "]";
	}
	
	

}
