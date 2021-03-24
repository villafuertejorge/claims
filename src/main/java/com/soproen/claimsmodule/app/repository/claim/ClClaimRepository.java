package com.soproen.claimsmodule.app.repository.claim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soproen.claimsmodule.app.model.claim.ClClaim;

@Repository
public interface ClClaimRepository extends JpaRepository<ClClaim,Long>{

}
