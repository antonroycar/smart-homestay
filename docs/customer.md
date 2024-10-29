## Register Customer

Endpoint : POST /auth/register

### Request Body : 

```json
{
    "username": "roycar",
    "password": "roycar",
    "name": "Anton Roycar Nababan",
    "age": 22,
    "gender": "MALE",
    "contactNumber": "08123456789",
    "address": "bandung",
    "role" : "CUSTOMER"
}
```

### Response Body :

```json
"Customer registered successfully"
```

## Login Customer

Endpoint : POST /auth/login

### Request Body : 
```json
{
    "username": "roycar",
    "password": "roycar"
}
```

### Response Body :

```json
{
    "token" : "TOKEN",
    "expiredAt" : 2342342423423 // milliseconds
}
```

## Logout Customer

Endpoint : DELETE /auth/logout

### Request Header :
X-API-TOKEN : Token (Mandatory)

### Response Body :
```json
"Logout successfully"
```
