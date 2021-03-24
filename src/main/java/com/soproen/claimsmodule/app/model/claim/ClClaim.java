package com.soproen.claimsmodule.app.model.claim;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.soproen.claimsmodule.app.model.catalog.ClClaimType;
import com.soproen.claimsmodule.app.model.catalog.ClDistrict;
import com.soproen.claimsmodule.app.model.catalog.ClTa;
import com.soproen.claimsmodule.app.model.catalog.ClTransferInstitution;
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
@Table(name = "cl_claims")
public class ClClaim implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name= "claim_number")
	private String claimNumber;
	
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
	
	@Column(name= "site_name")
	private String siteName;
	
	@ManyToOne
	@JoinColumn(name="transfer_institution_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private ClTransferInstitution clTransferInstitution;
	
	@Column(name= "agency_name")
	private String agencyName;
	
	@ManyToOne
	@JoinColumn(name="claim_type_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private ClClaimType clClaimType;
	
	@Column(name= "amount_of_the_claim")
	private Double amountOfTheClaim;
	
	@Column(name= "observation")
	private String observation;
	
	@Column(name= "officer_name")
	private String officerName;
	
	@Column(name= "officer_position")
	private String officerPosition;
	
	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	
	@Column(name= "amount_to_be_transferred")
	private Double amountToBeTransferred;
	
	@Column(name= "created_by")
	private String createdBy;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value={ "hibernateLazyInitializer", "handler","clClaim" } ,allowSetters = true)
	@JoinColumn(name = "claim_id",nullable = false)
	private List<ClClaimStatus> clClaimStatuses;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value={ "hibernateLazyInitializer", "handler","clClaim" } ,allowSetters = true)
	@JoinColumn(name = "claim_id",nullable = false)
	private List<ClClaimActionRegistry> clClaimActionsRegistry;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value={ "hibernateLazyInitializer", "handler","clClaim" } ,allowSetters = true)
	@JoinColumn(name = "claim_id",nullable = false)
	private ClHouseholdClaim clHouseholdClaim;
	
	

}
