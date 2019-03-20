## README

This is the github development folder for subroutines. 
The software can be build as described in the README of the master folder.

### The subroutine specific software

The files for this framework are located in
- framework
  * /testar/src/nl.ou.testar.subroutine
  
- examples
  * /testar/resources/settings
  
- tgherkin
  * /testar/src/nl.ou.testar.tgherkin
  
– /tgherkin
  * /native/src/.ou.testar.tgherkin.grammar
  
### Options

There are currently two options within the framework to implement a subroutine
for a specific site:

- TG Tgherkin option
  The subroutine is using a Tgherkin file to run the subprocess.
  
- CA Compound Action option
  The subroutine is using compound actions to run the subprocess.

### The TG option

The TG option runs a subroutine with a specific Tgherkin file. If the URL of a web
page matches one of the URLs defined in the input data file (tgherkinFileData.csv) the
related Tgherkin file is executed. Before and after running the subroutine, TESTAR
is in control of the test procedure.
If a Tgherkin subroutine is running, it checks whether all action steps of the Tgherkin
Document are completed. If all actions steps of the Document are completed, it checks
if a predefined exit URL exists. If so, the predefined exit URL is activated. If no
predefined exit URL exists TESTAR continues with random actions.
The success of this option merely depends on the contents of the Tgherkin file and
the way it is handled within the Tgherkin environment. In practice, the Tgherkin
environment is at this moment, not a stable factor.

Building steps are:
  - Implement a subclass of TgherkinFileProtocol
  - Create an input data file tgherkinFileData.csv. 
    Define URL, Tgherkin file and exit action for each subroutine
  - Change the settings of tag TgherkinFileData and the usual tags 
    ProtocolClassand SUTConnectorValue.
  - Create a Tgherkin file to run the subprocess
  - Optional: adapt methods startState, startSubroutine, finishState and finishSubroutine

With this option, multiple URLs can be triggered each with its Tgherkin file.

### The CA option

If using the CA option, selecting a subroutine for a form depends on:
  - the actual number of editable widgets on the first screen being equal or larger
than an arbitrary number of editable widgets (see method setMinNoOfEditWidgets(
int)).
The CA option counts the number of editable widgets on the first web page. If this
number is larger than an arbitrarily minimum number, it presumes that the targeted
form is available.
The subroutine starts creating compound actions combining the widgets on the actual
screen with the information from the input data file (compoundActionData.csv).
Compound actions are stored in a set with unique compound actions. The procedure
is repeated until the size of the compound action set equals the expected number
of screens. Default the CA option scrolls to the next screen. The remainder of the
sequence is random TESTAR actions.

Building steps are:
  - Implement a subclass of CompoundActionProtocol
  - Create an input data file compoundActionData.csv. 
    Define role, title and optional input text for individual widgets
  - Change the settings of tag CompoundActionData and the usual tags 
    ProtocolClass and SUTConnectorValue.
  - Select the compound actions using method startSubroutine(State state).
    Default startSubroutine selects all widgets with role UIAEdit, UIARadioButton,
    UIACheckBox, UIASpinner, UIACustomControl and UIAButton.
  - Optional: adapt methods startState and startSubroutine

The input data file contains data for only one form. In contrast with the TG option,
the CA option can trigger only one subroutine for one specific form.
