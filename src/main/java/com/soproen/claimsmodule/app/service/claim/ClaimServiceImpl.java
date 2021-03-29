package com.soproen.claimsmodule.app.service.claim;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soproen.claimsdto.dto.claim.ClClaimDTO;
import com.soproen.claimsdto.dto.claim.RegisterNewClaimForHouseholdDTO;
import com.soproen.claimsmodule.app.Utilities.Utilities;
import com.soproen.claimsmodule.app.enums.ClClaimStatusEnum;
import com.soproen.claimsmodule.app.enums.RequiredClaimFiledsEnum;
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
	@Autowired
	private Environment env;

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
					.clHouseholdsClaim(Arrays.asList(clHouseholdClaim))
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

		newClClaim.getClHouseholdsClaim().iterator().next().getClHouseholdMembersClaim().stream().forEach(obj -> {
			if (obj.getIsPresentedClaim() != null && obj.getIsPresentedClaim()) {
				newClClaim.setSelectedMemberHowPresentClaim(obj);
			}
		});

	}

	@Override
	@Transactional
	public ClClaim updateClaim(ClClaimDTO clClaimDTO) throws ServiceException {
		try {

			Date currentDate = Calendar.getInstance().getTime();
			ClClaim clClaim = utilities.mapObject(clClaimDTO, ClClaim.class);

			final Long idSelectedMemberHowPresentClaim = clClaim.getSelectedMemberHowPresentClaim().getId();
			clClaim.getClHouseholdsClaim().iterator().next().getClHouseholdMembersClaim().stream().forEach(obj -> {
				obj.setIsPresentedClaim(Boolean.FALSE);
				if (obj.getId() == idSelectedMemberHowPresentClaim) {
					obj.setIsPresentedClaim(Boolean.TRUE);
				}
			});

			// close current claim status
			clClaim.getClClaimStatuses().stream().forEach(clClaimStatusesTmp -> {
				if (clClaimStatusesTmp.getClosedAt() == null) {
					clClaimStatusesTmp.setClosedAt(currentDate);
				}
			});

			Map<RequiredClaimFiledsEnum, String> mapResult = isClaimFormComplete(clClaim);
			clClaim.getClClaimStatuses()
					.add(ClClaimStatus.builder().createdAt(currentDate)
							.status(mapResult.isEmpty() ? ClClaimStatusEnum.OPEN : ClClaimStatusEnum.INCOMPLETE)
							.createdAt(currentDate)
							.usernameCreatedBy(clClaim.getApplicationUserName()).build());

			
			clClaim = clClaimRepository.save(clClaim);
			clClaim.setMapResultClaimFieldsValidation(mapResult);
			return clClaim;

		} catch (DataAccessException e) {
			log.error("updateClaim = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	private Map<RequiredClaimFiledsEnum, String> isClaimFormComplete(ClClaim clClaim) {

		Map<RequiredClaimFiledsEnum, String> mapResult = new HashMap<>();

		if (clClaim.getClHouseholdsClaim().iterator().next().getClHouseholdMembersClaim().stream()
				.filter(obj -> obj.isPresentedClaim).findAny().isPresent()) {
			mapResult.put(RequiredClaimFiledsEnum.MEMBER_HOW_PRESENT_CLAIM,
					env.getProperty("app.claim-fields.member-how-present-claim"));
		}

		if (clClaim.getClTransferInstitution() != null && clClaim.getClTransferInstitution().getId() != null) {
			mapResult.put(RequiredClaimFiledsEnum.TRANSFER_INSTITUTION,
					env.getProperty("app.claim-fields.transfer-institution"));
		}

		if (clClaim.getClClaimType() != null && clClaim.getClClaimType().getId() != null) {
			mapResult.put(RequiredClaimFiledsEnum.TYPE_OF_CLAIM, env.getProperty("app.claim-fields.claim-type"));
		}

		if (clClaim.getAmountOfTheClaim() != null && clClaim.getAmountOfTheClaim() <= 0.0) {
			mapResult.put(RequiredClaimFiledsEnum.AMOUNT_OF_CLAIM, env.getProperty("app.claim-fields.claim-amount"));
		}

		if (clClaim.getOfficerName() != null && !clClaim.getOfficerName().isEmpty()) {
			mapResult.put(RequiredClaimFiledsEnum.NAME_OFFICER, env.getProperty("app.claim-fields.officer-name"));
		}

		if (clClaim.getCreatedAt() != null) {
			mapResult.put(RequiredClaimFiledsEnum.CLAIM_DATE, env.getProperty("app.claim-fields.claim"));
		}

		return mapResult;

	}

}
