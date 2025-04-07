# ValueSetCtrApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getAvailableCodeSystems**](ValueSetCtrApi.md#getAvailableCodeSystems) | **GET** /ValueSet |  |
| [**getCode1**](ValueSetCtrApi.md#getCode1) | **GET** /ValueSet/{system}/{code} |  |
| [**getValueSetContent**](ValueSetCtrApi.md#getValueSetContent) | **GET** /ValueSet/{system} |  |


<a name="getAvailableCodeSystems"></a>
# **getAvailableCodeSystems**
> String getAvailableCodeSystems(system, code, version)



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

<a name="getCode1"></a>
# **getCode1**
> String getCode1(system, code, version)



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

<a name="getValueSetContent"></a>
# **getValueSetContent**
> String getValueSetContent(system, version)



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

