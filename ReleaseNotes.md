<div style="text-align:right"><img src="https://raw.githubusercontent.com/gematik/gematik.github.io/master/Gematik_Logo_Flag_With_Background.png" width="250" height="47" alt="gematik GmbH Logo"/> <br/> </div> <br/>
 
# FHIR-UI-Data-Model-Translation-Service Release Notes

## Release 2.6.2
- added 7.3 follow-up notification endpoints

## Release 2.6.1
- updated datepicker to use default placeholder
- fixed caching issue where disease questionnaire date limits were computed once at startup instead of per request
- bumped spring parent to 2.15.7

## Release 2.6.0 
- updated base-image and updated from java 21 to java 25
- Removed istio helm chart
- added disease questionnaire organization fields input validation
- added FEATURE_FLAG_DISEASE_QUESTIONNAIRE_INPUT_VALIDATION
- removed FEATURE_FLAG_SNOMED_VERSION_FROM_FUTS
- increased RAM-Limit
- decreased MaxRAMPercentage from 80 to 65 %
- removed FEATURE_FLAG_ADD_DESIGNATION_USE
- removed FEATURE_FLAG_NOTIFICATIONS_7_3
- removed fall back solutions and processing for pre snapshot 6 data
- bumped spring parent to 2.15.6
- removed FEATURE_FLAG_SNAPSHOT_6

## Release 2.5.0
- added processing of FHIR profiles LaboratoryFacility and InfectProtectFacility
- skipping CodeSystem files with no content
- added FEATURE_FLAG_SNAPSHOT_6_ACTIVE
- added possibility to create Beans conditionally based on business context

## Release 2.4.0
- added designation uses to codes
- added FEATURE_FLAG_ADD_DESIGNATION_USE
- updated spring-parent to 2.14.19
- added version parameter to CodeDisplay object
- added version for snomed and loinc to prepared pathogen data for front end
- added FEATURE_FLAG_SNOMED_VERSION_FROM_FUTS
- removed feature flag FEATURE_FLAG_SNAPSHOT_6_ACTIVE

## Release 2.3.4
- upgraded fhir-package-initializer to 1.0.6 to reduce wait time for istio-proxy
- removed FEATURE_FLAG_DISEASE_DATEPICKER
- upgraded dependencies
- add new endpoints for follow up disease and pathogen code displays

## Release 2.3.3
- upgraded fhir-package-initializer to 1.0.5 for faster package loading

## Release 2.3.2
- added feature flag feature.flag.snapshot.6.active to additional service

## Release 2.3.1
- added feature flag feature.flag.snapshot.6.active

## Release 2.3.0
- updaded version of Notification Builder to 8.0.0
- derived datepicker precision from regex in strict disease profile (only for the dynamically built part of the form)
- added support of FHIR packages through new Docker base image (FHIR package initializer)
- added tooltip integration from FHIR structure definitions to disease formly forms
- integrated FHIR profile files of R4 terminology and DEMIS test notification category GAPP
- added new endpoint to retrieve all notification categories for §7.1 notifications
- added code to quantity formly representation
- replaced FEATURE_FLAG_FUTS_VALUESETS_SNOMED with FEATURE_FLAG_DISEASE_STRICT
- replaced FEATURE_FLAG_HOSP_REASON_MOVE with FEATURE_FLAG_DISEASE_STRICT

## Release 2.2.3
- Upgraded dependencies
- parsed questionnaire items referencing Quantity types

## Release 2.2.2
- disease frontend date inputs changed to date picker component
- set test data creation for disease notification category to false
- removed feature flag FEATURE_FLAG_HOSP_COPY_CHECKBOXES

## Release 2.2.1
- added default feature flag FEATURE_FLAG_FUTS_VALUESETS_SNOMED to values.yaml
- upgraded spring parent to 2.12.3

## Release 2.2.0
- fix: set required attribute for text-based FieldGroups
- Updated ospo-resources for adding additional notes and disclaimer
- setting new ressources in helm chart
- setting new timeouts and retries in helm chart
- updating dependencies
- new endpoints and logic to process nonnominal notifications for IfsG §7.3
- added version to system for strict laboratory notification

## Release 2.1.0
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

## Release 1.2.3
- Using Spring-Parent
- Fix CVEs
- Extended Helm Chart for Kubernetes

## Release 1.2.1
- Using Spring-Parent
- Fixed CVEs

## Release 1.2.0
- SpringBoot 3.2.0

## Release 1.1.5
- Observability features
- SpringBoot 3.1.4

## Release 1.0.0
- SpringBoot 3.0.1
- License updated to 2023
- SonarQube Code Smells
- Removed SnakeYAML (OWASP Scan)
