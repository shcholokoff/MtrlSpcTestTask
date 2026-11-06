package com.salesanalytics.base.rest.transactions.dto;

import java.util.List;

import lombok.Data;

@Data
public class TransactionsResponse {

	private List<TransactionVO> data;

	private MetaVO meta;
}
