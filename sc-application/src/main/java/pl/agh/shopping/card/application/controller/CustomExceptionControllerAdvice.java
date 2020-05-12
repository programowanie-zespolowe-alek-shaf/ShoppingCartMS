package pl.agh.shopping.card.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.agh.shopping.card.common.exception.BadRequestException;
import pl.agh.shopping.card.common.exception.CustomException;
import pl.agh.shopping.card.common.exception.NotFoundException;
import pl.agh.shopping.card.common.response.CustomExceptionResponse;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

@ControllerAdvice
public class CustomExceptionControllerAdvice {

    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomExceptionResponse handleBadRequestException(CustomException se) {
        return new CustomExceptionResponse(singletonList(se.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomExceptionResponse handleNotFoundException(CustomException se) {
        return new CustomExceptionResponse(singletonList(se.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomExceptionResponse handleBadRequestException(MethodArgumentNotValidException e) {
        List<String> errorString = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> format("%s=[%s] -> %s", error.getField(), error.getRejectedValue(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new CustomExceptionResponse(errorString);
    }
}
