package kz.iitu.springab.web;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(assignableTypes = BookRestController.class)
public class BookApiExceptionHandler extends ResponseEntityExceptionHandler {
}