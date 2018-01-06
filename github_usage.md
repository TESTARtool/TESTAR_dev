#Github usage
The code of TESTAR is stored in [github](https://github.com). This document provides information about the way of working
within the TESTAR development project.

##TESTAR

TESTAR is a tool for automated testing of the desktop. More
information can be found at [testar.org](https://testar.org/about/).

##Git

The source code is maintained in a repository at Github. In order to work with this repository, GIT needs to 
be installed on the development machine. Git can be installed from [https://git-scm.com/downloads](https://git-scm.com/downloads).

##Cloning the repository

The software of TESTAR is maintained in a source code repository at Github. The repository
can be found at [https://github.com/florendg/TESTAR_dev](https://github.com/florendg/TESTAR_dev).
To start working on the code, you need to clone the repository. In this chapter we present two procedures;

1. From Eclipse
2. From the command line.

### Using Eclipse

To clone the repository from Eclipse, Eclipse needs to be installed. The Java development Environment comes 
with Git installed.

1. Start Eclipse
2. Select an empty workspace
3. Select File -> Import -> Git -> Projects from Git
4. Select Clone URI -> Next
5. Fill in the URI: https://github.com/florendg/TESTAR_dev 
6. Provide your username and password in the Authentication section -> Next
7. Select your development branch <name>_dev and master -> Next
8. Provide the location where the repository will be stored.
9. Select your development branch as initial branch. -> Next
10. Select "Import as general Project" -> Next
11. Provide the project name -> Finish
12. Right mouse on the project -> Configure -> Add Gradle Nature

The TESTAR developement Environment is now ready to use.

### Using command line tooling

To clone the repository using the git command line tooling make sure the git command is on the path.
Next, issue the following commands;

1. Open a command prompt (cmd, powershell, bash)
2. Change directory to the location where the repository needs be cloned.
3. Issue the following command; git clone https://github.com/florendg/TESTAR_dev
4. Checkout your development branchl git checkout <username>_dev
TODO complete

##Checkout a branch

### From Eclipse

### Using command line tooling

##Working @ your local branch

### From Eclipse

### Using command line tooling


##Delivery of your work.