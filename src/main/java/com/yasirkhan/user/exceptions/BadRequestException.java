package com.yasirkhan.user.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadRequestException extends RuntimeException{

    private String message;
    private HttpStatus status;

    public BadRequestException(String message){
        super(message);
        this.message = message;
    }
}
