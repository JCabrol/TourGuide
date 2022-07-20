package tourGuide.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
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

