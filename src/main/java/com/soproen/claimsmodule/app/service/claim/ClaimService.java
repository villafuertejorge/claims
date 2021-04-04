package com.soproen.claimsmodule.app.service.claim;

import java.io.InputStream;
import java.util.List;

import com.soproen.claimsdto.dto.claim.ClClaimDTO;
import com.soproen.claimsdto.dto.claim.RegisterNewClaimActionDTO;
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

	ClClaim registerNewClaimAction(RegisterNewClaimActionDTO registerNewClaimActionDTO) throws ServiceException;

	InputStream generateSearchClaimCsvFile(SearchClaimDTO searchClaimDTO) throws ServiceException;

}
