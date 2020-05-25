package com.fnordfisch.i3connect.service;

import org.apache.commons.codec.Charsets;
import org.apache.commons.text.StrSubstitutor;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service for fetching access token to API
 */
@EnableScheduling
public class TokenService {

    public static final String HEADER_TEMPLATE = "${token_type} ${access_token}";
    public static final String CACHE_NAME = "token";

    @Autowired
    CacheManager cacheManager;

    private String url;
    private Map<String, String> queryParams;

    /**
     * Fetch token and create authorization header
     *
     * @param user service user
     * @param pass service password
     * @return the authorization header
     */
    @Cacheable(CACHE_NAME)
    public String fetchHeader(String user, String pass) {
        queryParams.put("username", user);
        queryParams.put("password", pass);
        String parameters = fetchLocationURI(queryParams).getFragment();
        Map<String, String> parameterMap = toMap(URLEncodedUtils.parse(parameters, Charsets.UTF_8));
        return StrSubstitutor.replace(HEADER_TEMPLATE, parameterMap);
    }

    /**
     * Evict Cache
     */
    @Scheduled(fixedDelay = 7200000)
    public void evictCache() {
        Objects.requireNonNull(cacheManager.getCache(CACHE_NAME)).clear();
    }

    /**
     * Fetch location URI with token data
     *
     * @param queryParams map containing necessary URL parameters
     * @return the location URI
     */
    protected URI fetchLocationURI(Map<String, String> queryParams) {
        String postdata = URLEncodedUtils.format(toList(queryParams), Charsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> request = new HttpEntity<>(postdata, headers);
        return new RestTemplate().postForLocation(url, request);
    }

    /**
     * Create NameValuePair list from Map
     *
     * @param map the Map
     * @return the List
     */
    private List<NameValuePair> toList(Map<String, String> map) {
        return map.entrySet().stream()
                .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Create Map from NameValuePair List
     *
     * @param list the List
     * @return the Map
     */
    private Map<String, String> toMap(List<NameValuePair> list) {
        return list.stream().collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
