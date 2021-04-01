package com.soproen.claimsmodule.app.repository.dinamicsearch;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class SearchData<T> implements Specification<T> {

	private static final long serialVersionUID = 1L;
	private List<SearchCriteria> searchCriteriaList;

	public SearchData() {
		this.searchCriteriaList = new ArrayList<>();
	}

	public void add(SearchCriteria criteria) {
		searchCriteriaList.add(criteria);
	}

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		List<Predicate> predicates = new ArrayList<>();
		for (SearchCriteria criteria : searchCriteriaList) {
			switch (criteria.getSearchOperation()) {
			case GREATER_THAN:
				predicates.add(builder.greaterThan(root.get(criteria.getAttr()), criteria.getValue().toString()));
				break;
			case LESS_THAN:
				predicates.add(builder.lessThan(root.get(criteria.getAttr()), criteria.getValue().toString()));
				break;
			case GREATER_THAN_EQUAL:
				predicates.add(
						builder.greaterThanOrEqualTo(root.get((criteria.getAttr())), criteria.getValue().toString()));
				break;
			case LESS_THAN_EQUAL:
				predicates.add(builder.lessThanOrEqualTo(root.get(criteria.getAttr()), criteria.getValue().toString()));
				break;
			case NOT_EQUAL:
				predicates.add(builder.notEqual(root.get(criteria.getAttr()), criteria.getValue()));
				break;
			case EQUAL:
				predicates.add(builder.equal(root.get(criteria.getAttr()), criteria.getValue()));
				break;
			case MATCH:
				predicates.add(builder.like(builder.lower(root.get(criteria.getAttr())),
						"%" + criteria.getValue().toString().toLowerCase() + "%"));
				break;
			case MATCH_END:
				predicates.add(builder.like(builder.lower(root.get(criteria.getAttr())),
						criteria.getValue().toString().toLowerCase() + "%"));
				break;
			case MATCH_START:
				predicates.add(builder.like(builder.lower(root.get(criteria.getAttr())),
						"%" + criteria.getValue().toString().toLowerCase()));
				break;
			case IN:
				predicates.add(builder.in(root.get(criteria.getAttr())).value(criteria.getValue()));
				break;
			case NOT_IN:
				predicates.add(builder.not(root.get(criteria.getAttr())).in(criteria.getValue()));
			}
		}
		return builder.and(predicates.toArray(new Predicate[0]));
	}

}
