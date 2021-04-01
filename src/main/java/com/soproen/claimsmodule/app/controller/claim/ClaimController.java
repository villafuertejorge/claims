package com.soproen.claimsmodule.app.controller.claim;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.soproen.claimsdto.dto.BasicValidation;
import com.soproen.claimsdto.dto.claim.ClClaimDTO;
import com.soproen.claimsdto.dto.claim.RegisterNewClaimForHouseholdDTO;
import com.soproen.claimsdto.dto.claim.SearchClaimDTO;
import com.soproen.claimsmodule.app.controller.AbstractParentController;
import com.soproen.claimsmodule.app.exceptions.ServiceException;
import com.soproen.claimsmodule.app.service.claim.ClaimService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/claim")
public class ClaimController extends AbstractParentController {

	@Autowired
	private ClaimService claimService;

	@PostMapping("/registerNewClaimForHousehold")
	public ResponseEntity<?> registerNewClaimForHousehold(
			@Validated(BasicValidation.class) @Valid @RequestBody RegisterNewClaimForHouseholdDTO registerNewClaimForHouseholdDTO,
			BindingResult bindingResult) {
		try {
			if (bindingResult.hasErrors()) {
				return new ResponseEntity<Map<String, Object>>(super.responseError("Validation Error", bindingResult),
						HttpStatus.OK);
			}
			ClClaimDTO clClaimDTO = utilities.mapObject(
					claimService.registerNewClaimForHousehold(registerNewClaimForHouseholdDTO), ClClaimDTO.class);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", clClaimDTO), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("registerNewClaimForHousehold = {} ", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError(e.getMessage()), HttpStatus.OK);
		}
	}

	@GetMapping("/findClaimById/{idClClaim}")
	public ResponseEntity<?> findClaimById(@PathVariable(name = "idClClaim", required = true) Long idClClaim) {
		try {

			ClClaimDTO clClaimDTO = utilities.mapObject(claimService.findClClaimById(idClClaim), ClClaimDTO.class);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", clClaimDTO), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("findClaimById = {} ", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError(e.getMessage()), HttpStatus.OK);
		}
	}
	
	@PostMapping("/updateClaim")
	public ResponseEntity<?> updateClaim(
			@Validated(BasicValidation.class) @Valid @RequestBody ClClaimDTO clClaimDTOTmp,
			BindingResult bindingResult) {
		try {
			if (bindingResult.hasErrors()) {
				return new ResponseEntity<Map<String, Object>>(super.responseError("Validation Error", bindingResult),
						HttpStatus.OK);
			}
			ClClaimDTO clClaimDTO = utilities.mapObject(claimService.updateClaim(clClaimDTOTmp), ClClaimDTO.class);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", clClaimDTO), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("updateClaim = {} ", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError(e.getMessage()), HttpStatus.OK);
		}
	}
	
	@PostMapping("/searchClaim")
	public ResponseEntity<?> searchClaim( @RequestBody SearchClaimDTO searchClaimDTO) {
		try {
			List<ClClaimDTO> claimList = utilities.mapObject(
					claimService.searchClaim(searchClaimDTO),
					new TypeReference<List<ClClaimDTO>>() {
					});
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", claimList), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("updateClaim = {} ", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError(e.getMessage()), HttpStatus.OK);
		}
	}
	
	@PostMapping("/registerClaimAction")
	public ResponseEntity<?> registerClaimAction( @RequestBody SearchClaimDTO searchClaimDTO) {
		try {
			
			
			
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", null), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("registerClaimAction = {} ", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError(e.getMessage()), HttpStatus.OK);
		}
	}
	
	
	

}
