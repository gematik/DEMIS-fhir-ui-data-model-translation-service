# LaboratoryDataLoaderCtrApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**get73NotificationCategories**](LaboratoryDataLoaderCtrApi.md#get73NotificationCategories) | **GET** /laboratory/7.3 |  |
| [**getAvailableFederalStates**](LaboratoryDataLoaderCtrApi.md#getAvailableFederalStates) | **GET** /laboratory/federalStates |  |
| [**getAvailableFederalStates1**](LaboratoryDataLoaderCtrApi.md#getAvailableFederalStates1) | **GET** /laboratory/7.1/federalStates |  |
| [**getLaboratoryDataForSpecificCode**](LaboratoryDataLoaderCtrApi.md#getLaboratoryDataForSpecificCode) | **GET** /laboratory/7.3/pathogenData/{code} |  |
| [**getLaboratoryDataForSpecificCode1**](LaboratoryDataLoaderCtrApi.md#getLaboratoryDataForSpecificCode1) | **GET** /laboratory/7.1/federalState/pathogenData/{code} |  |
| [**getLaboratoryDataForSpecificCode2**](LaboratoryDataLoaderCtrApi.md#getLaboratoryDataForSpecificCode2) | **GET** /laboratory/federalState/pathogenData/{code} |  |
| [**getLaboratoryDataForSpecificCodeAndFederalState**](LaboratoryDataLoaderCtrApi.md#getLaboratoryDataForSpecificCodeAndFederalState) | **GET** /laboratory/federalState/{federalState} |  |
| [**getLaboratoryDataForSpecificCodeAndFederalState1**](LaboratoryDataLoaderCtrApi.md#getLaboratoryDataForSpecificCodeAndFederalState1) | **GET** /laboratory/7.1/federalState/{federalState} |  |


<a name="get73NotificationCategories"></a>
# **get73NotificationCategories**
> List get73NotificationCategories()



### Parameters
This endpoint does not need any parameter.

### Return type

[**List**](../Models/CodeDisplay.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="getAvailableFederalStates"></a>
# **getAvailableFederalStates**
> List getAvailableFederalStates()



### Parameters
This endpoint does not need any parameter.

### Return type

[**List**](../Models/CodeDisplay.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="getAvailableFederalStates1"></a>
# **getAvailableFederalStates1**
> List getAvailableFederalStates1()



### Parameters
This endpoint does not need any parameter.

### Return type

[**List**](../Models/CodeDisplay.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="getLaboratoryDataForSpecificCode"></a>
# **getLaboratoryDataForSpecificCode**
> LabNotificationData getLaboratoryDataForSpecificCode(code)



### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **code** | **String**|  | [default to null] |

### Return type

[**LabNotificationData**](../Models/LabNotificationData.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="getLaboratoryDataForSpecificCode1"></a>
# **getLaboratoryDataForSpecificCode1**
> LabNotificationData getLaboratoryDataForSpecificCode1(code)



### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **code** | **String**|  | [default to null] |

### Return type

[**LabNotificationData**](../Models/LabNotificationData.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="getLaboratoryDataForSpecificCode2"></a>
# **getLaboratoryDataForSpecificCode2**
> LabNotificationData getLaboratoryDataForSpecificCode2(code)



### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **code** | **String**|  | [default to null] |

### Return type

[**LabNotificationData**](../Models/LabNotificationData.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="getLaboratoryDataForSpecificCodeAndFederalState"></a>
# **getLaboratoryDataForSpecificCodeAndFederalState**
> List getLaboratoryDataForSpecificCodeAndFederalState(federalState)



### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **federalState** | **String**|  | [default to null] |

### Return type

[**List**](../Models/CodeDisplay.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="getLaboratoryDataForSpecificCodeAndFederalState1"></a>
# **getLaboratoryDataForSpecificCodeAndFederalState1**
> List getLaboratoryDataForSpecificCodeAndFederalState1(federalState)



### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **federalState** | **String**|  | [default to null] |

### Return type

[**List**](../Models/CodeDisplay.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

