## README

This folder contains the data used and collected during my master thesis. 

This folder contains the following folder
1. code 
2. configuration
3. data

### Code folder

This folder contains the code generated for this thesis. It consist of the following folders

1. ActionSelects
2. CoverageCollector
3. CoverageProtocol
4. web_generic_protocol_setup_0
5. webgwt_protocol_setup_1
5. webdrivergwt_protocol_setup_2

These directories contain the source code used during the thesis. From eacht ActionSelects, CoverageCollector, and CoverageProtocol folder a .jar file has to be generated, for example, by using Eclipse. These jar-files must be added to the TESTAR lib folder.

The dependencies for these 3 projects are the standard library dependencies of TESTAR, except CoverageCollector which needed the following additional dependencies:
- antlr-runtime-3.5.3.jar (downloadable via https://mvnrepository.com/artifact/org.antlr/antlr/3.5.2)
- ST-4.1.jar (downloadable via https://mvnrepository.com/artifact/org.antlr/ST4/4.1 or http://www.stringtemplate.org/download/ST-4.1.jar)

The other folders contain the specific protocols used. Note: the web_generic_protocol_setup_0 is tested with TESTAR v1.3, the others with the latest version of TESTAR (v2.0.3).

### Configuration folder

This folder contains the action filters (filters), action select sequences (selects), and action checks (checks) used during testing of setup-1 and setup-2.

### Data folder

This folder contains all the data collected during the tests split-up into different setups and collecting steps.
 
