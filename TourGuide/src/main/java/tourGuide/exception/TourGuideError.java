package tourGuide.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
class TourGuideError {

    private HttpStatus status;

    private String message;

    TourGuideError(HttpStatus status) {
        this.status = status;
    }

    TourGuideError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}

