package com.fnordfisch.i3connect.service;

import com.fnordfisch.i3connect.model.ApiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Service for fetching data from API
 */
@EnableScheduling
public class DataService {

    public static final String AUTHORIZATION = "Authorization";
    public static final String CACHE_NAME = "data";
    public static final String API_BASEURL = "https://www.bmw-connecteddrive.de/api/vehicle/";

    @Autowired
    TokenService tokenService;

    @Autowired
    CacheManager cacheManager;

    @Value(API_BASEURL)
    String baseUrl;

    Map<ApiFunction, String> functions;

    /**
     * Fetch data from API urls
     *
     * @param function the function
     * @param user     the user
     * @param pass     the password
     * @param vin      the vin
     * @return the data
     */
    @Cacheable(CACHE_NAME)
    public Optional<String> fetchData(ApiFunction function, String user, String pass, String vin) {
        String url = baseUrl + functions.get(function) + vin;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Collections.singletonList(new StringHttpMessageConverter()));
        restTemplate.setInterceptors(Collections.singletonList(new AddAuthorizationHeaderInterceptor(user, pass)));
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if (responseEntity.getStatusCode().is2xxSuccessful())
            return Optional.of(Objects.requireNonNull(responseEntity.getBody()));
        return Optional.empty();
    }

    /**
     * Evict cache
     */
    @Scheduled(fixedDelay = 300000)
    public void evictCache() {
        Objects.requireNonNull(cacheManager.getCache(CACHE_NAME)).clear();
    }

    /**
     * Set supported api functions
     *
     * @param functions map of supported functions
     */
    public void setFunctions(Map<ApiFunction, String> functions) {
        this.functions = functions;
    }

    /**
     * Interceptor to add authorization header
     */
    private class AddAuthorizationHeaderInterceptor
            implements ClientHttpRequestInterceptor {

        final String user;
        final String pass;

        public AddAuthorizationHeaderInterceptor(String user, String pass) {
            this.user = user;
            this.pass = pass;
        }

        /**
         * Fetch credentials from tokenService, add to request headers
         *
         * @param request   the request
         * @param body      the body
         * @param execution the execution
         * @return the response
         * @throws IOException exception on execution
         */
        @Override
        public ClientHttpResponse intercept(
                HttpRequest request,
                byte[] body,
                ClientHttpRequestExecution execution) throws IOException {
            request.getHeaders().add(AUTHORIZATION, tokenService.fetchHeader(user, pass));
            return execution.execute(request, body);
        }
    }
}
