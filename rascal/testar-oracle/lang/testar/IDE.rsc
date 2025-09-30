module lang::testar::IDE

import lang::testar::Oracle;
import lang::testar::Check;
import lang::testar::Model;

import util::LanguageServer;
import util::Reflective;
import ParseTree;
import IO;


import lang::testar::Compiler;

set[LanguageService] oracleContributor() 
    = {parsing(Tree(str input, loc l) { return parse(#start[Oracle], input, l); }),
    build(oracleSummarizer, providesDocumentation = false, providesDefinitions = false
        , providesReferences = false, providesImplementations = false)
    };

set[LanguageService] modelContributor() 
    = {parsing(Tree(str input, loc l) { return parse(#start[Model], input, l); })};

//start[Model] TESTAR_MODEL;

Summary oracleSummarizer(loc origin, start[Oracle] input) {
    // TODO: somehow get this out of the function.
    start[Model] model = parse(#start[Model], |project://testar-oracle/src/main/rascal/lang/testar/testar.model|);
    rel[loc, Message] msgs = {<m.at, m> | Message m <- check(input, model) };
    if ({ m | Message m <- msgs<1>, m is error } == {}) {
        compileToFile(input);
    }
    return summary(origin, messages = msgs);
}

void main() {
    registerLanguage(language(pathConfig(srcs = [|std:///|, |project://testar-oracle/src/main/rascal|]),
            "Testar", {"testar"}, "lang::testar::IDE", "oracleContributor"));
    registerLanguage(language(pathConfig(srcs = [|std:///|, |project://testar-oracle/src/main/rascal|]),
            "UIModel", {"model"}, "lang::testar::IDE", "modelContributor"));
}
