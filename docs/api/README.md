# Documentation for FHIR UI Data Model Translation Service

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *http://localhost*

| Class | Method | HTTP request | Description |
|------------ | ------------- | ------------- | -------------|
| *CodeSystemCtrApi* | [**getAvailableCodeSystems1**](Apis/CodeSystemCtrApi.md#getavailablecodesystems1) | **GET** /CodeSystem |  |
*CodeSystemCtrApi* | [**getCode2**](Apis/CodeSystemCtrApi.md#getcode2) | **GET** /CodeSystem/{system}/{code} |  |
*CodeSystemCtrApi* | [**getSystemContent**](Apis/CodeSystemCtrApi.md#getsystemcontent) | **GET** /CodeSystem/{system} |  |
| *ConceptMapsCtrApi* | [**getAllConceptMaps**](Apis/ConceptMapsCtrApi.md#getallconceptmaps) | **GET** /conceptmap |  |
*ConceptMapsCtrApi* | [**getCode**](Apis/ConceptMapsCtrApi.md#getcode) | **GET** /conceptmap/{name}/{code} |  |
*ConceptMapsCtrApi* | [**getConceptMap**](Apis/ConceptMapsCtrApi.md#getconceptmap) | **GET** /conceptmap/{name} |  |
| *DiseaseDataLoaderCtrApi* | [**getAllAvailableCodes**](Apis/DiseaseDataLoaderCtrApi.md#getallavailablecodes) | **GET** /disease |  |
*DiseaseDataLoaderCtrApi* | [**getFormlyRepresentationOfQuestionnaire**](Apis/DiseaseDataLoaderCtrApi.md#getformlyrepresentationofquestionnaire) | **GET** /disease/questionnaire/{code}/formly |  |
*DiseaseDataLoaderCtrApi* | [**getQuestionsForSpecificCode**](Apis/DiseaseDataLoaderCtrApi.md#getquestionsforspecificcode) | **GET** /disease/questionnaire/{code}/items |  |
| *LaboratoryDataLoaderCtrApi* | [**get73NotificationCategories**](Apis/LaboratoryDataLoaderCtrApi.md#get73notificationcategories) | **GET** /laboratory/7.3 |  |
*LaboratoryDataLoaderCtrApi* | [**getAvailableFederalStates**](Apis/LaboratoryDataLoaderCtrApi.md#getavailablefederalstates) | **GET** /laboratory/federalStates |  |
*LaboratoryDataLoaderCtrApi* | [**getAvailableFederalStates1**](Apis/LaboratoryDataLoaderCtrApi.md#getavailablefederalstates1) | **GET** /laboratory/7.1/federalStates |  |
*LaboratoryDataLoaderCtrApi* | [**getFederalStateLaboratoryDataForSpecificCode**](Apis/LaboratoryDataLoaderCtrApi.md#getfederalstatelaboratorydataforspecificcode) | **GET** /laboratory/7.3/pathogenData/{code} |  |
*LaboratoryDataLoaderCtrApi* | [**getFederalStateLaboratoryDataForSpecificCode1**](Apis/LaboratoryDataLoaderCtrApi.md#getfederalstatelaboratorydataforspecificcode1) | **GET** /laboratory/7.1/federalState/pathogenData/{code} |  |
*LaboratoryDataLoaderCtrApi* | [**getFederalStateLaboratoryDataForSpecificCode2**](Apis/LaboratoryDataLoaderCtrApi.md#getfederalstatelaboratorydataforspecificcode2) | **GET** /laboratory/federalState/pathogenData/{code} |  |
*LaboratoryDataLoaderCtrApi* | [**getLaboratoryDataForSpecificCodeAndFederalState**](Apis/LaboratoryDataLoaderCtrApi.md#getlaboratorydataforspecificcodeandfederalstate) | **GET** /laboratory/federalState/{federalState} |  |
*LaboratoryDataLoaderCtrApi* | [**getLaboratoryDataForSpecificCodeAndFederalState1**](Apis/LaboratoryDataLoaderCtrApi.md#getlaboratorydataforspecificcodeandfederalstate1) | **GET** /laboratory/7.1/federalState/{federalState} |  |
| *UtilsCtrApi* | [**getDoubleCharacterCountryCode**](Apis/UtilsCtrApi.md#getdoublecharactercountrycode) | **GET** /utils/countryCodes |  |
| *ValueSetCtrApi* | [**getAvailableCodeSystems**](Apis/ValueSetCtrApi.md#getavailablecodesystems) | **GET** /ValueSet |  |
*ValueSetCtrApi* | [**getCode1**](Apis/ValueSetCtrApi.md#getcode1) | **GET** /ValueSet/{system}/{code} |  |
*ValueSetCtrApi* | [**getValueSetContent**](Apis/ValueSetCtrApi.md#getvaluesetcontent) | **GET** /ValueSet/{system} |  |


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
 - [QuestionnaireTranslation](./Models/QuestionnaireTranslation.md)
 - [Validator](./Models/Validator.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

All endpoints do not require authorization.
