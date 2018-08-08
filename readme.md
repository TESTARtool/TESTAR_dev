### Usage of TESTAR-webdriver

The webdriver version of TESTAR is developed to better handle web applications.  
For various reasons, the desktop version doesn't recognize the non-native widgets available in websites.  
TESTAR webdriver tries to remedy this by looking at the DOM and adding a (JS) web extension to the browser.  
An additional advantage is the fact that it's safe to run the webdriver version 'natively',  
i.e. it's not necessary to run TESTAR webdriver in a VM.

The webdriver version of TESTAR is tested on 3 mayor platforms : Windows 10, OS X and Linux.  
It has been tested with Chrome/Chromium, Firefox and Edge (Windows).  
That said, using Chrome seems to be the most performant option.  

#### Prerequisites

Make sure the conditions to run the desktop version of TESTAR are met, i.e. Java and Gradle are installed.  
Next, download a webdriver for the browser to use, the browser itself also needs to be installed.  
- Chrome/Chromium : http://chromedriver.chromium.org/downloads
- Firefox : https://github.com/mozilla/geckodriver/releases
- Edge : https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/   
  Make sure to downloaded version matches the installed Edge version.

Place the driver in an accessible location.  
This location needs to be first argument in the SUTConnectorValue, either via the test.settings file,  
or edited via the TESTAR GUI. Adjust the other settings if needed, windows size comes to mind.

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
Under 'System Preferences', 'Security & Privacy', 'Accessibility' allow the terminal.  

