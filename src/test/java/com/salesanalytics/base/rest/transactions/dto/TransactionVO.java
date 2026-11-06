package com.salesanalytics.base.rest.transactions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class TransactionVO {

	private String id;

	private String date;

	private String region;

	private String category;

	private Integer revenue;
}
