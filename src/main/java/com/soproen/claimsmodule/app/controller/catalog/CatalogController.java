package com.soproen.claimsmodule.app.controller.catalog;

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
import com.soproen.claimsdto.dto.catalog.ClClaimActionDTO;
import com.soproen.claimsdto.dto.catalog.ClClaimTypeDTO;
import com.soproen.claimsdto.dto.catalog.ClDistrictDTO;
import com.soproen.claimsdto.dto.catalog.ClProgramDTO;
import com.soproen.claimsdto.dto.catalog.ClSaveProgramWithClaimTypesDTO;
import com.soproen.claimsdto.dto.catalog.ClTaDTO;
import com.soproen.claimsdto.dto.catalog.ClTransferInstitutionDTO;
import com.soproen.claimsdto.dto.catalog.ClVillageDTO;
import com.soproen.claimsdto.dto.catalog.ClZoneDTO;
import com.soproen.claimsmodule.app.controller.AbstractParentController;
import com.soproen.claimsmodule.app.exceptions.ServiceException;
import com.soproen.claimsmodule.app.model.catalog.ClDistrict;
import com.soproen.claimsmodule.app.model.catalog.ClTa;
import com.soproen.claimsmodule.app.model.catalog.ClVillage;
import com.soproen.claimsmodule.app.service.catalog.CatalogService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/catalogs")
public class CatalogController extends AbstractParentController {

	@Autowired
	private CatalogService catalogService;

