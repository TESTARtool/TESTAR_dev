## Usage of TESTAR Ubuntu 18.04

### Prerequisites

gradle: `sudo apt-get install gradle`

To allow testar compilation

xdotool: `sudo apt-get install xdotool`

To allow SUT interactions

geany: `sudo apt-get install geany`

As initial SUT

### Gradle build

To build the Gradle project and run it execute the following procedure;

1. In ./TESTAR_dev folder Run `gradle installDist`
2. Change directory to testar/target/install/bin `cd testar/target/install/bin`
3. Run `./testar`
