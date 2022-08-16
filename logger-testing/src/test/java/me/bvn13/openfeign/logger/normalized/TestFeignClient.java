package me.bvn13.openfeign.logger.normalized;


import feign.RequestLine;

public interface TestFeignClient {

    @RequestLine("GET /status")
    ResponseDto getStatus();
    
}
