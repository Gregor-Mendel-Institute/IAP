## IAP - The Integrated Analysis Platform for high-throughput plant image analysis

This fork contains all necessary changes to integrate IAP into the [PhenoBox-System](https://github.com/Gregor-Mendel-Institute/PhenoBox-System)

### Documentations and further Information

Documentations and releases (up to version 2.0.2) are available here: [http://iapg2p.sourceforge.net/](http://iapg2p.sourceforge.net/)

### Setup of Development Environment

The following procedure is suggested for setting up the development environment. First make sure that you have already installed the latest stable versions of Java and Eclipse. Then you can load the IAP source code from GitHub. 

#### Download/Info-Links

**Java Software Development Kit (SDK)**

[Install newest version of JDK.](http://www.oracle.com/technetwork/java/javase/overview/index.html)

**Eclipse Development Environment**

[Install newest version of Eclipse.](https://eclipse.org/)


#### Project import into Eclipse

Its advised to create a separate and clean Eclipse workspace. Then follow the next steps to import the project and configure the workspace:
First change the Text file encoding to UTF-8 (select Window > Preferences > General > Workspace, choose other and select UTF-8, then click OK)
Next, switch to the GIT Repository Exploring view and add the Git repository URL.
After connecting to the Git repository, all projects should be pulled and initialized correctly within Eclipse.
Now switch to the Preferences menu again and go to Java > Code Style > Formatter and import the VANTED_SAVE_ACTION_FORMAT, you can find it under your workspace > make > save_action_format.xml.
Then (also in the Preferences menu) go to Java > Editor > Save Actions and enable all options.
After that, switch to the Java Perspective and navigate into the make folder.
Depending on your operating system execute the createfilelist, if you use windows just double click on the .bat file, for Unix or mac execute the .cmd file in the shell.
Now, you have to configure the Run configuration (click on the arrow beside the run icon), choose a name for the configuration, choose the IAP folder by clicking on the Browse button and then search for the main class.
Finally, switch to the Arguments tab and free up some additional main memory by adding -Xmx20g (in this example 20 GB, size of the allocated memory depends on your system, you should left some reserves for your operating system) to the VM arguments, then click Run. Congrats, the IAP should start!
Normally, there should be no errors in the Problems view, warnings can be ignored. If you have further questions, please write a mail.
