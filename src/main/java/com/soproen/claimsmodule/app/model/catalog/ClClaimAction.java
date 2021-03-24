package com.soproen.claimsmodule.app.model.catalog;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cl_claim_actions")
public class ClClaimAction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	private String name;

}
