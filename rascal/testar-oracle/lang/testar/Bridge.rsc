module lang::testar::Bridge

import lang::testar::Oracle;    // grammar: start[Oracle]
import lang::testar::Compiler;  // compileToJava(start[Oracle])
import lang::testar::Check;     // check(start[Oracle], start[Model])
import lang::testar::Model;     // start[Model]
import ParseTree;

// ---- compiler entry point ----
public str compileAt(loc x) {
  start[Oracle] ast = parse(#start[Oracle], x);
  return compileToJava(ast);
}

// ---- diagnostics API ----
data Severity   = SevError() | SevWarning() | SevInfo();
data Diagnostic = diag(Severity sev, loc where, str message);

// optional: parse-only
public start[Oracle] parseAt(loc x) = parse(#start[Oracle], x);

/**
 * Validate oracle against a given model.
 * NOTE: let parse/static exceptions bubble to Java; we only convert Message -> Diagnostic here.
 */
public list[Diagnostic] validateAtWithModel(loc oracleFile, loc modelFile) {
  start[Oracle] ast = parse(#start[Oracle], oracleFile);
  start[Model]  mdl = parse(#start[Model],  modelFile);

  set[Message] msgs = check(ast, mdl);
  return [ msg2diag(m) | m <- msgs ];
}

// --------- Message -> Diagnostic (tolerant) ----------
//
// Supports all of these shapes emitted by your checker:
//   message(level, text, loc)
//   message(level, text)
//   error(text, loc) | warning(text, loc) | info(text, loc)
//   error(text, list[loc]) | warning(text, list[loc]) | info(text, list[loc])
//   error(text) | warning(text) | info(text)
// Anything else -> generic diagnostic with unknown location.
//
public Diagnostic msg2diag(Message m) {
  value lvl; str text; loc at; list[loc] ats;

  // Generic 'message' variants
  if (message(lvl, text, at) := m)       return diag(level2sev(lvl), at, text);
  if (message(lvl, text) := m)           return diag(level2sev(lvl), |unknown:///|, text);

  // Direct level + single location
  if (error(text, at)   := m)            return diag(SevError(),   at, text);
  if (warning(text, at) := m)            return diag(SevWarning(), at, text);
  if (info(text, at)    := m)            return diag(SevInfo(),    at, text);

  // Direct level + list of locations (pick first)
  if (error(text, ats)   := m && size(ats) > 0) return diag(SevError(),   ats[0], text);
  if (warning(text, ats) := m && size(ats) > 0) return diag(SevWarning(), ats[0], text);
  if (info(text, ats)    := m && size(ats) > 0) return diag(SevInfo(),    ats[0], text);

  // Direct level + no location
  if (error(text)   := m)                 return diag(SevError(),   |unknown:///|, text);
  if (warning(text) := m)                 return diag(SevWarning(), |unknown:///|, text);
  if (info(text)    := m)                 return diag(SevInfo(),    |unknown:///|, text);

  // Fallback
  return diag(SevError(), |unknown:///|, "Unhandled Message: <m>");
}

// Map Message's level constructors to our Severity (avoid name clashes)
public Severity level2sev(value lvl) {
  if (error()   := lvl) return SevError();
  if (warning() := lvl) return SevWarning();
  if (info()    := lvl) return SevInfo();
  return SevError();
}
