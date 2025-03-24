<img align="right" width="250" height="47" src="media/Gematik_Logo_Flag.png" alt="gematik logo"/> <br/>  

# Fhir-Ui-Data-Model-Translation-Service (FUTS)

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#release-notes">Release Notes</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>


[![Quality Gate Status](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Afhir-ui-data-model-translation-service&metric=alert_status&token=56c451846feaca7a027fb63687da583540b88235)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Afhir-ui-data-model-translation-service)
[![Vulnerabilities](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Afhir-ui-data-model-translation-service&metric=vulnerabilities&token=56c451846feaca7a027fb63687da583540b88235)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Afhir-ui-data-model-translation-service)
[![Bugs](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Afhir-ui-data-model-translation-service&metric=bugs&token=56c451846feaca7a027fb63687da583540b88235)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Afhir-ui-data-model-translation-service)
[![Code Smells](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Afhir-ui-data-model-translation-service&metric=code_smells&token=56c451846feaca7a027fb63687da583540b88235)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Afhir-ui-data-model-translation-service)
[![Lines of Code](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Afhir-ui-data-model-translation-service&metric=ncloc&token=56c451846feaca7a027fb63687da583540b88235)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Afhir-ui-data-model-translation-service)
[![Coverage](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Afhir-ui-data-model-translation-service&metric=coverage&token=56c451846feaca7a027fb63687da583540b88235)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Afhir-ui-data-model-translation-service)

# About The Project

This project provides a service that enables the translation of any code that is known for DEMIS notifications. It is
possible to convert codes to a display value via their resource type. It is also possible to query the available
resource lists and the available codes per resource type.

## Release Notes

See [ReleaseNotes](./ReleaseNotes.md) for all information regarding the (newest) releases.
___

# Getting Started

## Prerequisites

The Project requires Java 21 and Maven 3.8+.

## Installation

The Project can be built with the following command:

```sh
mvn clean install
```

The Docker Image associated to the service can be built with the extra profile `docker`:

```sh
mvn clean install -Pdocker
```

___

# Usage

The application can be executed from a JAR file or a Docker Image:

```sh
# As JAR Application
java -jar target/fhir-ui-data-model-translation-service.jar
# As Docker Image
docker run --rm -it -p 8080:8080 fhir-ui-data-model-translation-service:latest
```

It can also be deployed on Kubernetes by using the Helm Chart defined in the
folder `deployment/helm/fhir-ui-data-model-translation-service`:

```ssh
helm install fhir-ui-data-model-translation-service ./deployment/helm/fhir-ui-data-model-translation-service
```

## Endpoints

Define here the available endpoints exposed by the service

