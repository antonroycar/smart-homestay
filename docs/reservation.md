## Reservation 

Endpoint : POST /api/reservations

### Request Header
X-API-TOKEN : Token (Mandatory)

### Request Body :
```json
{
    "checkInDate": "2024-12-01",
    "checkOutDate": "2024-12-05",
    "adults": 2,
    "children": 1,
    "roomTypes": [
        {
            "roomType": "SINGLE",
            "quantity": 1
        },
        {
            "roomType": "DOUBLE",
            "quantity": 1
        }
    ]
}
```

### Response Body 
```json
{
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
}
```