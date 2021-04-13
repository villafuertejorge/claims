package com.soproen.claimsmodule.app.repository.claim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.soproen.claimsmodule.app.model.claim.ClClaim;

@Repository
public interface ClClaimRepository extends JpaRepository<ClClaim,Long>,JpaSpecificationExecutor<ClClaim>{

	@Modifying
	@Query("update ClClaim cl SET cl.claimNumber = :claimNumber where cl.id = :id")
	int updateClaimNumber(@Param("claimNumber") String claimNumber, @Param("id") Long id);
}