| Endpoint                                                                                                 | Input                                                                                                                                                                                              | Output                                                                                           |
|----------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------|
| old: /laboratory/federalStates <br/> new: /laboratory/7.1/federalStates                                  | none                                                                                                                                                                                               | list of all available federal states                                                             
| old: /laboratory/federalState/{code} <br/> new /laboratory/7.1/federalStates/{code}                      | federal state code from CodeSystemISO31662DE                                                                                                                                                       | list of all notificatoin categories for federal state                                            
| old: /laboratory/federalState/pathogenData/{code} <br/> /laboratory/7.1/federalState/pathogenData/{code} | specific pathogenCode from notificationCategories                                                                                                                                                  | all data to fill selection lists in the notification-portal                                      
| /laboratory/7.3                                                                                          | none                                                                                                                                                                                               | list of all available notification caterogries for §7.3 notifications                            
| /laboratory/7.3/pathogenData/{code}                                                                      | specific pathogenCode from notificationCategories                                                                                                                                                  | all data to fill selection lists in the notification-portal                                      
| /CodeSystem                                                                                              | none                                                                                                                                                                                               | returns all available code systems                                                               
| /CodeSystem?system={system}&version={version}                                                            | system is the url of a CodeSystem. version is optional. you can either use the version parameter or enter the version in fhir style with system\|version                                           | returns all data for a given code system name                                                    
| /CodeSystem?system={system}&code={code}&version={version}                                                | system is the url of a CodeSystem and code, e.g. "addressUse" and "current". version is optional. you can either use the version parameter or enter the version in fhir style with system\|version | returns the code with its display value                                                          
| /ValueSet                                                                                                | none                                                                                                                                                                                               | returns all available value sets                                                                 
| /ValueSet?system={system}&version={version}                                                              | system is the url of a ValueSet. version is optional. you can either use the version parameter or enter the version in fhir style with system\|version                                             | returns all data for a given value set name                                                      
| /ValueSet?system={system}&code={code}&version={version}                                                  | system is the url of a ValueSet and code, e.g. "addressUse" and "current".  version is optional. you can either use the version parameter or enter the version in fhir style with system\|version  | returns the code with its display value                                                          
| /disease/questionnaire/{code}/items                                                                      | disease code, e.g. "CVDD"                                                                                                                                                                          | questionnaire title and text for all questions                                                   
| /disease                                                                                                 | none                                                                                                                                                                                               | returns list of all available diseases                                                           
| /disease/questionnaire/{code}/formly                                                                     | disease code, e.g. "CVDD"                                                                                                                                                                          | return formly json configuration for specific questionnaire, common questionnaire and condition  
| /conceptmap/                                                                                             | none                                                                                                                                                                                               | returns all available concept map keys (url and name) and condition                              
| /conceptmap/{name}                                                                                       | concept map name or url                                                                                                                                                                            | returns content of a concept map                                                                 
| /conceptmap/{name}/{code}                                                                                | concept map name or url and code                                                                                                                                                                   | returns mapping for one code in a concept map                                                    
| /utils/countryCodes                                                                                      | none                                                                                                                                                                                               | returns all two digit country codes from iso 3166 code system sorted by their german designation 

## DEMIS-Profiles

This service provides data for front-end applications, e.g. for the Microfrontends or PDF-Generator. Since the
data is created from various sources, a profile snapshot of the DEMIS profiles must be available; further on, this data
is referred to as base data. 
To use it locally in development the base data must be located in the root directory of the project in the folder 
demis-profile-snapshots. The image exists for the Docker environments, which provides all basic data when used as a 
volume. This image is maintained in DockerHub: [demis-profile-snapshots](https://hub.docker.com/repository/docker/gematik1/demis-fhir-profile-snapshots/general).

## Feature Flags

| Property                                 | Description                                                         |
|------------------------------------------|---------------------------------------------------------------------|
| `feature.flag.notification7_3`           | activates §7.3 notification categories                              |
| `feature.flag.moveHospitalizationReason` | moves question for hospitalization to the common questionnaire part | 

## Ops Flags

| Property                           | Description                                                                                                                                                                                                                                                                                                                                                                                  |
|------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `add.test.data.error.case.for.lab` | activates a testcase for the laboratory notification portal. adds a notification category that will lead to an error when chosen by the user                                                                                                                                                                                                                                                 |
| `add.test.data.laboratory.sorting` | activates a testcase for the laboratory notification portal. adds a notification category which has special test value sets, that are not processable by core. the value sets can be used to check if the value set sorting works. if data.path.gapp.data does not lead to a existing path, the GAPP notification category will result in the same error as add.test.data.error.case.for.lab |
| `data.path.gapp.data`              | if path does not exist,  add.test.data.laboratory.sorting will not add any sorting testdata.                                                                                                                                                                                                                                                                                                 

## Security Policy

If you want to see the security policy, please check our [SECURITY.md](.github/SECURITY.md).

## Contributing

If you want to contribute, please check our [CONTRIBUTING.md](.github/CONTRIBUTING.md).

## License

EUROPEAN UNION PUBLIC LICENCE v. 1.2

EUPL © the European Union 2007, 2016

Copyright (c) 2023 gematik GmbH

See [LICENSE](LICENSE.md).

## Contact

E-Mail to [DEMIS Entwicklung](mailto:demis-entwicklung@gematik.de?subject=[GitHub]%20VFhir-Ui-Data-Model-Translation-Service)