package nl.ou.testar.reporter.model;

import lombok.Builder;
import lombok.Data;

/**
 * PostEntityResponse
 */

@Data
@Builder
public class PostEntityResponse {
    private Integer id;
    private String uri;
    private String message;
}
