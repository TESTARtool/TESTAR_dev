package org.fruit.monkey.btrace;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class MethodInvocation {

    private String className;

    private String methodName;

    private List<String> parameterTypes;
    private long times;
}
