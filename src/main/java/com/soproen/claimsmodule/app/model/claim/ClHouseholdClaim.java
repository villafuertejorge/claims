package com.soproen.claimsmodule.app.model.claim;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.soproen.claimsmodule.app.model.catalog.ClDistrict;
import com.soproen.claimsmodule.app.model.catalog.ClTa;
import com.soproen.claimsmodule.app.model.catalog.ClVillage;
import com.soproen.claimsmodule.app.model.catalog.ClZone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cl_households_claim")
public class ClHouseholdClaim implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="district_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private ClDistrict clDistrict;
	
	@ManyToOne
	@JoinColumn(name="ta_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private ClTa clTa;
	
	@ManyToOne
	@JoinColumn(name="village_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private ClVillage clVillage;
	
	@ManyToOne
	@JoinColumn(name="zone_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private ClZone clZone;
	
	@Column(name="household_id")
	private Long householdId;
	
	@Column(name="household_code")
	private String householdCode;

	@Column(name="address")
	private String address;
	
	@Column(name="telephone")
	private String telephone;
	
	@Column(name="payment_receiver_code")
	private String paymentReceiverCode;

	@Column(name="payment_receiver_id")
	private Integer paymentReceiverId;

	@Column(name="payment_receiver_name")
	private String paymentReceiverName;
	
	@Column(name="alternative_receiver_code")
	private String alternativeReceiverCode;

	@Column(name="alternative_receiver_id")
	private Integer alternativeReceiverId;

	@Column(name="alternative_receiver_name")
	private String alternativeReceiverName;
	
	@Column(name="external_receiver_code")
	private String externalReceiverCode;

	@Column(name="external_receiver_id")
	private Integer externalReceiverId;

	@Column(name="external_receiver_name")
	private String externalReceiverName;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value={ "hibernateLazyInitializer", "handler","clHouseholdClaim" } ,allowSetters = true)
	@JoinColumn(name = "household_claim_id",nullable = false)
	private List<ClHouseholdMemberClaim> clHouseholdMembersClaim;
}
