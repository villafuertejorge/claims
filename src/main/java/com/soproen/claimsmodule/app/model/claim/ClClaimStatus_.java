package com.soproen.claimsmodule.app.model.claim;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.soproen.claimsmodule.app.enums.ClClaimStatusEnum;

@StaticMetamodel(ClClaimStatus.class)
public class ClClaimStatus_ {

	public static volatile SingularAttribute<ClClaimStatus, ClClaimStatusEnum> status;
	public static volatile SingularAttribute<ClClaimStatus, Date> closedAt;
}
