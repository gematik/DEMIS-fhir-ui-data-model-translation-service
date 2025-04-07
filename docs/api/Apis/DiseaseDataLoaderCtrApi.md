# DiseaseDataLoaderCtrApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getAllAvailableCodes**](DiseaseDataLoaderCtrApi.md#getAllAvailableCodes) | **GET** /disease |  |
| [**getFormlyRepresentationOfQuestionnaire**](DiseaseDataLoaderCtrApi.md#getFormlyRepresentationOfQuestionnaire) | **GET** /disease/questionnaire/{code}/formly |  |
| [**getQuestionsForSpecificCode**](DiseaseDataLoaderCtrApi.md#getQuestionsForSpecificCode) | **GET** /disease/questionnaire/{code}/items |  |


<a name="getAllAvailableCodes"></a>
# **getAllAvailableCodes**
> List getAllAvailableCodes()



### Parameters
This endpoint does not need any parameter.

### Return type

[**List**](../Models/CodeDisplay.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="getFormlyRepresentationOfQuestionnaire"></a>
# **getFormlyRepresentationOfQuestionnaire**
> Map getFormlyRepresentationOfQuestionnaire(code)



### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **code** | **String**|  | [default to null] |

### Return type

[**Map**](../Models/array.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="getQuestionsForSpecificCode"></a>
# **getQuestionsForSpecificCode**
> QuestionnaireTranslation getQuestionsForSpecificCode(code)



### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **code** | **String**|  | [default to null] |

### Return type

[**QuestionnaireTranslation**](../Models/QuestionnaireTranslation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

