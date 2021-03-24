package com.soproen.claimsmodule.app.service.catalog;

import java.util.List;

import com.soproen.claimsdto.dto.catalog.ClSaveProgramWithClaimTypesDTO;
import com.soproen.claimsmodule.app.exceptions.ServiceException;
import com.soproen.claimsmodule.app.model.catalog.ClClaimType;
import com.soproen.claimsmodule.app.model.catalog.ClDistrict;
import com.soproen.claimsmodule.app.model.catalog.ClProgram;
import com.soproen.claimsmodule.app.model.catalog.ClTa;
import com.soproen.claimsmodule.app.model.catalog.ClTransferInstitution;
import com.soproen.claimsmodule.app.model.catalog.ClVillage;
import com.soproen.claimsmodule.app.model.catalog.ClZone;

public interface CatalogService {

	List<ClProgram> retrieveAllPrograms() throws ServiceException;

	void saveProgramWithClaimTypes(ClSaveProgramWithClaimTypesDTO clSaveProgramWithClaimTypesDTO)
			throws ServiceException;

	List<ClClaimType> retrieveAllClaimTypes() throws ServiceException;

	List<ClDistrict> retrieveAllDistricts() throws ServiceException;
	
	List<ClTa> retrieveAllTaByDistrict(ClDistrict clDistrict) throws ServiceException;
	
	List<ClVillage> retrieveAllVillageByTa(ClTa clTa) throws ServiceException;
	
	List<ClZone> retrieveAllZoneByVillage(ClVillage clVillage) throws ServiceException;
	
	ClDistrict findClDistrictById(Long id) throws ServiceException;
	
	ClTa findClTaById(Long id) throws ServiceException;
	
	ClVillage findClVillageById(Long id) throws ServiceException;
	
	ClZone findClZoneById(Long id) throws ServiceException;
	
	ClProgram findClProgramById(Long id) throws ServiceException;
	
	ClClaimType findClClaimTypeById(Long id) throws ServiceException;

	List<ClTransferInstitution> retrieveAllTransferInstitutions() throws ServiceException;

	ClTransferInstitution findClTransferInstitutionById(Long id) throws ServiceException;

}
