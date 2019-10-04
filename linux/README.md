## Usage of TESTAR Ubuntu 18.04

### Prerequisites

To allow testar compilation
gradle: `sudo apt-get install gradle`

To allow SUT interactions
xdotool: `sudo apt-get install xdotool`


SUT as example
geany: `sudo apt-get install geany`
mypaint: `sudo apt-get install mypaint`

### Gradle build

To build the Gradle project and run it execute the following procedure;

1. In ./TESTAR_dev folder Run `gradle installDist`
2. Change directory to testar/target/install/bin `cd testar/target/install/bin`
3. Run `./testar`
