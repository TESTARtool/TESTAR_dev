# TESTAR Rascal Oracle Module

## Objective

The Rascal module provides the DSL tooling for authoring TESTAR oracles and generating Java oracle classes that can run through the normal TESTAR oracle pipeline.

The intended workflow is:

1. Write a `.testar` oracle DSL file.
2. Validate the DSL file against the TESTAR oracle model.
3. Generate a Java oracle class.
4. Compile and enable the generated Java oracle through the workspace `ExtendedOracles` setting.

## Current Capabilities

- Parses TESTAR oracle DSL files.
- Validates DSL files with `testar.model`.
- Generates Java oracle source code compatible with `org.testar.oracle.DslOracle`.
- Generates `List<Verdict> getVerdicts(State state)` implementations.
- Supports oracle application status through `initialize()`, `resetApplicationStatus()`, and `markAsNonVacuous()`.
- Exposes Rascal bridge functions for Java and WebStudio integration.
- Includes Java helper classes for loading, validating, and generating DSL oracles.
- Exposes a `DslOracleCompiler` facade that can be used by modules like WebStudio.

## Workspace Layout

Workspace DSL and generated Java oracles should use this structure:

```text
settings/<workspace>/oracles/
  dsl/
  java/
  compiled/
```

Expected responsibilities:

- `dsl/` contains user-authored `.testar` DSL files.
- `java/` contains manually written Java oracles and Java files generated from DSL.
- `compiled/` contains compiled workspace oracle classes.
- `ExtendedOracles` stores the enabled Java oracle class names.

Generated Java oracles participate in the same enable, disable, compile, and execution flow as manually written Java oracles.

## Module Layout

- `testar-oracle/lang/testar/Oracle.rsc`: DSL grammar.
- `testar-oracle/lang/testar/Check.rsc`: DSL validation.
- `testar-oracle/lang/testar/Compiler.rsc`: Java source generator.
- `testar-oracle/lang/testar/Bridge.rsc`: Java-facing parse, validate, and compile entry points.
- `testar-oracle/lang/testar/testar.model`: validation model.
- `src/org/testar/rascal`: Java loader and helper classes used by WebStudio integration.

## WebStudio Integration

WebStudio exposes DSL oracle files in the Test Oracles view.

The integration should allow users to:

- create and edit `.testar` DSL oracle files in the selected workspace
- validate DSL files and show diagnostics in the editor
- generate Java oracle files into `settings/<workspace>/oracles/java`
- inspect and edit generated Java oracle files
- compile workspace Java oracles
- enable or disable generated Java oracles through `ExtendedOracles`

## Testing Priorities

- Add generator fixture tests for representative `.testar` files.
- Verify generated Java compiles against the current TESTAR oracle API.
- Verify generated oracles report application status correctly.
- Verify WebStudio DSL validation and Java generation write only inside the selected workspace.
