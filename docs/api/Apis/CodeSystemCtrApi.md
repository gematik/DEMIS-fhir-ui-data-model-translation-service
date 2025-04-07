# CodeSystemCtrApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getAvailableCodeSystems1**](CodeSystemCtrApi.md#getAvailableCodeSystems1) | **GET** /CodeSystem |  |
| [**getCode2**](CodeSystemCtrApi.md#getCode2) | **GET** /CodeSystem/{system}/{code} |  |
| [**getSystemContent**](CodeSystemCtrApi.md#getSystemContent) | **GET** /CodeSystem/{system} |  |


<a name="getAvailableCodeSystems1"></a>
# **getAvailableCodeSystems1**
> String getAvailableCodeSystems1(system, code, version)



### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **system** | **String**|  | [optional] [default to null] |
| **code** | **String**|  | [optional] [default to null] |
| **version** | **String**|  | [optional] [default to null] |

### Return type

**String**

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*, application/json

<a name="getCode2"></a>
# **getCode2**
> String getCode2(system, code, version)



### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **system** | **String**|  | [default to null] |
| **code** | **String**|  | [default to null] |
| **version** | **String**|  | [optional] [default to null] |

### Return type

**String**

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*, application/json

<a name="getSystemContent"></a>
# **getSystemContent**
> String getSystemContent(system, version)



### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **system** | **String**|  | [default to null] |
| **version** | **String**|  | [optional] [default to null] |

### Return type

**String**

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*, application/json

