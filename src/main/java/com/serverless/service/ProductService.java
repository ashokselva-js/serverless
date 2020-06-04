/**
 * 
 */
package com.serverless.service;

import java.util.List;

import com.serverless.dal.Product;

/**
 * @author Mallikarjuna
 *
 */
public interface ProductService {

	List<Product> findAll();

	List<Product> findById(String id);

	Product save(Product product);

}
