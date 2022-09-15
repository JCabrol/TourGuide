# TourGuideProject

A project done as part of my Java application developper training (project n°8).

The main skills acquired with this project were:

* Configure a deployment environment to manage the lifecycle of an application
* Correct malfunctions reported by the customer on the application
* Produce technical and functional documentation of the application
* Complete a suite of unit and integration tests to take into account the changes made
* Provide customer-requested feature enhancements

* This project is shared on gitLab with a continuous integration pipeline. ([Here is my GitLab repository](https://gitlab.com/JCabrol/tourguideproject))
* Here is the [technical documentation]() I made for this project.
* Here are the [presentation slideshows]() I made for this project.


## Description

TourGuide is SpringBoot application which permit user to find attractions close from its location, to obtains rewards and trip propositions.

## Technical specifications

TourGuide uses
* Java 1.8
* Gradle 4.8.1
* SpringBoot 2.1.6

TourGuide also uses RecursiveTasks with ForkJoinPools and LinkekBlockingQueues to permit application running concurrently with several threads to increase its performance.
