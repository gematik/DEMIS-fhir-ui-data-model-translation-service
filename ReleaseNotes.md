<div style="text-align:right"><img src="https://raw.githubusercontent.com/gematik/gematik.github.io/master/Gematik_Logo_Flag_With_Background.png" width="250" height="47" alt="gematik GmbH Logo"/> <br/> </div> <br/>
 
# Release notes

## 2.4.0
- added designation uses to codes
- added FEATURE_FLAG_ADD_DESIGNATION_USE
- updated spring-parent to 2.14.19
- added version parameter to CodeDisplay object
- added version for snomed and loinc to prepared pathogen data for front end
- added FEATURE_FLAG_SNOMED_VERSION_FROM_FUTS
- removed feature flag FEATURE_FLAG_SNAPSHOT_6_ACTIVE

## 2.3.4
- upgraded fhir-package-initializer to 1.0.6 to reduce wait time for istio-proxy
- removed FEATURE_FLAG_DISEASE_DATEPICKER
- upgraded dependencies
- add new endpoints for follow up disease and pathogen code displays

## 2.3.3
- upgraded fhir-package-initializer to 1.0.5 for faster package loading

## 2.3.2
- added feature flag feature.flag.snapshot.6.active to additional service

## 2.3.1
- added feature flag feature.flag.snapshot.6.active

## 2.3.0
- updaded version of Notification Builder to 8.0.0
- derived datepicker precision from regex in strict disease profile (only for the dynamically built part of the form)
- added support of FHIR packages through new Docker base image (FHIR package initializer)
- added tooltip integration from FHIR structure definitions to disease formly forms
- integrated FHIR profile files of R4 terminology and DEMIS test notification category GAPP
- added new endpoint to retrieve all notification categories for §7.1 notifications
- added code to quantity formly representation
- replaced FEATURE_FLAG_FUTS_VALUESETS_SNOMED with FEATURE_FLAG_DISEASE_STRICT
- replaced FEATURE_FLAG_HOSP_REASON_MOVE with FEATURE_FLAG_DISEASE_STRICT

## 2.2.3
- Upgraded dependencies
- parsed questionnaire items referencing Quantity types

## 2.2.2
### changed
- disease frontend date inputs changed to date picker component
- set test data creation for disease notification category to false
- removed feature flag FEATURE_FLAG_HOSP_COPY_CHECKBOXES

## 2.2.1
### changed
- added default feature flag FEATURE_FLAG_FUTS_VALUESETS_SNOMED to values.yaml
- upgraded spring parent to 2.12.3

## 2.2.0
### changed
- fix: set required attribute for text-based FieldGroups

## Release 2.2.0
### changed
- Updated ospo-resources for adding additional notes and disclaimer
- setting new ressources in helm chart
- setting new timeouts and retries in helm chart
- updating dependencies
- new endpoints and logic to process nonnominal notifications for IfsG §7.3
- added version to system for strict laboratory notification

## 2.1.0
### changed
- First official GitHub-Release
- Sorting of ValueSet and CodeSytem
- Added notification status and initial message
- Added diagnosis note
- Resistances added for §7.1
- Switched to the delegation pattern from istio
- Displayed of state-specific pathogen detections
- Changed country codes in the bundles to FHIR standard
- Transferred the reason for hospitalization to the general questionnaire
- Simplification of entering the hospital address for hospitalization
- Dependency-Updates (CVEs et al.)
- Updated Base-Image to OSADL

## 1.2.3 (2024-04-15)
### changed
- Using Spring-Parent
- Fix CVEs
- Extended Helm Chart for Kubernetes

## 1.2.1 (2023-12-20)
### changed
- Using Spring-Parent
- Fixed CVEs 

## 1.2.0 (2023-12-01)
### changed
- SpringBoot 3.2.0

## 1.1.5 (2023-11-01)
### added
- Observability features

### changed
- SpringBoot 3.1.4

## 1.0.0 (2023-01-04)
### added
- SpringBoot 3.0.1

### changed
- License updated to 2023

### fixed
- SonarQube Code Smells

### security
- Removed SnakeYAML (OWASP Scan)
