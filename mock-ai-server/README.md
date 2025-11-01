# Mock AI Server

Simple mock server for testing the TicketScan AI service integration.

## Endpoints

- `GET /health` - Health check
- `POST /api/analyze/image` - Analyze image and return ticket data
- `POST /api/analyze/audio` - Analyze audio and return ticket data
- `POST /api/analyze/text` - Analyze text and return ticket data

## Running the server

```bash
./gradlew run
```

Server will start on `http://localhost:8080`

## Example Response

All endpoints return a JSON ticket structure:

```json
{
  "id": "uuid",
  "date": "2024-11-01T14:58:00Z",
  "store": {
    "id": "uuid",
    "name": "Supermercado Central",
    "cuit": 30123456789,
    "location": "Av. Principal 123"
  },
  "origin": "MEDIA",
  "items": [
    {
      "id": "uuid",
      "name": "Pan integral",
      "category": {
        "id": "uuid",
        "name": "Panadería",
        "color": "#FFB74D"
      },
      "quantity": 2,
      "isIntUnit": true,
      "price": 450.50
    }
  ],
  "total": 1591.50
}
```
