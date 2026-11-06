package com.salesanalytics.rest.steps.context;

import java.util.List;

import com.salesanalytics.base.rest.transactions.dto.TransactionsQueryVO;
import io.restassured.response.Response;
import lombok.Value;

@Value
public class TransactionsRequestResult {

	TransactionsQueryVO query;

	List<String> expectedOrderIds;

	Response response;
}
