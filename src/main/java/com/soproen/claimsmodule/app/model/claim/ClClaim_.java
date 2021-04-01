package com.soproen.claimsmodule.app.model.claim;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.soproen.claimsmodule.app.model.catalog.ClTransferInstitution;

@StaticMetamodel(ClClaim.class)
public class ClClaim_ {

	public static volatile SingularAttribute<ClClaim, String> claimNumber;
	public static volatile ListAttribute<ClClaim, ClHouseholdClaim> clHouseholdsClaim;
	public static volatile SingularAttribute<ClClaim, ClTransferInstitution> clTransferInstitution;
	public static volatile ListAttribute<ClClaim, ClClaimStatus> clClaimStatuses;
	public static volatile SingularAttribute<ClClaim, String> createdBy;
}
