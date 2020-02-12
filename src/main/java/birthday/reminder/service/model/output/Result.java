package birthday.reminder.service.model.output;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class Result<P> {

    private P payload;
    private int statusCode;
    private ErrorPayload errorPayload;

    public Result(P payload, int statusCode) {
        this.payload = payload;
        this.statusCode = statusCode;
    }

    public Result(int statusCode, String errorMessage) {
        this.statusCode = statusCode;
        this.errorPayload = new ErrorPayload(errorMessage);
    }

    public static <P> Result<P> created(P payload) {
        return new Result<>(payload, HttpStatus.CREATED.value());
    }

    public static <P> Result<P> conflict(String errorMessage) {
        return new Result<>(HttpStatus.CONFLICT.value(), errorMessage);
    }
}
