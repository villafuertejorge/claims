package com.soproen.claimsmodule.app.controller.claim;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soproen.claimsdto.dto.BasicValidation;
import com.soproen.claimsdto.dto.claim.ClClaimDTO;
import com.soproen.claimsdto.dto.claim.RegisterNewClaimForHouseholdDTO;
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
			ClClaimDTO clClaimDTO = utilities.mapObject(claimService.registerNewClaimForHousehold(registerNewClaimForHouseholdDTO),ClClaimDTO.class);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", clClaimDTO), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("registerNewClaimForHousehold = {} ", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError(e.getMessage()), HttpStatus.OK);
		}
	}
	
}