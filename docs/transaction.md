## Transaction

Endpoint : POST /api/transactions

### Request Header
X-API-TOKEN : Token (Mandatory)

### Request Body
```json
{
    "reservationId": "671efc177a99c639ce4799b2"
}
```

### Response Body
```json
{
  "transactionId": "671f02bffbb9cc3f92866453",
  "reservation": {
    "reservationId": "671f0026fbb9cc3f92866449",
    "accountId": "671efbee7a99c639ce4799b0",
    "roomTypeDetails": [
      {
        "type": "SINGLE",
        "description": "Single Room",
        "price": 100.0,
        "quantity": 1
      },
      {
        "type": "DOUBLE",
        "description": "Double Room",
        "price": 150.0,
        "quantity": 1
      }
    ],
    "guestDetails": {
      "adults": 2,
      "children": 1,
      "quantity": 3
    },
    "dateRange": {
      "checkInDate": "2024-12-01T00:00:00.000+00:00",
      "checkOutDate": "2024-12-05T00:00:00.000+00:00"
    }
  },
  "totalAmount": 250.0,
  "status": "PENDING",
  "transactionDate": "2024-10-28T03:19:27.910+00:00"
}
```