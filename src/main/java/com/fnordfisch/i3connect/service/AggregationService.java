package com.fnordfisch.i3connect.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.fnordfisch.i3connect.model.ApiFunction;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Service for aggregating Data from API
 */
public class AggregationService {

  public static final String KEY_FUNCTION_DELIMITER = "_";
  DataService dataService;
  Map<ApiFunction, Map<String, String>> extractablesByFunction;

  /**
   * Fetch aggregated data
   *
   * @param user bmw connected user
   * @param pass bmw connected password
   * @param vin  vehicle identification number
   * @return Optional of aggregated data
   * TODO: Error Handling
   */
  public Optional<Map<String, Object>> fetchData(String user, String pass, String vin) {
    Map<String, Object> result = new TreeMap<>();
    for (ApiFunction apiFunction : extractablesByFunction.keySet()) {
      Optional<String> stringOptional = dataService.fetchData(apiFunction, user, pass, vin);
      stringOptional.ifPresent(s -> result.putAll(extractByFunction(
              apiFunction.getFunctionName(),
              extractablesByFunction.get(apiFunction),
              JsonPath.parse(s))));
    }
    if (result.isEmpty())
      return Optional.empty();
    return Optional.of(result);
  }

  /**
   * Extracts Extractable Values from Document Context.
   *
   * @param function        ApiFunction
   * @param extractables    Map of extractable Values.
   *                        Key is used as suffix for result map key.
   *                        Value is jsonPath used for extraction
   * @param documentContext Document Context for json data source.
   * @return Map of extracted values. Key is build from function name, delimiter and key of extractable map.
   * TODO: Error Handling
   */
  private Map<String, Object> extractByFunction(String function, Map<String, String> extractables, DocumentContext documentContext) {
    Map<String, Object> result = new TreeMap<>();
    for (String key : extractables.keySet()) {
      String resultKey = function + KEY_FUNCTION_DELIMITER + key;
      Object extractValue = documentContext.read(extractables.get(key));
      if (extractValue instanceof JSONArray && !((JSONArray) extractValue).isEmpty())
        extractValue = ((JSONArray) extractValue).get(0);
      if (extractValue instanceof Number)
        result.put(resultKey, extractValue);
      else if (extractValue instanceof String
              && NumberUtils.isCreatable((String) extractValue)) {
        result.put(
                resultKey,
                ((String) extractValue).contains(".") ? NumberUtils.createFloat((String) extractValue) : NumberUtils.createNumber((String) extractValue));
      } else
        result.put(resultKey, extractValue);
    }
    return result;
  }

  public void setDataService(DataService dataService) {
    this.dataService = dataService;
  }

  public void setExtractablesByFunction(Map<ApiFunction, Map<String, String>> extractablesByFunction) {
    this.extractablesByFunction = extractablesByFunction;
  }
}
