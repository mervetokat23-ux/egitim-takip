# Payment Management API Documentation

## Overview
Complete payment management backend with CRUD operations, filtering, validation, logging, and authorization.

---

## Endpoints

### 1. GET /odeme
**Description**: List all payments with pagination and filtering

**Authorization**: All authenticated users

**Query Parameters**:
- `page` (int, default: 0) - Page number (0-indexed)
- `size` (int, default: 10) - Page size
- `sort` (string, default: "id,desc") - Sort field and direction (e.g., "id,asc")
- `egitimId` (long, optional) - Filter by education ID
- `durum` (string, optional) - Filter by status (Ödendi, Bekliyor, İptal)
- `sorumluId` (long, optional) - Filter by responsible person ID
- `odemeKaynagi` (string, optional) - Filter by payment source

**Response**: `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "birimUcret": 100.00,
      "toplamUcret": 500.00,
      "odemeKaynagi": "Banka Havalesi",
      "durum": "Ödendi",
      "operasyon": "Havale",
      "isDeleted": false,
      "createdAt": "2025-12-09T10:30:00",
      "updatedAt": "2025-12-09T10:30:00",
      "egitim": {
        "id": 1,
        "ad": "Java Eğitimi"
      },
      "sorumlu": {
        "id": 1,
        "ad": "Ahmet Yılmaz"
      }
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

**Example**:
```bash
curl -X GET "http://localhost:8080/odeme?page=0&size=10&durum=Ödendi&egitimId=1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 2. GET /odeme/{id}
**Description**: Get payment details by ID

**Authorization**: All authenticated users

**Path Parameters**:
- `id` (long, required) - Payment ID

**Response**: `200 OK`
```json
{
  "id": 1,
  "birimUcret": 100.00,
  "toplamUcret": 500.00,
  "odemeKaynagi": "Banka Havalesi",
  "durum": "Ödendi",
  "operasyon": "Havale",
  "isDeleted": false,
  "createdAt": "2025-12-09T10:30:00",
  "updatedAt": "2025-12-09T10:30:00",
  "egitim": {
    "id": 1,
    "ad": "Java Eğitimi",
    "il": "İstanbul"
  },
  "sorumlu": {
    "id": 1,
    "ad": "Ahmet Yılmaz",
    "email": "ahmet@example.com"
  }
}
```

**Error Response**: `404 NOT FOUND`
```json
{
  "error": "Ödeme bulunamadı: 999"
}
```

**Activity Log**: Logs "VIEW" action for PAYMENT entity

**Example**:
```bash
curl -X GET "http://localhost:8080/odeme/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 3. POST /odeme
**Description**: Create a new payment

**Authorization**: ADMIN or SORUMLU roles required

**Request Body**:
```json
{
  "egitimId": 1,
  "birimUcret": 100.00,
  "toplamUcret": 500.00,
  "odemeKaynagi": "Banka Havalesi",
  "durum": "Ödendi",
  "operasyon": "Havale",
  "sorumluId": 1,
  "miktar": 5
}
```

**Validation Rules**:
- `egitimId`: Required, must exist in database
- `birimUcret`: Required, must be > 0, max 10 digits with 2 decimals
- `toplamUcret`: Required, must be > 0, max 10 digits with 2 decimals
- `odemeKaynagi`: Required, max 200 characters
- `durum`: Required, max 50 characters (e.g., "Ödendi", "Bekliyor", "İptal")
- `operasyon`: Optional, max 100 characters (e.g., "Havale", "Nakit", "POS", "Sistem içi")
- `sorumluId`: Optional, must exist if provided
- `miktar`: Optional, min 1 (used for validation: totalPrice = unitPrice * miktar)

**Response**: `201 CREATED`
```json
{
  "id": 1,
  "birimUcret": 100.00,
  "toplamUcret": 500.00,
  "odemeKaynagi": "Banka Havalesi",
  "durum": "Ödendi",
  "operasyon": "Havale",
  "isDeleted": false,
  "createdAt": "2025-12-09T10:30:00",
  "updatedAt": "2025-12-09T10:30:00",
  "egitim": { ... },
  "sorumlu": { ... }
}
```

**Error Responses**:
- `400 BAD REQUEST`: Validation error
```json
{
  "error": "Validasyon hatası: Birim ücret 0'dan büyük olmalıdır"
}
```
- `403 FORBIDDEN`: Insufficient permissions

**Activity Log**: Logs "CREATE" action for PAYMENT entity with details

**Example**:
```bash
curl -X POST "http://localhost:8080/odeme" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "egitimId": 1,
    "birimUcret": 100.00,
    "toplamUcret": 500.00,
    "odemeKaynagi": "Banka Havalesi",
    "durum": "Ödendi",
    "operasyon": "Havale",
    "sorumluId": 1
  }'
