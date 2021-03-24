package com.soproen.claimsmodule.app.repository.catalog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soproen.claimsmodule.app.model.catalog.ClVillage;
import com.soproen.claimsmodule.app.model.catalog.ClZone;

@Repository
public interface ClZoneRepository  extends JpaRepository<ClZone,Long>{

	List<ClZone> findAllByClVillage(ClVillage clVillage);

}
