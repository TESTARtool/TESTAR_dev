#Github usage
The code of TESTAR is stored in [github](https://github.com). This document provides information about the way of working
within the TESTAR development project. For detailed information about Git and Github, please read the documentation at the
websites of the project.

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

1. Open a command prompt (cmd, powershell, bash, ksh, etc)
2. Change directory to the location where the repository needs be cloned.
3. Issue the following command; git clone https://github.com/florendg/TESTAR_dev
4. Checkout your development branch git checkout <username>_dev

The TESTAR development Environment is ready to use.

## Commit your work to your local repository

When you want to put your work under version control you have to commit it to te local repository. 
This section presents the process to save your work to the local repository.

### From Eclipse

1. Open the staging dialog CTRL+#.
2. In the window with unstaged files select the files you want to commit and press the + sign in the 
menubar of the "unstaged" window.
3. Type a commit message
4. Press commit

### Using command line tooling

First your need to stage your work Simply type

1. git add <filename> for each file you want to add. This command "stages" your work for commit
2. git commit -m "<message>"  to commit your work. 

## Push your changes to the archive maintained at Github.

The commit command only save your changes to the local instance of the repository. In order save your changes to 
your branch which is maintained in Github, you need to push the changes. The chapter explains how it is done.

### From Eclipse

1. Right-click on your project
2. Select Team -> Push upstream

A dialog shows the changes that are pushed to the remote repository.

### Using command line tooling.

In the root folder of your project type: git push

## Creating a pull request

TODO

## Update your project with the latest master.

The master branch should contain all accepted work. Before your pull request is integrated, it needs to be up to date
with the master branch. In order to achieve this state, the changes from the master branch need to be merged to your
development branch. The section describes the process using the command line

### Using command line tooling.

1. checkout the master branch (git checkout master) 
2. pull the latest changes for the master branch (git pull)
3. checkout your development branch (git checkout <user>_dev)
4. merge the changes from master (git merge --no-ff master)
5. Resolve conflicts if there are any.
6. commit and push the changes in the development branch.