```

---

### 4. PUT /odeme/{id}
**Description**: Update an existing payment

**Authorization**: ADMIN or SORUMLU roles required

**Path Parameters**:
- `id` (long, required) - Payment ID

**Request Body**: Same as POST (all fields can be updated)

**Response**: `200 OK`
```json
{
  "id": 1,
  "birimUcret": 150.00,
  "toplamUcret": 750.00,
  "odemeKaynagi": "Nakit",
  "durum": "Ödendi",
  "operasyon": "Nakit",
  "isDeleted": false,
  "createdAt": "2025-12-09T10:30:00",
  "updatedAt": "2025-12-09T11:45:00",
  "egitim": { ... },
  "sorumlu": { ... }
}
```

**Error Responses**:
- `400 BAD REQUEST`: Validation error
- `403 FORBIDDEN`: Insufficient permissions
- `404 NOT FOUND`: Payment not found

**Activity Log**: Logs "UPDATE" action for PAYMENT entity with details

**Example**:
```bash
curl -X PUT "http://localhost:8080/odeme/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "egitimId": 1,
    "birimUcret": 150.00,
    "toplamUcret": 750.00,
    "odemeKaynagi": "Nakit",
    "durum": "Ödendi",
    "operasyon": "Nakit",
    "sorumluId": 1
  }'
```

---

### 5. DELETE /odeme/{id}
**Description**: Delete a payment (soft delete or hard delete based on configuration)

**Authorization**: ADMIN role required

**Path Parameters**:
- `id` (long, required) - Payment ID

**Response**: `204 NO CONTENT` (no body)

**Error Responses**:
- `403 FORBIDDEN`: Insufficient permissions (only ADMIN can delete)
- `404 NOT FOUND`: Payment not found

**Activity Log**: Logs "DELETE" action for PAYMENT entity with details

**Example**:
```bash
curl -X DELETE "http://localhost:8080/odeme/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 6. POST /odeme/calculate-total
**Description**: Calculate total price based on unit price and quantity

**Authorization**: All authenticated users

**Query Parameters**:
- `unitPrice` (decimal, required) - Unit price
- `quantity` (int, optional, default: 1) - Quantity

**Response**: `200 OK`
```json
{
  "unitPrice": 100.00,
  "quantity": 5,
  "totalPrice": 500.00
}
```

**Error Response**: `400 BAD REQUEST`
```json
{
  "error": "Hesaplama hatası: Birim ücret geçersiz"
}
```

**Example**:
```bash
curl -X POST "http://localhost:8080/odeme/calculate-total?unitPrice=100.00&quantity=5" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Data Model

### Odeme Entity
```java
{
  "id": Long,                    // Primary key (auto-generated)
  "birimUcret": BigDecimal,      // Unit price (precision: 10,2)
  "toplamUcret": BigDecimal,     // Total price (precision: 10,2)
  "odemeKaynagi": String,        // Payment source (max 200 chars)
  "durum": String,               // Status (max 50 chars)
  "operasyon": String,           // Operation type (max 100 chars)
  "isDeleted": Boolean,          // Soft delete flag
  "createdAt": LocalDateTime,    // Creation timestamp
  "updatedAt": LocalDateTime,    // Last update timestamp
  "egitim": Egitim,              // Foreign key to Egitim (required)
  "sorumlu": Sorumlu             // Foreign key to Sorumlu (optional)
}
```

### Database Indexes
- `idx_odeme_egitim_id` on `egitim_id`
- `idx_odeme_sorumlu_id` on `sorumlu_id`
- `idx_odeme_durum` on `durum`
- `idx_odeme_is_deleted` on `is_deleted`
- `idx_odeme_created_at` on `created_at`

---

## Validation Rules

### Business Logic Validation
1. **Unit Price**: Must be > 0
2. **Total Price**: Must be > 0
3. **Payment Source**: Cannot be empty
4. **Status**: Cannot be empty
5. **Total Price Consistency**: If `miktar` is provided, validates that `toplamUcret = birimUcret * miktar` (with 0.01 tolerance for rounding)

### Field Validation (Jakarta Validation)
- `@NotNull`: egitimId, birimUcret, toplamUcret
- `@NotBlank`: odemeKaynagi, durum
- `@DecimalMin`: birimUcret, toplamUcret (must be > 0.01)
- `@Digits`: birimUcret, toplamUcret (max 10 integer digits, 2 fraction digits)
- `@Size`: odemeKaynagi (max 200), durum (max 50), operasyon (max 100)
- `@Min`: miktar (min 1)

---

## Activity Logging

All payment operations are automatically logged to the `activity_logs` table:

### Log Actions
- **CREATE**: When a new payment is created
- **UPDATE**: When a payment is updated
- **DELETE**: When a payment is deleted
- **VIEW**: When a payment detail is viewed

### Log Format
```json
{
  "userId": 1,
  "action": "CREATE",
  "entityType": "PAYMENT",
  "entityId": 1,
  "description": "Yeni ödeme oluşturuldu: Eğitim ID=1, Tutar=500.00, Kaynak=Banka Havalesi, Durum=Ödendi",
  "createdAt": "2025-12-09T10:30:00"
}
```

---

## Authorization Matrix

| Endpoint | ADMIN | SORUMLU | EGITMEN | Anonymous |
|----------|-------|---------|---------|-----------|
| GET /odeme | ✅ | ✅ | ✅ | ❌ |
| GET /odeme/{id} | ✅ | ✅ | ✅ | ❌ |
| POST /odeme | ✅ | ✅ | ❌ | ❌ |
| PUT /odeme/{id} | ✅ | ✅ | ❌ | ❌ |
| DELETE /odeme/{id} | ✅ | ❌ | ❌ | ❌ |
| POST /odeme/calculate-total | ✅ | ✅ | ✅ | ❌ |

---

## Error Handling

### Standard Error Response Format
```json
{
  "error": "Error message description"
}
```

### HTTP Status Codes
- `200 OK`: Successful GET/PUT request
- `201 CREATED`: Successful POST request
- `204 NO CONTENT`: Successful DELETE request
- `400 BAD REQUEST`: Validation error or invalid data
- `403 FORBIDDEN`: Insufficient permissions
- `404 NOT FOUND`: Resource not found

---

## Example Usage Scenarios

### Scenario 1: Create a payment for a training
```bash
# Step 1: Login to get JWT token
curl -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@akademi.com", "sifre": "admin123"}'

