package com.soproen.claimsmodule.app.model.claim;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ClHouseholdMemberClaim.class)
public class ClHouseholdMemberClaim_ {

	public static volatile SingularAttribute<ClHouseholdMemberClaim, String> firstName;
	public static volatile SingularAttribute<ClHouseholdMemberClaim, String> lastName;
	public static volatile SingularAttribute<ClHouseholdMemberClaim, Boolean> isPresentedClaim;
	
	
}
