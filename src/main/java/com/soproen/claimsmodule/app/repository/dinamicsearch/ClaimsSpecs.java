package com.soproen.claimsmodule.app.repository.dinamicsearch;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.soproen.claimsmodule.app.enums.ClClaimStatusEnum;
import com.soproen.claimsmodule.app.model.catalog.ClDistrict;
import com.soproen.claimsmodule.app.model.catalog.ClProgram;
import com.soproen.claimsmodule.app.model.catalog.ClTa;
import com.soproen.claimsmodule.app.model.catalog.ClTransferInstitution;
import com.soproen.claimsmodule.app.model.catalog.ClVillage;
import com.soproen.claimsmodule.app.model.catalog.ClZone;
import com.soproen.claimsmodule.app.model.claim.ClClaim;
import com.soproen.claimsmodule.app.model.claim.ClClaimStatus;
import com.soproen.claimsmodule.app.model.claim.ClClaimStatus_;
import com.soproen.claimsmodule.app.model.claim.ClClaim_;
import com.soproen.claimsmodule.app.model.claim.ClHouseholdClaim;
import com.soproen.claimsmodule.app.model.claim.ClHouseholdClaim_;
import com.soproen.claimsmodule.app.model.claim.ClHouseholdMemberClaim;
import com.soproen.claimsmodule.app.model.claim.ClHouseholdMemberClaim_;

public class ClaimsSpecs {

	
	public static Specification<ClClaim> getClaimByClaimNumber(String claimNumber) {
		return (root, query, criteriaBuilder) -> {
			Predicate equalPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get(ClClaim_.claimNumber)),
					"%" + claimNumber.toLowerCase() + "%");
			return equalPredicate;
		};
	}
	
	public static Specification<ClClaim> getClaimByCreatedByUsername(String username) {
		return (root, query, criteriaBuilder) -> {
			Predicate equalPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get(ClClaim_.createdBy)),
					"%" + username.toLowerCase() + "%");
			return equalPredicate;
		};
	}

	public static Specification<ClClaim> getClaimByHouseholdCode(String householdCode) {
		return (root, query, criteriaBuilder) -> {
			ListJoin<ClClaim, ClHouseholdClaim> householdClaimJoin = root.join(ClClaim_.clHouseholdsClaim);
			Predicate equalPredicate = criteriaBuilder.like(
					criteriaBuilder.lower(householdClaimJoin.get(ClHouseholdClaim_.householdCode)),
					"%" + householdCode.toLowerCase() + "%");
			return equalPredicate;
		};
	}
	
	
	public static Specification<ClClaim> getClaimByProgram(ClProgram clProgram) {
	      return (root, query, criteriaBuilder) -> {
	    	  ListJoin<ClClaim, ClHouseholdClaim> householdClaimJoin = root.join(ClClaim_.clHouseholdsClaim);
	    	  Predicate equalPredicate = criteriaBuilder.equal( householdClaimJoin.get(ClHouseholdClaim_.clProgram), clProgram);
	          return equalPredicate;
	      };
	  }
	
	public static Specification<ClClaim> getClaimByTransferInstitution(ClTransferInstitution clTransferInstitution) {
	      return (root, query, criteriaBuilder) -> {
	    	  Predicate equalPredicate = criteriaBuilder.equal(root.get(ClClaim_.clTransferInstitution), clTransferInstitution);
				return equalPredicate;
	      };
	  }
	
	public static Specification<ClClaim> getClaimByDistrict(ClDistrict clDistrict) {
	      return (root, query, criteriaBuilder) -> {
	    	  ListJoin<ClClaim, ClHouseholdClaim> householdClaimJoin = root.join(ClClaim_.clHouseholdsClaim);
	    	  Predicate equalPredicate = criteriaBuilder.equal( householdClaimJoin.get(ClHouseholdClaim_.clDistrict), clDistrict);
	          return equalPredicate;
	      };
	  }
	
	public static Specification<ClClaim> getClaimByTA(ClTa clTa) {
	      return (root, query, criteriaBuilder) -> {
	    	  ListJoin<ClClaim, ClHouseholdClaim> householdClaimJoin = root.join(ClClaim_.clHouseholdsClaim);
	    	  Predicate equalPredicate = criteriaBuilder.equal( householdClaimJoin.get(ClHouseholdClaim_.clTa), clTa);
	          return equalPredicate;
	      };
	  }
	

	public static Specification<ClClaim> getClaimByVillage(ClVillage clVillage) {
	      return (root, query, criteriaBuilder) -> {
	    	  ListJoin<ClClaim, ClHouseholdClaim> householdClaimJoin = root.join(ClClaim_.clHouseholdsClaim);
	    	  Predicate equalPredicate = criteriaBuilder.equal( householdClaimJoin.get(ClHouseholdClaim_.clVillage), clVillage);
	          return equalPredicate;
	      };
	  }
	
	public static Specification<ClClaim> getClaimByZone(ClZone clZone) {
	      return (root, query, criteriaBuilder) -> {
	    	  ListJoin<ClClaim, ClHouseholdClaim> householdClaimJoin = root.join(ClClaim_.clHouseholdsClaim);
	    	  Predicate equalPredicate = criteriaBuilder.equal( householdClaimJoin.get(ClHouseholdClaim_.clZone), clZone);
	          return equalPredicate;
	      };
	  }
	
	public static Specification<ClClaim> getClaimByFirstNameMemberHowPresentClaim(String firstName, String lastName) {
	      return (root, query, criteriaBuilder) -> {
	    	  ListJoin<ClHouseholdClaim, ClHouseholdMemberClaim> householdClaimJoin = root.join(ClClaim_.clHouseholdsClaim).join(ClHouseholdClaim_.clHouseholdMembersClaim);
	    	  List<Predicate> predicateList = new ArrayList<>();
	    	 
	    	  if(firstName!=null && !firstName.isEmpty()) {
	    		  Predicate equalPredicate = criteriaBuilder.like(criteriaBuilder.lower(householdClaimJoin.get(ClHouseholdMemberClaim_.firstName)), "%" + firstName.toLowerCase() + "%");
	    		  predicateList.add(equalPredicate);
	    	  }
	    	  
	    	  if(lastName!=null && !lastName.isEmpty()) {
	    		  Predicate equalPredicate = criteriaBuilder.like(criteriaBuilder.lower(householdClaimJoin.get(ClHouseholdMemberClaim_.lastName)), "%" + lastName.toLowerCase() + "%");
	    		  predicateList.add(equalPredicate);
	    	  }
	    	  
	    	  if(!predicateList.isEmpty()) {
	    		  Predicate equalPredicate = criteriaBuilder.isTrue(householdClaimJoin.get(ClHouseholdMemberClaim_.isPresentedClaim));
	    		  predicateList.add(equalPredicate);
	    	  }
	    	  return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
	      };
	  }
	
	public static Specification<ClClaim> getClaimByStatus(ClClaimStatusEnum status) {
	      return (root, query, criteriaBuilder) -> {
	    	  ListJoin<ClClaim, ClClaimStatus> householdClaimJoin = root.join(ClClaim_.clClaimStatuses);
	    	  Predicate equalPredicate = criteriaBuilder.isNull(householdClaimJoin.get(ClClaimStatus_.closedAt));
	    	  Predicate equalPredicate2 = criteriaBuilder.equal(householdClaimJoin.get(ClClaimStatus_.status),status);
	    	  return criteriaBuilder.and(equalPredicate,equalPredicate2);
	      };
	  }
}
