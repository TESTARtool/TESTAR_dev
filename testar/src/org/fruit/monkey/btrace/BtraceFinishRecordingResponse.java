package org.fruit.monkey.btrace;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
public class BtraceFinishRecordingResponse extends BtraceResponse {

    private List<MethodEntry> methodsExecuted;

    @Data
    @EqualsAndHashCode
    public static class MethodEntry {

        @JsonProperty("class")
        private String className;

        @JsonProperty("method")
        private String methodName;

        @JsonProperty("parameterTypes")
        private List<String> parameterTypes;
    }
}
