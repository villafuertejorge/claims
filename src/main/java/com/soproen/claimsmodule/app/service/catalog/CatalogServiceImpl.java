package com.soproen.claimsmodule.app.service.catalog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soproen.claimsdto.dto.catalog.ClSaveProgramWithClaimTypesDTO;
import com.soproen.claimsmodule.app.exceptions.ServiceException;
import com.soproen.claimsmodule.app.model.catalog.ClClaimType;
import com.soproen.claimsmodule.app.model.catalog.ClDistrict;
import com.soproen.claimsmodule.app.model.catalog.ClProgram;
import com.soproen.claimsmodule.app.model.catalog.ClTa;
import com.soproen.claimsmodule.app.model.catalog.ClTransferInstitution;
import com.soproen.claimsmodule.app.model.catalog.ClVillage;
import com.soproen.claimsmodule.app.model.catalog.ClZone;
import com.soproen.claimsmodule.app.repository.catalog.ClClaimTypeRepository;
import com.soproen.claimsmodule.app.repository.catalog.ClDistrictRepository;
import com.soproen.claimsmodule.app.repository.catalog.ClProgramRepository;
import com.soproen.claimsmodule.app.repository.catalog.ClTaRepository;
import com.soproen.claimsmodule.app.repository.catalog.ClTransferInstitutionRepository;
import com.soproen.claimsmodule.app.repository.catalog.ClVillageRepository;
import com.soproen.claimsmodule.app.repository.catalog.ClZoneRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CatalogServiceImpl implements CatalogService {

	@Autowired
	private ClProgramRepository clProgramRepository;
	@Autowired
	private ClClaimTypeRepository clClaimTypeRepository;
	@Autowired
	private ClDistrictRepository clDistrictRepository;
	@Autowired
	private ClTaRepository clTaRepository;
	@Autowired
	private ClVillageRepository clVillageRepository;
	@Autowired
	private ClZoneRepository clZoneRepository;
	@Autowired
	private ClTransferInstitutionRepository clTransferInstitutionRepository;

	@Override
	@Transactional(readOnly = true)
	public List<ClProgram> retrieveAllPrograms() throws ServiceException {
		try {
			return clProgramRepository.findAll();
		} catch (DataAccessException e) {
			log.error("retrieveAllPrograms = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ClTransferInstitution> retrieveAllTransferInstitutions() throws ServiceException {
		try {
			return clTransferInstitutionRepository.findAll();
		} catch (DataAccessException e) {
			log.error("retrieveAllTransferInstitutions = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ClClaimType> retrieveAllClaimTypes() throws ServiceException {
		try {
			return clClaimTypeRepository.findAll();
		} catch (DataAccessException e) {
			log.error("retrieveAllClaimTypes = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public void saveProgramWithClaimTypes(ClSaveProgramWithClaimTypesDTO clSaveProgramWithClaimTypesDTO)
			throws ServiceException {
		try {
			ClProgram clProgram = clProgramRepository.findById(clSaveProgramWithClaimTypesDTO.getClProgram().getId())
					.orElseThrow(() -> new ServiceException("Program not found"));
			System.out.println(clProgram);
			clProgram.getClClaimTypes().clear();
			clSaveProgramWithClaimTypesDTO.getClClaimTypes().stream().forEach(obj -> {
				System.out.println(obj.toString());
				clProgram.getClClaimTypes().add(clClaimTypeRepository.findById(obj.getId()).get());
			});
			System.out.println(clProgram);
			clProgramRepository.save(clProgram);
		} catch (DataAccessException e) {
			log.error("saveProgramWithClaimTypes = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}

	}

	@Override
	@Transactional(readOnly = true)
	public List<ClDistrict> retrieveAllDistricts() throws ServiceException {
		try {
			return clDistrictRepository.findAll();
		} catch (DataAccessException e) {
			log.error("retrieveAllDistricts = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ClTa> retrieveAllTaByDistrict(ClDistrict clDistrict) throws ServiceException {
		try {
			return clTaRepository.findAllByClDistrict(clDistrict);
		} catch (DataAccessException e) {
			log.error("retrieveAllTaByDistrict = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ClVillage> retrieveAllVillageByTa(ClTa clTa) throws ServiceException {
		try {
			return clVillageRepository.findAllByClTa(clTa);
		} catch (DataAccessException e) {
			log.error("retrieveAllVillageByTa = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ClZone> retrieveAllZoneByVillage(ClVillage clVillage) throws ServiceException {
		try {
			return clZoneRepository.findAllByClVillage(clVillage);
		} catch (DataAccessException e) {
			log.error("retrieveAllZoneByVillage = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ClDistrict findClDistrictById(Long id) throws ServiceException {
		try {
			return clDistrictRepository.findById(id).get();
		} catch (DataAccessException e) {
			log.error("findClDistrictById = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ClTa findClTaById(Long id) throws ServiceException {
		try {
			return clTaRepository.findById(id).get();
		} catch (DataAccessException e) {
			log.error("findClTaById = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ClVillage findClVillageById(Long id) throws ServiceException {
		try {
			return clVillageRepository.findById(id).get();
		} catch (DataAccessException e) {
			log.error("findClVillageById = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ClZone findClZoneById(Long id) throws ServiceException {
		try {
			return clZoneRepository.findById(id).get();
		} catch (DataAccessException e) {
			log.error("findClZoneById = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ClProgram findClProgramById(Long id) throws ServiceException {
		try {
			return clProgramRepository.findById(id).get();
		} catch (DataAccessException e) {
			log.error("findClProgramById = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ClClaimType findClClaimTypeById(Long id) throws ServiceException {
		try {
			return clClaimTypeRepository.findById(id).get();
		} catch (DataAccessException e) {
			log.error("findClClaimTypeById = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public ClTransferInstitution findClTransferInstitutionById(Long id) throws ServiceException {
		try {
			return clTransferInstitutionRepository.findById(id).get();
		} catch (DataAccessException e) {
			log.error("findClTransferInstitutionById = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

}
