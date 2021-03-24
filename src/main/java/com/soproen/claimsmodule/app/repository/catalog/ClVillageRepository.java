package com.soproen.claimsmodule.app.repository.catalog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soproen.claimsmodule.app.model.catalog.ClTa;
import com.soproen.claimsmodule.app.model.catalog.ClVillage;

@Repository
public interface ClVillageRepository extends JpaRepository<ClVillage,Long>{

	List<ClVillage> findAllByClTa(ClTa clTa);

}
