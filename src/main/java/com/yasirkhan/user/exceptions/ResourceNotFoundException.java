package com.yasirkhan.user.exceptions;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceNotFoundException extends RuntimeException{

    private String message;
    private HttpStatus status;

    public ResourceNotFoundException(String message){
        super(message);
        this.message = message;
    }
}
