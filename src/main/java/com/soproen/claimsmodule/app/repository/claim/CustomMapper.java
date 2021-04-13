package com.soproen.claimsmodule.app.repository.claim;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.soproen.claimsdto.dto.claim.ClClaimDTO;
import com.soproen.claimsmodule.app.model.claim.ClClaim;

@Mapper(componentModel = "spring")
public interface CustomMapper {

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCustomerFromDto(ClClaimDTO dto, @MappingTarget ClClaim entity);
}
