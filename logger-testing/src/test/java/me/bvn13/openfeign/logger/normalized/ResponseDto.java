package me.bvn13.openfeign.logger.normalized;

public class ResponseDto {

    private String status;

    public String getStatus() {
        return status;
    }

    public ResponseDto setStatus(String status) {
        this.status = status;
        return this;
    }
}
