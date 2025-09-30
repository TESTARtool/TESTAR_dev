module lang::testar::FixPackage

import lang::testar::Oracle;
import ParseTree;
import String;
import IO;
import util::FileSystem;

str pathDiff(str parent, str x) {
    if (/^<parent>\/<rest:.*>\/.*$/ := x) {
        return rest;
    }
    return "";
}

start[Oracle] fixPackage(start[Oracle] t, loc root) {
    str pkg = replaceAll(pathDiff(root.path, t.src.path), "/", ".");
    Package decl = [Package]"package <pkg>;";

    return top-down visit(t) {
        case Package _ => decl
        case (Oracle)`<Assert* as>` => (Oracle)`<Package decl>
                                               '
                                               '<Assert* as>`
    }
}

// WARNING: this destructively overwrites files!
void fixAllPackageDecls(loc root) {
    for (loc x <- files(root), x.extension == "testar") {
        start[Oracle] pt = parseOracle(x);
        pt = fixPackage(pt, root);
        writeFile(x, "<pt>");
    }
}