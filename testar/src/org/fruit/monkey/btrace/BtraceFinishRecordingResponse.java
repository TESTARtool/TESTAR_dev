package org.fruit.monkey.btrace;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
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
    public static class MethodEntry {

        @JsonProperty("class")
        private String className;

        @JsonProperty("method")
        private String methodName;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MethodEntry that = (MethodEntry) o;
            return Objects.equals(className, that.className) && Objects.equals(methodName, that.methodName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(className, methodName);
        }
    }
}
