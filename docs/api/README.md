# Documentation for FHIR UI Data Model Translation Service

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *http://localhost*

| Class | Method | HTTP request | Description |
|------------ | ------------- | ------------- | -------------|
| *CodeSystemCtrApi* | [**getAvailableCodeSystems1**](Apis/CodeSystemCtrApi.md#getAvailableCodeSystems1) | **GET** /CodeSystem |  |
*CodeSystemCtrApi* | [**getCode2**](Apis/CodeSystemCtrApi.md#getCode2) | **GET** /CodeSystem/{system}/{code} |  |
*CodeSystemCtrApi* | [**getSystemContent**](Apis/CodeSystemCtrApi.md#getSystemContent) | **GET** /CodeSystem/{system} |  |
| *ConceptMapsCtrApi* | [**getAllConceptMaps**](Apis/ConceptMapsCtrApi.md#getAllConceptMaps) | **GET** /conceptmap |  |
*ConceptMapsCtrApi* | [**getCode**](Apis/ConceptMapsCtrApi.md#getCode) | **GET** /conceptmap/{name}/{code} |  |
*ConceptMapsCtrApi* | [**getConceptMap**](Apis/ConceptMapsCtrApi.md#getConceptMap) | **GET** /conceptmap/{name} |  |
| *DiseaseDataLoaderCtrApi* | [**getAllAvailableCodes**](Apis/DiseaseDataLoaderCtrApi.md#getAllAvailableCodes) | **GET** /disease/6.1 |  |
*DiseaseDataLoaderCtrApi* | [**getAllAvailableCodes1**](Apis/DiseaseDataLoaderCtrApi.md#getAllAvailableCodes1) | **GET** /disease |  |
*DiseaseDataLoaderCtrApi* | [**getAllAvailableCodesNonNominal**](Apis/DiseaseDataLoaderCtrApi.md#getAllAvailableCodesNonNominal) | **GET** /disease/7.3 |  |
*DiseaseDataLoaderCtrApi* | [**getFormlyRepresentationOfQuestionnaire**](Apis/DiseaseDataLoaderCtrApi.md#getFormlyRepresentationOfQuestionnaire) | **GET** /disease/6.1/questionnaire/{code}/formly |  |
*DiseaseDataLoaderCtrApi* | [**getFormlyRepresentationOfQuestionnaire1**](Apis/DiseaseDataLoaderCtrApi.md#getFormlyRepresentationOfQuestionnaire1) | **GET** /disease/questionnaire/{code}/formly |  |
*DiseaseDataLoaderCtrApi* | [**getFormlyRepresentationOfQuestionnaireNonNominal**](Apis/DiseaseDataLoaderCtrApi.md#getFormlyRepresentationOfQuestionnaireNonNominal) | **GET** /disease/7.3/questionnaire/{code}/formly |  |
*DiseaseDataLoaderCtrApi* | [**getPossibleDiseaseCodesForFollowUp**](Apis/DiseaseDataLoaderCtrApi.md#getPossibleDiseaseCodesForFollowUp) | **GET** /disease/6.1/followup/{code} |  |
*DiseaseDataLoaderCtrApi* | [**getPossibleDiseaseCodesForNonNominalFollowUp**](Apis/DiseaseDataLoaderCtrApi.md#getPossibleDiseaseCodesForNonNominalFollowUp) | **GET** /disease/7.3/followup/{code} |  |
*DiseaseDataLoaderCtrApi* | [**getQuestionsForSpecificCode**](Apis/DiseaseDataLoaderCtrApi.md#getQuestionsForSpecificCode) | **GET** /disease/questionnaire/{code}/items |  |
| *LaboratoryDataLoaderCtrApi* | [**get71NotificationCategories**](Apis/LaboratoryDataLoaderCtrApi.md#get71NotificationCategories) | **GET** /laboratory/7.1 |  |
*LaboratoryDataLoaderCtrApi* | [**get73NotificationCategories**](Apis/LaboratoryDataLoaderCtrApi.md#get73NotificationCategories) | **GET** /laboratory/7.3 |  |
*LaboratoryDataLoaderCtrApi* | [**getAvailableFederalStates**](Apis/LaboratoryDataLoaderCtrApi.md#getAvailableFederalStates) | **GET** /laboratory/federalStates |  |
*LaboratoryDataLoaderCtrApi* | [**getAvailableFederalStates1**](Apis/LaboratoryDataLoaderCtrApi.md#getAvailableFederalStates1) | **GET** /laboratory/7.1/federalStates |  |
*LaboratoryDataLoaderCtrApi* | [**getLaboratoryDataForSpecificCode**](Apis/LaboratoryDataLoaderCtrApi.md#getLaboratoryDataForSpecificCode) | **GET** /laboratory/7.3/pathogenData/{code} |  |
*LaboratoryDataLoaderCtrApi* | [**getLaboratoryDataForSpecificCode1**](Apis/LaboratoryDataLoaderCtrApi.md#getLaboratoryDataForSpecificCode1) | **GET** /laboratory/7.1/federalState/pathogenData/{code} |  |
*LaboratoryDataLoaderCtrApi* | [**getLaboratoryDataForSpecificCode2**](Apis/LaboratoryDataLoaderCtrApi.md#getLaboratoryDataForSpecificCode2) | **GET** /laboratory/federalState/pathogenData/{code} |  |
*LaboratoryDataLoaderCtrApi* | [**getLaboratoryDataForSpecificCodeAndFederalState**](Apis/LaboratoryDataLoaderCtrApi.md#getLaboratoryDataForSpecificCodeAndFederalState) | **GET** /laboratory/federalState/{federalState} |  |
*LaboratoryDataLoaderCtrApi* | [**getLaboratoryDataForSpecificCodeAndFederalState1**](Apis/LaboratoryDataLoaderCtrApi.md#getLaboratoryDataForSpecificCodeAndFederalState1) | **GET** /laboratory/7.1/federalState/{federalState} |  |
*LaboratoryDataLoaderCtrApi* | [**getPossibleLaboratoryCodesForFollowUp**](Apis/LaboratoryDataLoaderCtrApi.md#getPossibleLaboratoryCodesForFollowUp) | **GET** /laboratory/7.1/followup/{code} |  |
*LaboratoryDataLoaderCtrApi* | [**getPossibleLaboratoryCodesForNonNominalFollowUp**](Apis/LaboratoryDataLoaderCtrApi.md#getPossibleLaboratoryCodesForNonNominalFollowUp) | **GET** /laboratory/7.3/followup/{code} |  |
| *UtilsCtrApi* | [**getDoubleCharacterCountryCode**](Apis/UtilsCtrApi.md#getDoubleCharacterCountryCode) | **GET** /utils/countryCodes |  |
| *ValueSetCtrApi* | [**getAvailableCodeSystems**](Apis/ValueSetCtrApi.md#getAvailableCodeSystems) | **GET** /ValueSet |  |
*ValueSetCtrApi* | [**getCode1**](Apis/ValueSetCtrApi.md#getCode1) | **GET** /ValueSet/{system}/{code} |  |
*ValueSetCtrApi* | [**getValueSetContent**](Apis/ValueSetCtrApi.md#getValueSetContent) | **GET** /ValueSet/{system} |  |


<a name="documentation-for-models"></a>
## Documentation for Models

 - [CodeDisplay](./Models/CodeDisplay.md)
 - [Designation](./Models/Designation.md)
 - [EnableWhen](./Models/EnableWhen.md)
 - [FieldGroup](./Models/FieldGroup.md)
 - [FormlyFieldConfigs](./Models/FormlyFieldConfigs.md)
 - [ImportSpec](./Models/ImportSpec.md)
 - [LabNotificationData](./Models/LabNotificationData.md)
 - [Props](./Models/Props.md)
 - [Quantity](./Models/Quantity.md)
 - [QuestionnaireTranslation](./Models/QuestionnaireTranslation.md)
 - [StaticSystemVersion](./Models/StaticSystemVersion.md)
 - [Use](./Models/Use.md)
 - [Validator](./Models/Validator.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

All endpoints do not require authorization.
