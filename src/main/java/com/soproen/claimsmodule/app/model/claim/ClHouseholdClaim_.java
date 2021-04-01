package com.soproen.claimsmodule.app.model.claim;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.soproen.claimsmodule.app.model.catalog.ClDistrict;
import com.soproen.claimsmodule.app.model.catalog.ClProgram;
import com.soproen.claimsmodule.app.model.catalog.ClTa;
import com.soproen.claimsmodule.app.model.catalog.ClVillage;
import com.soproen.claimsmodule.app.model.catalog.ClZone;

@StaticMetamodel(ClHouseholdClaim.class)
public class ClHouseholdClaim_ {

	public static volatile SingularAttribute<ClHouseholdClaim, String> householdCode;
	public static volatile SingularAttribute<ClHouseholdClaim, ClProgram> clProgram;
	public static volatile SingularAttribute<ClHouseholdClaim, ClDistrict> clDistrict;
	public static volatile SingularAttribute<ClHouseholdClaim, ClTa> clTa;
	public static volatile SingularAttribute<ClHouseholdClaim, ClVillage> clVillage;
	public static volatile SingularAttribute<ClHouseholdClaim, ClZone> clZone;
	public static volatile ListAttribute<ClHouseholdClaim, ClHouseholdMemberClaim> clHouseholdMembersClaim;
	 
}
