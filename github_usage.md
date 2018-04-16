# Github usage
The code of TESTAR is stored in [github](https://github.com). This document provides information about the way of working
within the TESTAR development project. If your new to Github and Git, please read the documentation at
 [Github Guide](https://guides.github.com).

## TESTAR

TESTAR is a tool for automated testing of the desktop. More
information can be found at [testar.org](https://testar.org/about/).

## Git

The source code is maintained in a repository at Github. In order to work with this repository, GIT needs to 
be installed on the development machine. Git can be installed from [https://git-scm.com/downloads](https://git-scm.com/downloads).

## Cloning the repository

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

1. Open a command prompt (cmd, powershell, bash, ksh, etc)
2. Change directory to the location where the repository needs be cloned.
3. Issue the following command; git clone https://github.com/florendg/TESTAR_dev
4. Checkout your development branch git checkout <username>_dev

The TESTAR development Environment is ready to use.

## Commit your work.

Within the TESTAR project we follow the workflow presented at [Github Workflow](https://guides.github.com/introduction/flow/).
The branch mentioned in this text is <user>_dev. You develop on your developer branch and deliver to 
the master branch with a pull request when your work is done.

## Final note.
Please read the available information on the internet if you are not familiar with the way things work with Git and Github
