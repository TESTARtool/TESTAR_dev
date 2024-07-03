package org.testar.btrace;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class BtraceFinishRecordingResponse extends BtraceResponse {

    private List<MethodEntry> methodsExecuted;

    @Data
    @EqualsAndHashCode
    public static class MethodEntry {

        @JsonProperty("class")
        public String className;

        @JsonProperty("method")
        public String methodName;

        @JsonProperty("parameterTypes")
        public List<String> parameterTypes;
    }
}
