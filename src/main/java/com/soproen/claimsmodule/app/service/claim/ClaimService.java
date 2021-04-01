package com.soproen.claimsmodule.app.service.claim;

import java.util.List;

import com.soproen.claimsdto.dto.claim.ClClaimDTO;
import com.soproen.claimsdto.dto.claim.RegisterNewClaimForHouseholdDTO;
import com.soproen.claimsdto.dto.claim.SearchClaimDTO;
import com.soproen.claimsmodule.app.exceptions.ServiceException;
import com.soproen.claimsmodule.app.model.claim.ClClaim;

public interface ClaimService {

	ClClaim registerNewClaimForHousehold(RegisterNewClaimForHouseholdDTO registerNewClaimForHouseholdDTO)
			throws ServiceException;

	ClClaim findClClaimById(Long idClClaim) throws ServiceException;

	ClClaim updateClaim(ClClaimDTO clClaimDTO) throws ServiceException;

	List<ClClaim> searchClaim(SearchClaimDTO searchClaimDTO) throws ServiceException;

}
