/**
 * 
 */
package com.serverless.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.serverless.dal.DynamoDBAdapter;
import com.serverless.dal.Product;
import com.serverless.service.ProductService;

/**
 * @author Mallikarjuna
 *
 */
@Component
@Service
public class ProductServiceImpl implements ProductService {

	AmazonDynamoDB dynamoDb = AmazonDynamoDBClientBuilder.standard().build();
//	DynamoDB client = new DynamoDB(dynamoDb);
	private static final String PRODUCTS_TABLE_NAME = "java-products-dev";
	private final DynamoDBMapper mapper = DynamoDBAdapter.getInstance().createDbMapper(DynamoDBMapperConfig.builder()
			.withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(PRODUCTS_TABLE_NAME)).build());

	private final Logger logger = Logger.getLogger(this.getClass());

	@Override
	public Product save(Product product) {
		try {
			mapper.save(product);
			return product;
		} catch (Exception e) {
			logger.debug(e);
			return null;
		}

	}

	@Override
	public List<Product> findAll() {
		logger.debug("repo: ");
		return new ArrayList<Product>();
	}

	@Override
	public List<Product> findById(String productId) {
		List<Map<String, AttributeValue>> items = new ArrayList<>();
		Map<String, String> expressionAttributesNames = new HashMap<>();
		expressionAttributesNames.put("#id", "id");

		Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
		expressionAttributeValues.put(":productId", new AttributeValue().withS(productId));

		QueryRequest queryRequest = new QueryRequest().withTableName(PRODUCTS_TABLE_NAME)
				.withKeyConditionExpression("#id = :productId ").withExpressionAttributeNames(expressionAttributesNames)
				.withExpressionAttributeValues(expressionAttributeValues);

		Map<String, AttributeValue> lastKey = null;
		do {

			QueryResult queryResult = dynamoDb.query(queryRequest);
			List<Map<String, AttributeValue>> results = queryResult.getItems();
			items.addAll(results);
			lastKey = queryResult.getLastEvaluatedKey();
			queryRequest.setExclusiveStartKey(lastKey);
		} while (lastKey != null);

		logger.debug(items);

		List<Product> result = new ArrayList<>();
		for (Map<String, AttributeValue> item : items) {
			Product product = new Product();
			product.setName(item.get("name") != null ? item.get("name").getS() : null);
			product.setPrice(item.get("price") != null ? Float.valueOf(item.get("price").getN()) : null);
			product.setId(item.get("id") != null ? item.get("id").getS() : null);
			result.add(product);
		}
		return result;
	}

}
