package com.soproen.claimsmodule.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.soproen.claimsdto.dto.claim.RegisterNewClaimActionDTO;
import com.soproen.claimsmodule.app.model.catalog.ClProgram;
import com.soproen.claimsmodule.app.model.claim.ClHouseholdClaim;
import com.soproen.claimsmodule.app.service.claim.ClaimService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
class ClaimsModuleApplicationTests {

	
	@Autowired
	private ClaimService claimService;
	
	//@Test
	void registerClaimValueInPaymentModule() {
		
		RegisterNewClaimActionDTO registerNewClaimActionDTO = RegisterNewClaimActionDTO.builder()
		.amountToBeTransferred(101.25)
		.createdByUsername("javg")
		.build();
		
		ClHouseholdClaim clHouseholdClaim = ClHouseholdClaim.builder()
				.householdCode("hh1_code55")
				.clProgram(ClProgram.builder().id(2L).build())
				.build();
		
		claimService.registerClaimValueInPaymentModule(registerNewClaimActionDTO,clHouseholdClaim);
	}

}
