### Usage of TESTAR-webdriver

The webdriver version of TESTAR is developed to better handle web applications.
For various reasons, the desktop version doesn't recognize the non-native widgets available in websites.
TESTAR webdriver tries to remedy this by looking at the DOM and adding a (JS) web extension to the browser.
An additional advantage is the fact that it is safe to run the webdriver version 'natively',
i.e. it is not necessary to run TESTAR webdriver in a VM.

The webdriver version of TESTAR is tested on 3 mayor platforms : Windows (10 and 11), OS X and Linux.
It has been tested with Chrome for Testing, Firefox and Edge (Windows).
That said, using Chrome for Testing seems to be the most performant option.

#### Browser Prerequisites

Make sure the conditions to run the desktop version of TESTAR are met, i.e. Java and Gradle are installed.
Next, download the browser to use:
- Chrome for Testing : https://googlechromelabs.github.io/chrome-for-testing/
- Firefox Browser: https://github.com/mozilla-firefox/firefox
- Edge Browser: https://github.com/MicrosoftEdge/MSEdge

#### WebDriverManager

TESTAR makes use of the WebDriverManager open-source Java library: https://github.com/bonigarcia/webdrivermanager  
to automatically download and make use of the driver dependencies.  

#### Select browser in the SUTConnectorValue

TESTAR users have multiple options to select the browser to test:  
1. `"https://para.testar.org/"` : Indicating only the web URL to test will automatically download the lastest Chrome for Testing stable release
2. `"C:\Program Files\chrome-win64\chrome.exe" "https://para.testar.org/"` : Users can indicate the specific Chrome for Testing browser
3. `"C:\Program Files\Mozilla Firefox\firefox.exe" "https://para.testar.org/"` : Users can indicate the specific Mozilla Firefox browser
4. `"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe" "https://para.testar.org/"` : Users can indicate the specific Microsoft Edge browser

#### Running

On Windows, open a command windows and go to the root TESTAR directory.
In this directory, run TESTAR with :  
$ gradle runTestar

On OS X and Linux, open a terminal and build TESTAR with the following command :  
$ gradle installDist  
Next cd to the installation directory :  
$ cd testar/target/install/testar/bin  
And start TESTAR  
$ ./testar


On OS X, the terminal needs to be allowed to control the computer.  
Under _System Preferences_, _Security & Privacy_, _Accessibility_ allow the terminal.

#### Extra options in Protocol_webdriver_generic

While the webdriver tries to find all clickable elements on the page via the _onclick_ attribute or eventlistener, this is not sufficient for certain web frameworks that use global eventlisteners. Global eventListeners use class attributes to determine the required actions when the element is clicked. The protocol allows the tester to add these attributes to _clickableClasses_.

Because the webdriver is dependent on the use of the (Javascript) web extension, it fails when the browser encounters PDFs or images. With the _deniedExtensions_ option the testar can deny these URLs.

While testing web applications, it is desirable to keep the testing on the same domain. Many applications have links pointing to domains outside the scope. When the browser reaches a domain not defined in _webDomainsAllowed_ it will try to go back to the last allowed page. Related to this is the _followLinks_ option to follow links opened in new tabs or stay with the original tab. 

The _login_, _username_ and _password_ pairs allow TESTAR to automatically login on websites that need authentication. The first pair consists of the URL with the login and the id of the FORM. The next 2 items should contain the ids of the input fields with their credential values.

The last configurable item, _policyAttributes_, is a list of attribute names and values. When the deriveAction method finds all these name-value pairs on an element, a click action is forced. This is used to automate the removal of annoying policy popups.
