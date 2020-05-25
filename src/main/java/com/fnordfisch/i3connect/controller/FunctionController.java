package com.fnordfisch.i3connect.controller;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.fnordfisch.i3connect.model.ApiFunction;
import com.fnordfisch.i3connect.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Optional;

/**
 * Controller passing through responses for supported API functions
 */
@Controller
@RequestMapping(value = "/data")
public class FunctionController {

    @Autowired
    DataService dataService;

    /**
     * Get responses for supported API functions
     *
     * @param functionName name of supported function
     * @param user         service user
     * @param pass         service password
     * @param vin          vehicle information number
     * @return response entity of data from corresponding api function
     */
    @GetMapping(value = "/{function}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonElement> data(
            @PathVariable("function") String functionName,
            @RequestHeader("user") String user,
            @RequestHeader("pass") String pass,
            @RequestHeader("vin") String vin) {
        Optional<ApiFunction> function = Arrays.stream(ApiFunction.values())
                .filter(apiFunction -> apiFunction.getFunctionName().equals(functionName))
                .findFirst();
        if (function.isPresent()) {
            Optional<String> data = dataService.fetchData(function.get(), user, pass, vin);
            if (data.isPresent()) {
                final JsonParser parser = new JsonParser();
                JsonElement json = parser.parse(data.get());
                return ResponseEntity.ok(json);
            }
        }
        return ResponseEntity.notFound().build();
    }
}

