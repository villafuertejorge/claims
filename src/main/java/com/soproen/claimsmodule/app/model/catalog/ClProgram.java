package com.soproen.claimsmodule.app.model.catalog;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.soproen.claimsmodule.app.enums.YesNoEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cl_programs")
public class ClProgram implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	private String name;
	
	@Column(name="is_editable")
	@Enumerated(EnumType.STRING)
	private YesNoEnum isEditable;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "cl_program_claim_types", joinColumns = @JoinColumn(name = "program_id"), 
	inverseJoinColumns = @JoinColumn(name = "claim_type_id"), uniqueConstraints = {	
			@UniqueConstraint(columnNames = { "program_id", "claim_type_id" }) })
	private List<ClClaimType> clClaimTypes;
	
	

}
