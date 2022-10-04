package nl.ou.testar.reporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Authorization {
    private String accessToken;
    private String refreshToken;
}
