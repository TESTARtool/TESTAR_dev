package org.testar.btrace;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class MethodInvocation {

    public String className;

    public String methodName;

    public List<String> parameterTypes;

    public long times;
}
