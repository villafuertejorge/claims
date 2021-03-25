package com.soproen.claimsmodule.app.service.claim;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soproen.claimsdto.dto.claim.RegisterNewClaimForHouseholdDTO;
import com.soproen.claimsmodule.app.Utilities.Utilities;
import com.soproen.claimsmodule.app.enums.ClClaimStatusEnum;
import com.soproen.claimsmodule.app.exceptions.ServiceException;
import com.soproen.claimsmodule.app.model.claim.ClClaim;
import com.soproen.claimsmodule.app.model.claim.ClClaimActionRegistry;
import com.soproen.claimsmodule.app.model.claim.ClClaimStatus;
import com.soproen.claimsmodule.app.model.claim.ClHouseholdClaim;
import com.soproen.claimsmodule.app.repository.claim.ClClaimRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClaimServiceImpl implements ClaimService {

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

			ClHouseholdClaim clHouseholdClaim = utilities
					.mapObject(registerNewClaimForHouseholdDTO.getClHouseholdClaim(), ClHouseholdClaim.class);

			ClClaim newClClaimTmp = ClClaim.builder().claimNumber("CL-" + currentDate.getTime())
					.createdBy(registerNewClaimForHouseholdDTO.getUsernameCreatedBy())
					.clHouseholdsClaim(Arrays.asList(
							clHouseholdClaim))
					.clClaimStatuses(Arrays
							.asList(ClClaimStatus.builder().createdAt(currentDate).status(ClClaimStatusEnum.CREATED)
									.usernameCreatedBy(registerNewClaimForHouseholdDTO.getUsernameCreatedBy()).build()))
					.build();
			
			ClClaim newClClaim = clClaimRepository.save(newClClaimTmp);
			return newClClaim;

		} catch (DataAccessException e) {
			log.error("registerNewClaimForHousehold = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ClClaim findClClaimById(Long idClClaim) throws ServiceException {
		try {

			ClClaim newClClaim = clClaimRepository.findById(idClClaim)
					.orElseThrow(() -> new ServiceException("Claim not found"));

			prepareteClClaimToReturn(newClClaim);

			return newClClaim;

		} catch (DataAccessException e) {
			log.error("findClClaimById = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	private void prepareteClClaimToReturn(ClClaim newClClaim) {

		List<ClClaimActionRegistry> clClaimActionsRegistryList = newClClaim.getClClaimActionsRegistries().stream()
				.filter(obj -> obj.getClosedAt() == null).collect(Collectors.toList());
		newClClaim.setClClaimActionsRegistries(clClaimActionsRegistryList);

		List<ClClaimStatus> clClaimStatusesList = newClClaim.getClClaimStatuses().stream()
				.filter(obj -> obj.getClosedAt() == null).collect(Collectors.toList());
		newClClaim.setClClaimStatuses(clClaimStatusesList);
	}
}
