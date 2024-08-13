package com.user.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Data
@Table(name = "USER_MASTER")
@Entity
public class UserMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;
	
	private String fullName;
	
	private String email
	; 
	private Long mobile;
	
	private String gender;
	
	private LocalDate dob;
	
	private Long ssn;
	
	private String password;
	
	private String accStatus;
	
	@CreationTimestamp
	@Column(name = "CREATE_DATE", updatable = false)
	private LocalDate createDate;
	
	@UpdateTimestamp
	@Column(name = "UPDATE_DATE", insertable = false)
	private LocalDate updateDate;
	
	private String createdBy;
	
	private String updatedBy;
	
}
