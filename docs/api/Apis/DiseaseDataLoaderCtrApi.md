# DiseaseDataLoaderCtrApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getAllAvailableCodes**](DiseaseDataLoaderCtrApi.md#getAllAvailableCodes) | **GET** /disease/6.1 |  |
| [**getAllAvailableCodes1**](DiseaseDataLoaderCtrApi.md#getAllAvailableCodes1) | **GET** /disease |  |
| [**getAllAvailableCodesNonNominal**](DiseaseDataLoaderCtrApi.md#getAllAvailableCodesNonNominal) | **GET** /disease/7.3 |  |
| [**getFormlyRepresentationOfQuestionnaire**](DiseaseDataLoaderCtrApi.md#getFormlyRepresentationOfQuestionnaire) | **GET** /disease/6.1/questionnaire/{code}/formly |  |
| [**getFormlyRepresentationOfQuestionnaire1**](DiseaseDataLoaderCtrApi.md#getFormlyRepresentationOfQuestionnaire1) | **GET** /disease/questionnaire/{code}/formly |  |
| [**getFormlyRepresentationOfQuestionnaireNonNominal**](DiseaseDataLoaderCtrApi.md#getFormlyRepresentationOfQuestionnaireNonNominal) | **GET** /disease/7.3/questionnaire/{code}/formly |  |
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

<a name="getAllAvailableCodes1"></a>
# **getAllAvailableCodes1**
> List getAllAvailableCodes1()



### Parameters
This endpoint does not need any parameter.

### Return type

[**List**](../Models/CodeDisplay.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="getAllAvailableCodesNonNominal"></a>
# **getAllAvailableCodesNonNominal**
> List getAllAvailableCodesNonNominal()



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

<a name="getFormlyRepresentationOfQuestionnaire1"></a>
# **getFormlyRepresentationOfQuestionnaire1**
> Map getFormlyRepresentationOfQuestionnaire1(code)



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

<a name="getFormlyRepresentationOfQuestionnaireNonNominal"></a>
# **getFormlyRepresentationOfQuestionnaireNonNominal**
> Map getFormlyRepresentationOfQuestionnaireNonNominal(code)



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

