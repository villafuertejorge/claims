package com.soproen.claimsmodule.app.repository.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soproen.claimsmodule.app.model.catalog.ClDistrict;

@Repository
public interface ClDistrictRepository extends JpaRepository<ClDistrict,Long>{

}
