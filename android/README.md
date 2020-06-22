### Current Prerequisites

- See TESTAR_dev/README for compilation instructions
- Windows 10 host
- Android Studio Virtual Device Emulator (currently tested and used to connect through process name `qemu-system`)
- Appium application (run the server to work as middleware)
- Java JDK 1.8

### Current status of TESTAR with Android 

- TESTAR Android plugin is currently developed to test APK applications on a Virtual Device Emulator (tested with Android Studio AVD).
- We use Appium application as a middleware to launch the desired APK and obtain the current loaded DOM from Android.
- Appium needs to know the folder that contains the APK and the Emulator properties to find it and connect with. These settings are defined in the DesiredCapabilites.json file of each android protocol. Ex: `testar/resources/settings/android_generic`

**TESTAR (+ DesiredCapabilites.json) + AndroidDriver <---> Appium <---> Android Emulator Environment**

- To allow us to get the correct coordinates of the Android elements (to use SPY Mode) that exists inside the Emulator, we need to get the screen coordinates of the host OS. In this case, Windows 10. To do this, we use Windows Accessibility API to obtain the Emulator properties.

**TESTAR <--> Windows Accessibility API <--> Android Windows Emulator**

### Implementation 

Native Java Project - `es.upv.staq.testar.NativeLinker.java` : Is the TESTAR layer that manages the tool to use one or other plugin implementation.

Example:
- `getNativeStateBuilder` leads TESTAR to `org.testar.android.AndroidStateBuilder.java` : where we are defining how the State of the SUT will be created.
- `getNativeSUT` leads TESTAR to `org.testar.android.AppiumFramework.java` : where we are defining which object is going to be our SUT.

To activate one layer or another, by default we try to detect the Operative System name. But if we want to select a non-OS-specific implementation, we need to use the TESTAR Java protocols to select the plugin (Android - Webdriver).

Example:
- `testar/resources/settings/android_generic/Protocol_android_generic.java` -> `initialize(Settings settings)` -> `NativeLinker.addAndroidOS()`

#### (android - Java Project)

#### Gradle
`android/build.gradle` : Contains windows plugin dependency and Appium driver Java client dependency

#### Define TESTAR Android SUT

`org.testar.android.AppiumFramework.java` : Create the Object that contains (Appium-Android)Driver features and that will act as TESTAR SUT.

- Use the default Appium server URL address initialization `http://127.0.0.1:4723/wd/hub` with the desired Capabilities obtained from `testar/resources/settings/android_generic/DesiredCapabilites.json`

- Define SUT features over AndroidDriver features ( Ex : `clickElementById` , `driver.findElementById(id).click()` )

- Get SUT State information : Used for widget tree creation in `AndroidStateFetcher.java` class ( `getAndroidPageSource()` )

#### Build TESTAR Android State 

`org.testar.android.AndroidStateFetcher.java` : Create TESTAR State and Widget-Tree

1 - Windows 10 Emulator Device ( Windows Accessibility API ) + Android Driver ( `AndroidDriver.getAndroidPageSource()` )

2 - Define Elements and RootElement : `AndroidElement.java` contains the properties we want to obtain from Windows Accessibility API (only Emulator bounds really) and `AndroidDriver.getAndroidPageSource()`

3 - Descend trought DOM nodes to create as many AndroidElements as detected nodes, and associate the nodes properties. ( `XmlNodeDescend(AndroidElement parent, Node xmlNode)` )

4 - Tansform Elements to Widgets : `org.testar.android.AndroidState.java` contains the Getter Tags method to associate Widget Tags with Element properties.


#### TESTAR Tags definition

`org.testar.android.enums` : 

#### TESTAR Android Actions

`org.testar.android.actions` : contains the Actions objects definition. Associates the OrigintWidget and defines what should happen when the Actions are going to be executed `public void run(...)`. In this case send a driver event trought AppiumFramework SUT object.


### TODO List: New features and improvements

- Implement Tag Path for Android widget tree
- Robust sequences. Sometimes we cannot get current `AndroidPageSource` and we need to stop and start again.
- Implement Windows mouse events? to simulate touch actions ?
- Use `DesiredCapabilites.json` file to connect with an existing/running Android application/process.
- Prepare a better way to connect with Android Emulator (now we are connecting with the Windows process name `qemu-system`)
- Integrate `OverrideWebDriverDisplayScale` setting (for now we can use 100% display scale option on the Windows 10 host)
- Check resize issue: When using TESTAR Spy mode we can see that Android internal Layout does not resize (not ConstraintLayout application ? ). Then we need to manually prepare the correct Emulator size before testing process begins.
- Try to connect with a real Android Device. Generate mode it sends action instructions through Appium AndroidDriver, we are not using OS actions like mouse or keyboard events. Then should be possible to test real Android Mobile Devices with Android Debug Bridge and Appium Framework.
- If we test a real Android Device. Verify if we can obtain State and Actions screenshots ( `driver.getScreenshotAs()` ).