# Response: { "token": "eyJhbGc..." }

# Step 2: Create payment
curl -X POST "http://localhost:8080/odeme" \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "egitimId": 1,
    "birimUcret": 100.00,
    "toplamUcret": 500.00,
    "odemeKaynagi": "Banka Havalesi",
    "durum": "Ödendi",
    "operasyon": "Havale",
    "sorumluId": 1
  }'
```

### Scenario 2: Filter payments by status and education
```bash
curl -X GET "http://localhost:8080/odeme?durum=Ödendi&egitimId=1&page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Scenario 3: Calculate total price before creating payment
```bash
# Calculate total
curl -X POST "http://localhost:8080/odeme/calculate-total?unitPrice=100.00&quantity=5" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Response: { "unitPrice": 100.00, "quantity": 5, "totalPrice": 500.00 }

# Then create payment with calculated total
curl -X POST "http://localhost:8080/odeme" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ ... "toplamUcret": 500.00 ... }'
```

---

## Swagger/OpenAPI Documentation

Access interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

All endpoints are documented with:
- Request/response schemas
- Validation rules
- Example values
- Authorization requirements

---

## Testing

### Unit Tests
Run unit tests:
```bash
mvn test -Dtest=OdemeServiceTest
```

### Integration Tests
Run integration tests:
```bash
mvn test -Dtest=OdemeControllerIntegrationTest
```

### Manual Testing with H2 Console
1. Start application: `mvn spring-boot:run`
2. Open H2 Console: `http://localhost:8080/h2-console`
3. JDBC URL: `jdbc:h2:file:./data/egitim_takip_dev`
4. Username: `sa`
5. Password: (empty)
6. Query payments: `SELECT * FROM odeme;`

---

## Migration Scripts

### V2: Add soft delete and indexes
```sql
ALTER TABLE odeme ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE NOT NULL;
CREATE INDEX idx_odeme_egitim_id ON odeme (egitim_id);
CREATE INDEX idx_odeme_sorumlu_id ON odeme (sorumlu_id);
CREATE INDEX idx_odeme_is_deleted ON odeme (is_deleted);
```

### V3: Add timestamps
```sql
ALTER TABLE odeme ADD COLUMN created_at TIMESTAMP;
ALTER TABLE odeme ADD COLUMN updated_at TIMESTAMP;
UPDATE odeme SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
UPDATE odeme SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL;
ALTER TABLE odeme ALTER COLUMN created_at SET NOT NULL;
CREATE INDEX idx_odeme_created_at ON odeme (created_at);
```

---

## Notes

1. **Soft Delete**: The `isDeleted` flag enables soft delete functionality. Deleted records are marked as deleted but not removed from the database.

2. **Timestamps**: `createdAt` and `updatedAt` are automatically managed by Hibernate annotations (`@CreationTimestamp`, `@UpdateTimestamp`).

3. **Activity Logging**: All CRUD operations are asynchronously logged to the `activity_logs` table for audit purposes.

4. **Validation**: Both Jakarta Validation (annotations) and custom business logic validation are applied.

5. **Performance**: Database indexes are created on frequently queried columns (egitim_id, sorumlu_id, durum, is_deleted, created_at).

6. **Security**: JWT authentication is required for all endpoints. Role-based access control is enforced.


