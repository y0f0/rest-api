package ru.yandex.megamarket.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;

/**
 * Error contains HttpStatus code and message for response.
 */
@Getter
@Setter
public class Error {
    @NotNull
    private Integer code;

    @NotNull
    private String message;

    public Error(HttpStatus code, String message) {
        this.code = code.value();
        this.message = message;
    }
}
