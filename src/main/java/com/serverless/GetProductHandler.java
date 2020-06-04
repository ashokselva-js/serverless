package com.serverless;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.ObjectUtils;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.config.SpringConfig;
import com.serverless.dal.Product;
import com.serverless.service.ProductService;
import com.serverless.service.impl.ProductServiceImpl;

public class GetProductHandler extends AbstractHandler<SpringConfig>
		implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Logger logger = Logger.getLogger(this.getClass());

	ProductService productService = getApplicationContext().getBean(ProductServiceImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

		try {
			logger.debug("Rest request to get products :" + input);
			Map<String, String> pathParameters = (Map<String, String>) input.get("pathParameters");
			String productId = pathParameters.get("id");
			List<Product> result = productService.findById(productId);
			if (!ObjectUtils.isEmpty(result)) {
				return ApiGatewayResponse.builder().setStatusCode(200).setObjectBody(result).build();
			} else {
				return ApiGatewayResponse.builder().setStatusCode(404)
						.setFailureMessage("No records found").build();
			}

		} catch (Exception ex) {
			logger.error("Error in retrieving product: " + ex);
			Response responseBody = new Response("Error in retrieving product: ", input);
			return ApiGatewayResponse.builder().setStatusCode(500).setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
		}
	}

//	private void authenticate() {
//		AWSCognitoIdentityProvider cognito = AWSCognitoIdentityProviderClientBuilder.defaultClient();
//		List<UserPoolDescriptionType> userPools = cognito.listUserPools(new ListUserPoolsRequest().withMaxResults(20))
//				.getUserPools();
//		String userPoolId = userPools.get(0).getId();
//		ListUserPoolClientsResult response = cognito
//				.listUserPoolClients(new ListUserPoolClientsRequest().withUserPoolId(userPoolId).withMaxResults(1));
//
//		UserPoolClientType userPool = cognito.describeUserPoolClient(new DescribeUserPoolClientRequest()
//				.withUserPoolId(userPoolId).withClientId(response.getUserPoolClients().get(0).getClientId()))
//				.getUserPoolClient();
//	}
}
