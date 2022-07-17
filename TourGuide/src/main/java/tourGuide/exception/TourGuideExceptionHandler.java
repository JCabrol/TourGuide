package tourGuide.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class TourGuideExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> buildResponseEntity(TourGuideError tourGuideError) {
        return new ResponseEntity<>(tourGuideError.getMessage(), tourGuideError.getStatus());
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            @NonNull NoHandlerFoundException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {

        String errorMessage = "The researched page was not found : "+ex.getMessage();
        log.error(errorMessage);
        return buildResponseEntity(new TourGuideError(NOT_FOUND, errorMessage));
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            @NonNull HttpMessageNotReadableException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        String errorMessage = "The request is not correct: there is a problem with the request's body.\n";
        log.error(errorMessage);
        return buildResponseEntity(new TourGuideError(HttpStatus.BAD_REQUEST, errorMessage));
    }


    @Override
    @NonNull
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            @NonNull MissingServletRequestParameterException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        String errorMessage = "The request is not correct : a request parameter is missing or wrong.\n";
        log.error(errorMessage);
        return buildResponseEntity(new TourGuideError(HttpStatus.BAD_REQUEST, errorMessage));
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    protected ResponseEntity<Object> handleObjectNotFound(
            ObjectNotFoundException ex) {
        TourGuideError tourGuideError = new TourGuideError(NOT_FOUND);
        tourGuideError.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return buildResponseEntity(tourGuideError);
    }

    @ExceptionHandler(ObjectAlreadyExistingException.class)
    protected ResponseEntity<Object> handleObjectAlreadyExisting(
            ObjectAlreadyExistingException ex) {
        TourGuideError tourGuideError = new TourGuideError(BAD_REQUEST);
        tourGuideError.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return buildResponseEntity(tourGuideError);
    }
}

