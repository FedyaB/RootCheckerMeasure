# Root Checker (Measurer Module)
A project to gather information about Android device configurations based on some features (a.e. execution time of a command in a shell).

This module is used as a part of my bachelor thesis about detection an access to the superuser account on an Android device, which is hidden by Magisk.  
The target is to find Magisk on a device, assuming that the module of detection can be placed into Magisk Hide List.  
The approach uses side-channel attacks. This project is used to gather data about a device to build a statistical/ML models then (not in the project). 

## Project structure
The project consists of 3 Android apps.
* `app` is an app to gather information about device (main module)
* `roothidelist` is an app that needs to be placed into Magisk Hide List
  * It is used by main module to determine the launch time of an app in Magisk Hide List
* `rootnormalapp` is an app that is not placed into Magisk Hide List
  * It is used by main module to determine the launch time of an app not in Magisk Hide List

## Gathered features
* Time needed to execute a shell command that needs superuser account to run (a.e. `su`)
* Time needed for `PackageManager` to return the list of installed apps
* Time needed for an app from Magisk Hide List to launch
* Time needed for an app not from Magisk Hide List to launch

Barinov Fedor, 43601/2, St.Petersburg Polytechnic University, 2019.
