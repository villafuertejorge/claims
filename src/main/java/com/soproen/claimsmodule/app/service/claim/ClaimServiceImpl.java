package com.soproen.claimsmodule.app.service.claim;

import java.util.ArrayList;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soproen.claimsdto.dto.claim.ClClaimDTO;
import com.soproen.claimsdto.dto.claim.RegisterNewClaimForHouseholdDTO;
import com.soproen.claimsdto.dto.claim.SearchClaimDTO;
import com.soproen.claimsmodule.app.Utilities.Utilities;
import com.soproen.claimsmodule.app.enums.ClClaimStatusEnum;
import com.soproen.claimsmodule.app.enums.RequiredClaimFiledsEnum;
import com.soproen.claimsmodule.app.exceptions.ServiceException;
import com.soproen.claimsmodule.app.model.catalog.ClDistrict;
import com.soproen.claimsmodule.app.model.catalog.ClProgram;
import com.soproen.claimsmodule.app.model.catalog.ClTa;
import com.soproen.claimsmodule.app.model.catalog.ClTransferInstitution;
import com.soproen.claimsmodule.app.model.catalog.ClVillage;
import com.soproen.claimsmodule.app.model.catalog.ClZone;
import com.soproen.claimsmodule.app.model.claim.ClClaim;
import com.soproen.claimsmodule.app.model.claim.ClClaimActionRegistry;
import com.soproen.claimsmodule.app.model.claim.ClClaimStatus;
import com.soproen.claimsmodule.app.model.claim.ClHouseholdClaim;
import com.soproen.claimsmodule.app.repository.claim.ClClaimRepository;
import com.soproen.claimsmodule.app.repository.dinamicsearch.ClaimsSpecs;

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
			log.info(" clClaim = {}", clClaim);

			final Long idSelectedMemberHowPresentClaim = clClaim.getSelectedMemberHowPresentClaim().getId();
			clClaim.getClHouseholdsClaim().iterator().next().getClHouseholdMembersClaim().stream().forEach(obj -> {
				obj.setIsPresentedClaim(Boolean.FALSE);
				if (obj.getId() == idSelectedMemberHowPresentClaim) {
					obj.setIsPresentedClaim(Boolean.TRUE);
				}
			});

			// close current claim status
			clClaim.setClClaimStatuses(clClaimRepository.findById(clClaimDTO.getId())
					.orElseThrow(() -> new ServiceException("Claim not found")).getClClaimStatuses());
			clClaim.getClClaimStatuses().stream().forEach(clClaimStatusesTmp -> {
				if (clClaimStatusesTmp.getClosedAt() == null) {
					clClaimStatusesTmp.setClosedAt(currentDate);
				}
			});

			log.info(" clClaim = {}", clClaim);
			Map<RequiredClaimFiledsEnum, String> mapResult = isClaimFormComplete(clClaim);
			for (Map.Entry<RequiredClaimFiledsEnum, String> obj : mapResult.entrySet()) {
				log.info("key = {}, value = {}", obj.getKey(), obj.getValue());
			}
			clClaim.getClClaimStatuses()
					.add(ClClaimStatus.builder().createdAt(currentDate)
							.status(mapResult.isEmpty() ? ClClaimStatusEnum.OPEN : ClClaimStatusEnum.INCOMPLETE)
							.createdAt(currentDate).usernameCreatedBy(clClaim.getApplicationUserName()).build());

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

		if (!clClaim.getClHouseholdsClaim().iterator().next().getClHouseholdMembersClaim().stream()
				.filter(obj -> obj.isPresentedClaim).findAny().isPresent()) {
			mapResult.put(RequiredClaimFiledsEnum.MEMBER_HOW_PRESENT_CLAIM,
					env.getProperty("app.claim-fields.member-how-present-claim"));
		}

		if (clClaim.getClTransferInstitution() == null || clClaim.getClTransferInstitution().getId() == null) {
			mapResult.put(RequiredClaimFiledsEnum.TRANSFER_INSTITUTION,
					env.getProperty("app.claim-fields.transfer-institution"));
		}

		if (clClaim.getClClaimType() == null || clClaim.getClClaimType().getId() == null) {
			mapResult.put(RequiredClaimFiledsEnum.TYPE_OF_CLAIM, env.getProperty("app.claim-fields.claim-type"));
		}

		if (clClaim.getAmountOfTheClaim() == null || clClaim.getAmountOfTheClaim() <= 0.0) {
			mapResult.put(RequiredClaimFiledsEnum.AMOUNT_OF_CLAIM, env.getProperty("app.claim-fields.claim-amount"));
		}

		if (clClaim.getOfficerName() == null || clClaim.getOfficerName().isEmpty()) {
			mapResult.put(RequiredClaimFiledsEnum.NAME_OFFICER, env.getProperty("app.claim-fields.officer-name"));
		}

		if (clClaim.getCreatedAt() == null) {
			mapResult.put(RequiredClaimFiledsEnum.CLAIM_DATE, env.getProperty("app.claim-fields.claim-date"));
		}

		return mapResult;

	}

	@Override
	@Transactional(readOnly = true)
	public List<ClClaim> searchClaim(SearchClaimDTO searchClaimDTO) throws ServiceException {
		try {

			log.info("searchClaimDTO = {}" , searchClaimDTO);
			
			List<Specification<ClClaim>> searchSpecifications = new ArrayList<>();

			if (!utilities.isNullOrEmpty(searchClaimDTO.getClaimNumber())) {
				searchSpecifications.add(ClaimsSpecs.getClaimByClaimNumber(searchClaimDTO.getClaimNumber()));
			}

			if (!utilities.isNullOrEmpty(searchClaimDTO.getHouseholdCode())) {
				searchSpecifications.add(ClaimsSpecs.getClaimByHouseholdCode(searchClaimDTO.getHouseholdCode()));
			}

			if (utilities.isObjectIdentifiable(searchClaimDTO.getProgram())) {
				searchSpecifications.add(ClaimsSpecs
						.getClaimByProgram(ClProgram.builder().id(searchClaimDTO.getProgram().getId()).build()));
			}

			if (utilities.isObjectIdentifiable(searchClaimDTO.getDistrict())) {
				searchSpecifications.add(ClaimsSpecs
						.getClaimByDistrict(ClDistrict.builder().id(searchClaimDTO.getDistrict().getId()).build()));
			}

			if (utilities.isObjectIdentifiable(searchClaimDTO.getTa())) {
				searchSpecifications
						.add(ClaimsSpecs.getClaimByTA(ClTa.builder().id(searchClaimDTO.getTa().getId()).build()));
			}

			if (utilities.isObjectIdentifiable(searchClaimDTO.getVillage())) {
				searchSpecifications.add(ClaimsSpecs
						.getClaimByVillage(ClVillage.builder().id(searchClaimDTO.getVillage().getId()).build()));
			}

			if (utilities.isObjectIdentifiable(searchClaimDTO.getZone())) {
				searchSpecifications
						.add(ClaimsSpecs.getClaimByZone(ClZone.builder().id(searchClaimDTO.getZone().getId()).build()));
			}

			if (!utilities.isNullOrEmpty(searchClaimDTO.getFirstNameMemberHowPresentClaim())
					|| !utilities.isNullOrEmpty(searchClaimDTO.getLastNameMemberHowPresentClaim())) {
				searchSpecifications.add(ClaimsSpecs.getClaimByFirstNameMemberHowPresentClaim(
						searchClaimDTO.getFirstNameMemberHowPresentClaim(),
						searchClaimDTO.getLastNameMemberHowPresentClaim()));
			}

			if (utilities.isObjectIdentifiable(searchClaimDTO.getTransferInstitution())) {
				searchSpecifications.add(ClaimsSpecs.getClaimByTransferInstitution(
						ClTransferInstitution.builder().id(searchClaimDTO.getTransferInstitution().getId()).build()));
			}

			if (!utilities.isNull(searchClaimDTO.getStatus())) {
				searchSpecifications.add(
						ClaimsSpecs.getClaimByStatus(ClClaimStatusEnum.valueOf(searchClaimDTO.getStatus().name())));
			}

			if (!utilities.isNullOrEmpty(searchClaimDTO.getUserNameCreatedBy())) {
				searchSpecifications
						.add(ClaimsSpecs.getClaimByCreatedByUsername(searchClaimDTO.getUserNameCreatedBy()));
			}

			List<ClClaim> claimList = new ArrayList<>();
			Pageable sortedByPriceDesc = PageRequest.of(0, 100, Sort.by("id").descending());
			if (searchSpecifications.isEmpty()) {
				claimList = clClaimRepository.findAll(sortedByPriceDesc).toList();
			} else {
				claimList = searhClaim(searchSpecifications, sortedByPriceDesc);
			}

			claimList.stream().forEach(obj -> prepareteClClaimToReturn(obj));
			return claimList;

		} catch (DataAccessException e) {
			log.error("searchClaim = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	private List<ClClaim> searhClaim(List<Specification<ClClaim>> searchSpecifications, Pageable sortedByPriceDesc) {
		List<ClClaim> claimList;
		Specification<ClClaim> specificaitionClaimTmp = null;
		int cont = 0;
		for (Specification<ClClaim> specification : searchSpecifications) {
			if (cont == 0) {
				specificaitionClaimTmp = Specification.where(specification);
				cont = 1;
			} else {
				specificaitionClaimTmp = specificaitionClaimTmp.and(specification);
			}
		}
		claimList = clClaimRepository.findAll(specificaitionClaimTmp, sortedByPriceDesc).toList();
		return claimList;
	}

}
