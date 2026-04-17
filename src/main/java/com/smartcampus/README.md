# Smart Campus API

A JAX-RS RESTful API for managing Rooms and Sensors across a university campus, built with Jersey 2.41 and Grizzly HTTP Server.

## Tech Stack
- Java 24
- JAX-RS (Jersey 2.41)
- Grizzly HTTP Server (embedded)
- In-memory storage (ConcurrentHashMap)

## Project Structure
```
src/main/java/com/smartcampus/
├── app/          - Application entry point and JAX-RS config
├── model/        - Room, Sensor, SensorReading POJOs
├── store/        - In-memory DataStore
├── resource/     - REST resource classes
├── exception/    - Custom exceptions and mappers
└── filter/       - Logging filter
```

## How to Build and Run

### Prerequisites
- Java 11 or above
- Apache Maven 3.9+

### Steps
```bash
mvn clean package
java -jar target/smart-campus-1.0-SNAPSHOT.jar
```

Server starts at: `http://localhost:8080/api/v1`

## Sample curl Commands

### 1. Discovery endpoint
```bash
curl http://localhost:8080/api/v1
```

### 2. Create a Room
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"LIB-301","name":"Library Quiet Study","capacity":50}'
```

### 3. Get all Rooms
```bash
curl http://localhost:8080/api/v1/rooms
```

### 4. Create a Sensor
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","currentValue":22.5,"roomId":"LIB-301"}'
```

### 5. Get Sensors filtered by type
```bash
curl http://localhost:8080/api/v1/sensors/search?type=Temperature
```

### 6. Post a Sensor Reading
```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":24.3}'
```

### 7. Get Sensor Readings
```bash
curl http://localhost:8080/api/v1/sensors/TEMP-001/readings
```

### 8. Delete a Room
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

## Report

### Part 1 — JAX-RS Resource Lifecycle
By default JAX-RS creates a new instance of a resource class for every incoming request (per-request lifecycle). This means instance variables are not shared between requests. To safely share in-memory data across requests, static data structures with ConcurrentHashMap are used in the DataStore class. ConcurrentHashMap handles concurrent access from multiple requests without race conditions or data loss, making it a safe alternative to a regular HashMap in a multi-threaded server environment.

### Part 1 — HATEOAS
Hypermedia as the Engine of Application State (HATEOAS) means API responses include links to related resources, allowing clients to navigate the API dynamically without relying on hardcoded URLs or static documentation. This benefits client developers because they can discover available actions and endpoints directly from the API responses, reducing coupling between client and server. If the server changes a URL structure, clients that follow links from responses will adapt automatically rather than breaking.

### Part 2 — IDs vs Full Objects in List Response
Returning only IDs saves bandwidth but forces clients to make N additional requests to fetch each room's details, increasing latency and complexity on the client side. Returning full objects increases payload size but reduces the number of round trips. For small datasets like a campus room list, returning full objects is preferred as it simplifies client logic. For very large datasets, a paginated response with full objects or a lightweight summary projection would be ideal.

### Part 2 — DELETE Idempotency
Yes, DELETE is idempotent in this implementation. The first call removes the room and returns 204 No Content. Any subsequent call for the same room ID finds no room and returns 404 Not Found. Although the status codes differ, the server state is identical after both calls — the room does not exist — which satisfies the definition of idempotency. The resource state on the server does not change after the first successful deletion regardless of how many times the request is repeated.

### Part 3 — @Consumes and Format Mismatch
If a client sends data with a Content-Type of text/plain or application/xml to an endpoint annotated with @Consumes(MediaType.APPLICATION_JSON), JAX-RS automatically intercepts the request before it even reaches the resource method and returns an HTTP 415 Unsupported Media Type response. The resource method is never invoked. This protects the API from malformed or unexpected input formats without any additional validation code inside the method itself.

### Part 3 — @QueryParam vs Path Segment for Filtering
Query parameters are semantically correct for optional filtering of a collection. The path /sensors?type=CO2 correctly expresses "give me the sensors collection, filtered by type CO2". In contrast, /sensors/type/CO2 implies that CO2 is a distinct named sub-resource under sensors, which breaks REST conventions and creates confusion. Query parameters are also optional by nature — the same endpoint handles both filtered and unfiltered requests cleanly — whereas a path segment approach would require separate endpoint definitions for each filter combination.

### Part 4 — Sub-Resource Locator Benefits
The sub-resource locator pattern delegates nested path handling to dedicated classes, keeping each resource class focused on a single responsibility. This makes the codebase easier to maintain, test and extend independently. In contrast, defining every nested path such as /sensors/{id}/readings/{rid} inside one massive controller class becomes unreadable at scale and violates the single responsibility principle. The locator pattern also allows sub-resources to be instantiated with context — in this case the sensorId — passed directly from the parent resource.

### Part 5 — 422 vs 404 for Missing Reference
HTTP 404 means the requested URL endpoint was not found on the server. HTTP 422 Unprocessable Entity means the request was syntactically valid and the endpoint exists, but the semantic content of the payload is invalid. When a client POSTs a sensor with a roomId that does not exist, the /sensors endpoint itself is perfectly valid — the problem is inside the request body. Using 422 communicates precisely that the server understood the request but could not process it due to a missing referenced resource, which is more informative and semantically accurate than a generic 404.

### Part 5 — Stack Trace Exposure Risks
Exposing raw Java stack traces to external API consumers reveals sensitive internal information including class names and package structure, library names and version numbers, internal file paths and line numbers, and method signatures. Attackers can use this information to identify known vulnerabilities in specific library versions, understand the internal architecture for targeted attacks, and craft exploits based on exposed method names and call chains. The global ExceptionMapper<Throwable> prevents this by intercepting all unhandled errors and returning a safe generic 500 message with no internal details.

### Part 5 — Filters vs Inline Logging
JAX-RS filters implement cross-cutting concerns in one centralised place. If logging were added manually inside every resource method, any change to the log format would require editing every single resource class. Filters are registered once via the @Provider annotation and applied automatically to every request and response without touching the resource methods at all. This follows the DRY principle, keeps resource methods clean and focused purely on business logic, and makes the logging behaviour easy to modify or extend in the future.