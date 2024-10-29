## Register Crew

Endpoint : POST /auth/register

### Request Body :

```json
{
  "username": "anton",
  "password": "anton",
  "name": "Anton Roycar Nababan",
  "jobTitle": "Manager",
  "role" : "CREW"
}
```

### Response Body :

```json
"Crew registered successfully"
```

## Login Crew

Endpoint : POST /auth/login

### Request Body :
```json
{
  "username": "anton",
  "password": "anton"
}
```

### Response Body :

```json
{
    "token" : "TOKEN",
    "expiredAt" : 2342342423423 // milliseconds
}
```

## Logout Crew

Endpoint : DELETE /auth/logout

### Request Header :
X-API-TOKEN : Token (Mandatory)

### Response Body :
```json
"Logout successfully"
```
