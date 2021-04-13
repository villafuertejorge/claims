package com.soproen.claimsmodule.app.service.claim;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.soproen.claimsdto.dto.claim.ClClaimDTO;
import com.soproen.claimsdto.dto.claim.RegisterNewClaimActionDTO;
import com.soproen.claimsdto.dto.claim.RegisterNewClaimForHouseholdDTO;
import com.soproen.claimsdto.dto.claim.SearchClaimDTO;
import com.soproen.claimsdto.dto.payments.ResponseRegisterHouseholdClaimValueDTO;
import com.soproen.claimsmodule.app.Utilities.CsvUtils;
import com.soproen.claimsmodule.app.Utilities.Utilities;
import com.soproen.claimsmodule.app.enums.ClClaimActionResultEnum;
import com.soproen.claimsmodule.app.enums.ClClaimStatusEnum;
import com.soproen.claimsmodule.app.enums.RequiredClaimFiledsEnum;
import com.soproen.claimsmodule.app.exceptions.ServiceException;
import com.soproen.claimsmodule.app.model.catalog.ClClaimAction;
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
import com.soproen.claimsmodule.app.model.claim.ClHouseholdMemberClaim;
import com.soproen.claimsmodule.app.repository.claim.ClClaimRepository;
import com.soproen.claimsmodule.app.repository.dinamicsearch.ClaimsSpecs;
import com.soproen.claimsmodule.app.service.catalog.CatalogService;

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
	@Autowired
	private CatalogService catalogService;
	@Autowired
	private CsvUtils csvUtils;
	@Autowired
	@Qualifier("restTemplatePayments")
	private RestTemplate restTemplatePayments;
	@Value("${app.end-point-register_hh_claim-value}")
	private String endPointRegisterHouseholdClaimValue;
	
	

	@Override
	@Transactional
	public ClClaim registerNewClaimForHousehold(RegisterNewClaimForHouseholdDTO registerNewClaimForHouseholdDTO)
			throws ServiceException {
		try {

			Date currentDate = Calendar.getInstance().getTime();

			ClHouseholdClaim clHouseholdClaim = utilities
					.mapObject(registerNewClaimForHouseholdDTO.getClHouseholdClaim(), ClHouseholdClaim.class);
			
			clHouseholdClaim.getClHouseholdMembersClaim().forEach(obj -> obj.setIsPresentedClaim(Boolean.FALSE));

			ClClaim newClClaimTmp = ClClaim.builder().claimNumber("CL-" + currentDate.getTime())
					.createdBy(registerNewClaimForHouseholdDTO.getUsernameCreatedBy())
					.clHouseholdsClaim(Arrays.asList(clHouseholdClaim))
					.clClaimStatuses(Arrays
							.asList(ClClaimStatus.builder().createdAt(currentDate).status(ClClaimStatusEnum.INCOMPLETE)
									.usernameCreatedBy(registerNewClaimForHouseholdDTO.getUsernameCreatedBy()).build()))
					.build();

			ClClaim newClClaim = clClaimRepository.save(newClClaimTmp);
			String claimNumber = "CL-" + newClClaim.getId();
			clClaimRepository.updateClaimNumber(claimNumber, newClClaim.getId());
			return newClClaim;

		} catch (DataAccessException e) {
			log.error("registerNewClaimForHousehold = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}catch(Exception e) {
			e.printStackTrace();
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
		// newClClaim.setClClaimActionsRegistries(clClaimActionsRegistryList);

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

			if (clClaim.getSelectedMemberHowPresentClaim() != null
					&& clClaim.getSelectedMemberHowPresentClaim().getId() != null) {
				final Long idSelectedMemberHowPresentClaim = clClaim.getSelectedMemberHowPresentClaim().getId();
				clClaim.getClHouseholdsClaim().iterator().next().getClHouseholdMembersClaim().stream().forEach(obj -> {
					obj.setIsPresentedClaim(Boolean.FALSE);
					if (obj.getId() == idSelectedMemberHowPresentClaim) {
						obj.setIsPresentedClaim(Boolean.TRUE);
					}
				});
			}

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
				.filter(obj -> obj.getIsPresentedClaim()!=null && obj.getIsPresentedClaim()).findAny().isPresent()) {
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
	public InputStream generateSearchClaimCsvFile(SearchClaimDTO searchClaimDTO) throws ServiceException {
		try {

			List<ClClaim> list = searchClaim(searchClaimDTO);
			List<Object[]> dataList = list.stream().map(tmp -> {
				return composeDataForClaimExportedFile(tmp);
			}).collect(Collectors.toList());

			InputStream newInputSream = csvUtils.createCsvFile(dataList,
					new String[] { 
							"District",
							"TA",
							"Village",
							"Zone",
							"Form Number",
							"Complete name of the member who presents the claim",
							"Address","Telephone/cellphone number",
							"Type of claim",
							"Amount of the claim ",
							"User who filled out the claim",
							"Transfer institution",
							"Observations",
							"Name of the officer",
							"Date of the claim",
							"Status",
							"Transfer Receiver Name",
							"Transfer Receiver Code",
							"Alternative Receiver Name",
							"Alternative Receiver Code",
							"External Receiver Name",
							"External Receiver Code",
							"Amount accepted",
							"Action",
							"Result",
							"Date of the last action taken",
							"User who registered the action"});

			return newInputSream;

		} catch (DataAccessException e) {
			log.error("generateSearchClaimCsvFile = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	private Object[] composeDataForClaimExportedFile(ClClaim tmp) {
		
		String selectedMemberFistName = "";
		String selectedMemberLastName = "";
		Optional<ClHouseholdMemberClaim> optionalSelectedMember = tmp.getClHouseholdsClaim().get(0)
				.getClHouseholdMembersClaim().stream().filter(obj -> obj.getIsPresentedClaim()!=null && obj.getIsPresentedClaim()).findAny();
		if (optionalSelectedMember.isPresent()) {
			selectedMemberFistName = optionalSelectedMember.get().getFirstName();
			selectedMemberLastName = optionalSelectedMember.get().getLastName();
		}

		ClClaimStatus clClaimStatus = tmp.getClClaimStatuses().stream().filter(obj -> obj.getClosedAt() == null)
				.findAny().get();
		
		String householdCode = tmp.getClHouseholdsClaim().get(0).getHouseholdCode();
		String address = tmp.getClHouseholdsClaim().get(0).getAddress()!=null? tmp.getClHouseholdsClaim().get(0).getAddress():"";
		String telephone = tmp.getClHouseholdsClaim().get(0).getTelephone()!=null? tmp.getClHouseholdsClaim().get(0).getTelephone():"";
		String claimType = tmp.getClClaimType()!=null?tmp.getClClaimType().getName():"";
		String amountOfTheClaim = tmp.getAmountOfTheClaim()!=null?tmp.getAmountOfTheClaim().toString():"";
		
		String district = tmp.getClHouseholdsClaim().get(0).getClDistrict() != null
				? tmp.getClHouseholdsClaim().get(0).getClDistrict().getName()
				: "";
		String ta = tmp.getClHouseholdsClaim().get(0).getClTa() != null
				? tmp.getClHouseholdsClaim().get(0).getClTa().getName()
				: "";
		String village = tmp.getClHouseholdsClaim().get(0).getClVillage() != null
				? tmp.getClHouseholdsClaim().get(0).getClVillage().getName()
				: "";
		String zone = tmp.getClHouseholdsClaim().get(0).getClZone() != null
				? tmp.getClHouseholdsClaim().get(0).getClZone().getName()
				: "";
		
		String transferInstitution = tmp.getClTransferInstitution() != null ? tmp.getClTransferInstitution().getName() : "";
		String claimStatus = clClaimStatus.getStatus().name() ;
		String createdBy = tmp.getCreatedBy();
		String observation = tmp.getObservation();
		String officerName = tmp.getOfficerName() != null ?tmp.getOfficerName():"";
		String claimDate = tmp.getCreatedAt()!= null ? utilities.formatDate(tmp.getCreatedAt(),"dd/MMM/yyyy"):"";
		
		String transferReceiverName = tmp.getClHouseholdsClaim().get(0).getPaymentReceiverName()!=null?tmp.getClHouseholdsClaim().get(0).getPaymentReceiverName():"";
		String transferReceiverCode = tmp.getClHouseholdsClaim().get(0).getPaymentReceiverCode()!=null?tmp.getClHouseholdsClaim().get(0).getPaymentReceiverCode():"";
		String alternativeReceiverName = tmp.getClHouseholdsClaim().get(0).getAlternativeReceiverName()!=null?tmp.getClHouseholdsClaim().get(0).getAlternativeReceiverName():"";
		String alternativeReceiverCode = tmp.getClHouseholdsClaim().get(0).getAlternativeReceiverCode()!=null?tmp.getClHouseholdsClaim().get(0).getAlternativeReceiverCode():"";
		String externalReceiverName = tmp.getClHouseholdsClaim().get(0).getExternalReceiverName()!=null?tmp.getClHouseholdsClaim().get(0).getExternalReceiverName():"";
		String externalReceiverCode = tmp.getClHouseholdsClaim().get(0).getExternalReceiverCode()!=null?tmp.getClHouseholdsClaim().get(0).getExternalReceiverCode():"";
		String amountAccepted=tmp.getAmountToBeTransferred()!=null?tmp.getAmountToBeTransferred().toString():"";
		

		String action="";
		String result ="";
		String dateLastAction="";
		String userRegisterAction="";
		
		Optional<ClClaimActionRegistry> optClClaimActionRegistry = tmp.getClClaimActionsRegistries().stream().filter(obj -> obj.getClosedAt() == null).findAny();
		if(optClClaimActionRegistry.isPresent()) {
			ClClaimActionRegistry clClaimActionRegistry = optClClaimActionRegistry.get();
			action = clClaimActionRegistry.getClClaimAction()!=null?clClaimActionRegistry.getClClaimAction().getName():"";
			result = clClaimActionRegistry.getActionResult()!=null?clClaimActionRegistry.getActionResult().name():"";
			dateLastAction = clClaimActionRegistry.getActionDate()!=null?utilities.formatDate(clClaimActionRegistry.getActionDate(),"dd/MMM/yyyy"):"";
			userRegisterAction = clClaimActionRegistry.getUsernameCreatedBy()!=null?clClaimActionRegistry.getUsernameCreatedBy():"";
		}
		
		
		
		
		String[] array = new String[] { 
				district,
				ta,
				village,
				zone,
				householdCode,
				selectedMemberFistName + " " + selectedMemberLastName,
				address,
				telephone,
				claimType,
				amountOfTheClaim,
				createdBy,
				transferInstitution,
				observation,
				officerName,
				claimDate,
				claimStatus,
				transferReceiverName,
				transferReceiverCode ,
				alternativeReceiverName,
				alternativeReceiverCode ,
				externalReceiverName ,
				externalReceiverCode ,
				amountAccepted,
				action,
				result,
				dateLastAction,
				userRegisterAction
				};

		return array;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ClClaim> searchClaim(SearchClaimDTO searchClaimDTO) throws ServiceException {
		try {

			log.info("searchClaimDTO = {}", searchClaimDTO);

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

	@Override
	@Transactional
	public ClClaim registerNewClaimAction(RegisterNewClaimActionDTO registerNewClaimActionDTO) throws ServiceException {
		try {

			Date currentDate = Calendar.getInstance().getTime();
			ClClaim newClClaim = clClaimRepository.findById(registerNewClaimActionDTO.getClaimId())
					.orElseThrow(() -> new ServiceException("Claim not found"));
			ClClaimAction clClaimAction = catalogService
					.findClClaimActionById(registerNewClaimActionDTO.getClClaimAction().getId());
			ClClaimActionResultEnum claimActionResult = ClClaimActionResultEnum
					.valueOf(registerNewClaimActionDTO.getClaimActionResult().name());

			newClClaim.getClClaimActionsRegistries().stream().forEach(obj -> {
				if (obj.getClosedAt() == null) {
					obj.setClosedAt(currentDate);
				}
			});

			log.info("clClaimAction = {}", clClaimAction);

			newClClaim.getClClaimActionsRegistries()
					.add(ClClaimActionRegistry.builder().clClaimAction(clClaimAction)
							.claimDetails(registerNewClaimActionDTO.getClaimDetails()).actionResult(claimActionResult)
							.details(registerNewClaimActionDTO.getClaimResultDetails())
							.actionDate(registerNewClaimActionDTO.getClaimActionDate())
							.usernameCreatedBy(registerNewClaimActionDTO.getCreatedByUsername()).createdAt(currentDate)
							.build());

			newClClaim.getClClaimStatuses().stream().forEach(clClaimStatusesTmp -> {
				if (clClaimStatusesTmp.getClosedAt() == null) {
					clClaimStatusesTmp.setClosedAt(currentDate);
				}
			});

			if (claimActionResult.equals(ClClaimActionResultEnum.ACCEPT)) {
				newClClaim.setAmountToBeTransferred(registerNewClaimActionDTO.getAmountToBeTransferred());
				// change status to accepted
				newClClaim.getClClaimStatuses()
						.add(ClClaimStatus.builder().createdAt(currentDate).status(ClClaimStatusEnum.ACCEPTED)
								.createdAt(currentDate)
								.usernameCreatedBy(registerNewClaimActionDTO.getCreatedByUsername()).build());

				// TODO send amount to payments module
				registerClaimValueInPaymentModule(registerNewClaimActionDTO, newClClaim.getClHouseholdsClaim().get(0));

			} else {
				// change status to reject or open
				newClClaim.getClClaimStatuses().add(ClClaimStatus.builder().createdAt(currentDate)
						.status(claimActionResult.equals(ClClaimActionResultEnum.OPEN_CLAIM) ? ClClaimStatusEnum.OPEN
								: ClClaimStatusEnum.REJECTED)
						.createdAt(currentDate).usernameCreatedBy(registerNewClaimActionDTO.getCreatedByUsername())
						.build());
			}

			log.info("newClClaim = {} ", newClClaim);
			newClClaim = clClaimRepository.save(newClClaim);
			return newClClaim;
		} catch (DataAccessException e) {
			e.printStackTrace();
			log.error("registerNewClaimAction = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public void registerClaimValueInPaymentModule(RegisterNewClaimActionDTO registerNewClaimActionDTO,
			ClHouseholdClaim clHouseholdClaim) {
		try {

			String householdCode = clHouseholdClaim.getHouseholdCode();
			Long idProgram = clHouseholdClaim.getClProgram().getId();

			String url = endPointRegisterHouseholdClaimValue.replace("{householdCode}", householdCode)
					.replace("{programId}", idProgram.toString())
					.replace("{amount}", registerNewClaimActionDTO.getAmountToBeTransferred().toString())
					.replace("{createdBy}", registerNewClaimActionDTO.getCreatedByUsername());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity request = new HttpEntity<>(headers);

			ResponseEntity<ResponseRegisterHouseholdClaimValueDTO> response = restTemplatePayments.postForEntity(url,
					request, ResponseRegisterHouseholdClaimValueDTO.class);

			log.info("response.getBody().isResponseOK() = {}", response.getBody().isResponseOK());

			if (!response.getStatusCode().equals(HttpStatus.OK) || !response.getBody().isResponseOK()) {
				throw new ServiceException("Claim could not be registered in the payment module");
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("registerClaimValueInPaymentModule = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

}
