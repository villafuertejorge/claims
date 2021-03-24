package com.soproen.claimsmodule.app.repository.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soproen.claimsmodule.app.model.catalog.ClProgram;

@Repository
public interface ClProgramRepository extends JpaRepository<ClProgram,Long>{

}
