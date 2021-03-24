package com.soproen.claimsmodule.app.service.claim;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soproen.claimsdto.dto.claim.RegisterNewClaimForHouseholdDTO;
import com.soproen.claimsmodule.app.Utilities.Utilities;
import com.soproen.claimsmodule.app.enums.ClClaimStatusEnum;
import com.soproen.claimsmodule.app.exceptions.ServiceException;
import com.soproen.claimsmodule.app.model.claim.ClClaim;
import com.soproen.claimsmodule.app.model.claim.ClClaimStatus;
import com.soproen.claimsmodule.app.model.claim.ClHouseholdClaim;
import com.soproen.claimsmodule.app.repository.claim.ClClaimRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClaimServiceImpl implements ClaimService{

	@Autowired
	protected Utilities utilities;
	@Autowired
	private ClClaimRepository clClaimRepository;

	@Override
	@Transactional
	public ClClaim registerNewClaimForHousehold(RegisterNewClaimForHouseholdDTO registerNewClaimForHouseholdDTO)
			throws ServiceException {
		try {
			
			Date currentDate = Calendar.getInstance().getTime();
			
			ClHouseholdClaim clHouseholdClaim = utilities.mapObject(registerNewClaimForHouseholdDTO.getClHouseholdClaim(), ClHouseholdClaim.class);
			
			ClClaim newClClaimTmp = ClClaim.builder()
			.claimNumber("CL-"+currentDate.getTime())
			.clHouseholdClaim(clHouseholdClaim)
			.clClaimStatuses(Arrays.asList(ClClaimStatus.builder()
					.createdAt(currentDate)
					.status(ClClaimStatusEnum.CREATED)
					.usernameCreatedBy(registerNewClaimForHouseholdDTO.getUsernameCreatedBy())
					.build()))
			.build();
			
			ClClaim newClClaim = clClaimRepository.save(newClClaimTmp);
			return newClClaim;
			
		} catch (DataAccessException e) {
			log.error("registerNewClaimForHousehold = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		
	}
}
