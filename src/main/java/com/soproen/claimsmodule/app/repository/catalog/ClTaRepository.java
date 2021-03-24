package com.soproen.claimsmodule.app.repository.catalog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soproen.claimsmodule.app.model.catalog.ClDistrict;
import com.soproen.claimsmodule.app.model.catalog.ClTa;

@Repository
public interface ClTaRepository extends JpaRepository<ClTa,Long>{

	List<ClTa> findAllByClDistrict(ClDistrict clDistrict);

}
