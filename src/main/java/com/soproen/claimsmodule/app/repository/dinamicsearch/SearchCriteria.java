package com.soproen.claimsmodule.app.repository.dinamicsearch;

import com.soproen.claimsmodule.app.enums.DataBaseOperators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {

	private String attr;
	private Object value;
	private DataBaseOperators searchOperation;

}