	@GetMapping("/retrieveAllPrograms")
	public ResponseEntity<?> retrieveAllPrograms() {
		try {
			List<ClProgramDTO> programList = utilities.mapObject(catalogService.retrieveAllPrograms(),
					new TypeReference<List<ClProgramDTO>>() {
					});
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", programList), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveAllPrograms = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Failed - retrieve all programs"),
					HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveAllClaimTypes")
	public ResponseEntity<?> retrieveAllClaimTypes() {
		try {
			List<ClClaimTypeDTO> programList = utilities.mapObject(catalogService.retrieveAllClaimTypes(),
					new TypeReference<List<ClClaimTypeDTO>>() {
					});
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", programList), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveAllClaimTypes = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Failed - retrieve all claim types"),
					HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveAllTransferInstitution")
	public ResponseEntity<?> retrieveAllTransferInstitution() {
		try {
			List<ClTransferInstitutionDTO> transferInstitutionList = utilities.mapObject(
					catalogService.retrieveAllTransferInstitutions(),
					new TypeReference<List<ClTransferInstitutionDTO>>() {
					});
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", transferInstitutionList),
					HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveAllTransferInstitution = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(
					super.responseError("Failed - retrieve all transfer institutions"), HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveAllDistricts")
	public ResponseEntity<?> retrieveAllDistricts() {
		try {

			List<ClDistrictDTO> districtList = utilities.mapObject(catalogService.retrieveAllDistricts(),
					new TypeReference<List<ClDistrictDTO>>() {
					});
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", districtList), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveAllDistricts = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Failed - retrieve all districts"),
					HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveAllTaByDistrict/{idClDistrict}")
	public ResponseEntity<?> retrieveAllTaByDistrict(
			@PathVariable(name = "idClDistrict", required = true) Long idClDistrict) {
		try {
			List<ClTaDTO> taList = utilities.mapObject(
					catalogService.retrieveAllTaByDistrict(ClDistrict.builder().id(idClDistrict).build()),
					new TypeReference<List<ClTaDTO>>() {
					});
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", taList), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveAllTaByDistrict = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Failed - retrieve all TA"),
					HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveAllVillagesByTa/{idClTa}")
	public ResponseEntity<?> retrieveAllVillagesByTa(@PathVariable(name = "idClTa", required = true) Long idClTa) {
		try {
			List<ClVillageDTO> villageList = utilities.mapObject(
					catalogService.retrieveAllVillageByTa(ClTa.builder().id(idClTa).build()),
					new TypeReference<List<ClVillageDTO>>() {
					});
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", villageList), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveAllVillagesByTa = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Failed - retrieve all Village"),
					HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveAllZonesByVillage/{idClVillage}")
	public ResponseEntity<?> retrieveAllZonesByVillage(
			@PathVariable(name = "idClVillage", required = true) Long idClVillage) {
		try {
			List<ClZoneDTO> zoneList = utilities.mapObject(
					catalogService.retrieveAllZoneByVillage(ClVillage.builder().id(idClVillage).build()),
					new TypeReference<List<ClZoneDTO>>() {
					});
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", zoneList), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveAllZonesByVillage = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Failed - retrieve all Zones"),
					HttpStatus.OK);
		}
	}

	@PostMapping("/saveProgramWithClaimTypes")
	public ResponseEntity<?> saveProgramWithClaimTypes(
			@Validated(BasicValidation.class) @Valid @RequestBody ClSaveProgramWithClaimTypesDTO clSaveProgramWithClaimTypesDTO,
			BindingResult bindingResult) {
		try {

			if (bindingResult.hasErrors()) {
				return new ResponseEntity<Map<String, Object>>(super.responseError("Validation Error", bindingResult),
						HttpStatus.OK);
			}
			catalogService.saveProgramWithClaimTypes(clSaveProgramWithClaimTypesDTO);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", null), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("saveProgramWithClaimTypes = {} ", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError(e.getMessage()), HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveClDistrictById/{idClDistrict}")
	public ResponseEntity<?> retrieveClDistrictById(
			@PathVariable(name = "idClDistrict", required = true) Long idClDistrict) {
		try {
			ClDistrictDTO district = utilities.mapObject(catalogService.findClDistrictById(idClDistrict),
					ClDistrictDTO.class);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", district), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveClDistrictById = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Retrieve District fail"),
					HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveClTaById/{idClTa}")
	public ResponseEntity<?> retrieveClTaById(@PathVariable(name = "idClTa", required = true) Long idClTa) {
		try {
			ClTaDTO ta = utilities.mapObject(catalogService.findClTaById(idClTa), ClTaDTO.class);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", ta), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveClTaById = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Retrieve TA fail"), HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveClVillageById/{idClVillage}")
	public ResponseEntity<?> retrieveClVillageById(
			@PathVariable(name = "idClVillage", required = true) Long idClVillage) {
		try {
			ClVillageDTO village = utilities.mapObject(catalogService.findClVillageById(idClVillage),
					ClVillageDTO.class);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", village), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveClVillageById = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Retrieve Village fail"), HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveClZoneById/{idClZone}")
	public ResponseEntity<?> retrieveClZoneById(@PathVariable(name = "idClZone", required = true) Long idClZone) {
		try {
			ClZoneDTO village = utilities.mapObject(catalogService.findClZoneById(idClZone), ClZoneDTO.class);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", village), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveClZoneById = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Retrieve Zone fail"), HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveClProgramById/{idClProgram}")
	public ResponseEntity<?> retrieveClProgramById(
			@PathVariable(name = "idClProgram", required = true) Long idClProgram) {
		try {
			ClProgramDTO village = utilities.mapObject(catalogService.findClProgramById(idClProgram),
					ClProgramDTO.class);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", village), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveClProgramById = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Retrieve Program fail"), HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveClClaimTypeById/{idClClaimType}")
	public ResponseEntity<?> retrieveClClaimTypeById(
			@PathVariable(name = "idClClaimType", required = true) Long idClClaimType) {
		try {
			ClClaimTypeDTO village = utilities.mapObject(catalogService.findClClaimTypeById(idClClaimType),
					ClClaimTypeDTO.class);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", village), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveClClaimTypeById = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Retrieve Claim Type fail"),
					HttpStatus.OK);
		}
	}
	
	@GetMapping("/retrieveClTransferInstitutionById/{idClTransferInstitution}")
	public ResponseEntity<?> retrieveClTransferInstitutionById(
			@PathVariable(name = "idClTransferInstitution", required = true) Long idClTransferInstitution) {
		try {
			ClTransferInstitutionDTO transferInstitution = utilities.mapObject(catalogService.findClTransferInstitutionById(idClTransferInstitution),
					ClTransferInstitutionDTO.class);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", transferInstitution), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveClTransferInstitutionById = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Retrieve Transfer Institution fail"),
					HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveAllClaimActions")
	public ResponseEntity<?> retrieveAllClaimActions() {
		try {
			List<ClClaimActionDTO> claimActionList = utilities.mapObject(catalogService.retrieveAllClaimActions(),
					new TypeReference<List<ClClaimActionDTO>>() {
					});
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", claimActionList), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveAllClaimTypes = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Failed - retrieve all claim actions"),
					HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveClClaimActionById/{idClClaimAction}")
	public ResponseEntity<?> retrieveClClaimActionById(
			@PathVariable(name = "idClClaimAction", required = true) Long idClClaimAction) {
		try {
			ClClaimActionDTO claimAction = utilities.mapObject(catalogService.findClClaimActionById(idClClaimAction),
					ClClaimActionDTO.class);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", claimAction), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveClClaimActionById = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Retrieve Claim Action fail"), HttpStatus.OK);
		}
	}
	
}
