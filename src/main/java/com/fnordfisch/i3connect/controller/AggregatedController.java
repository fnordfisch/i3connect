package com.fnordfisch.i3connect.controller;

import com.fnordfisch.i3connect.service.AggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.Optional;

/**
 * Controller producing aggregated data responses
 */
@Controller
@RequestMapping(value = "/aggregated")
public class AggregatedController {

  @Autowired
  AggregationService aggregationService;

  /**
   * Get aggregated Data for given credentials
   *
   * @param user service user
   * @param pass service password
   * @param vin  vehicle identification number
   * @return response entity of aggregated data as flat json
   */
  @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> aggregated(
          @RequestHeader("user") String user,
          @RequestHeader("pass") String pass,
          @RequestHeader("vin") String vin) {
    Optional<Map<String, Object>> data = aggregationService.fetchData(user, pass, vin);
    return data.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }
}
