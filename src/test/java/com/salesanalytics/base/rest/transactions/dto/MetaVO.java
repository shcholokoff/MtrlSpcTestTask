package com.salesanalytics.base.rest.transactions.dto;

import java.util.Map;

import lombok.Data;

@Data
public class MetaVO {

	private Integer total;

	private Integer returned;

	private Map<String, String> filters;
}
