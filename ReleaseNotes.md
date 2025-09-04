<img align="right" width="250" height="47" src="media/Gematik_Logo_Flag.png" alt="gematik logo"/> <br/> 
 
# Release notes

## 2.2.2
### changed
- disease frontend date inputs changed to date picker component
- set test data creation for disease notification category to false
- remove feature flag FEATURE_FLAG_HOSP_COPY_CHECKBOXES

## 2.2.1
### changed
- add default feature flag FEATURE_FLAG_FUTS_VALUESETS_SNOMED to values.yaml
- upgrade spring parent to 2.12.3

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
- add version to system for strict laboratory notification

## 2.1.0
### changed
- First official GitHub-Release
- Sorting of ValueSet and CodeSytem
- Add notification status and initial message
- Add diagnosis note
- Resistances added for §7.1
- Switch to the delegation pattern from istio
- Display of state-specific pathogen detections
- Change country codes in the bundles to FHIR standard
- Transfer the reason for hospitalization to the general questionnaire
- Simplification of entering the hospital address for hospitalization
- Dependency-Updates (CVEs et al.)
- Update Base-Image to OSADL

## 1.2.3 (2024-04-15)
### changed
- Using Spring-Parent
- Fix CVEs
- Extended Helm Chart for Kubernetes

## 1.2.1 (2023-12-20)
### changed
- Using Spring-Parent
- Fix CVEs 

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
