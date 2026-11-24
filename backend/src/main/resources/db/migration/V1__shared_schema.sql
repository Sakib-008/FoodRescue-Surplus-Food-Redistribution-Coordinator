-- =====================================================================
-- V1: SHARED SCHEMA  (owner: Member 1 / Team Lead, reviewed by all three)
-- Conventions: snake_case, BIGSERIAL ids, created_at/updated_at everywhere,
-- statuses stored as VARCHAR (mapped to Java enums with EnumType.STRING).
-- Cross-module references are plain foreign keys; Java entities hold ids only.
-- =====================================================================

CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    full_name     VARCHAR(150) NOT NULL,
    phone         VARCHAR(30),
    role          VARCHAR(20)  NOT NULL,
    address       VARCHAR(500),
    lat           DOUBLE PRECISION,
    lng           DOUBLE PRECISION,
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'DONOR', 'RECIPIENT', 'VOLUNTEER'))
);


CREATE TABLE food_categories (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(80) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE donations (
    id                  BIGSERIAL PRIMARY KEY,
    donor_id            BIGINT         NOT NULL REFERENCES users (id),
    category_id         BIGINT         NOT NULL REFERENCES food_categories (id),
    food_name           VARCHAR(150)   NOT NULL,
    description         VARCHAR(2000),
    quantity_total      NUMERIC(12, 2) NOT NULL,
    quantity_available  NUMERIC(12, 2) NOT NULL,
    unit                VARCHAR(20)    NOT NULL,
    expiry_at           TIMESTAMPTZ    NOT NULL,
    pickup_address      VARCHAR(500)   NOT NULL,
    lat                 DOUBLE PRECISION,
    lng                 DOUBLE PRECISION,
    pickup_from         TIMESTAMPTZ    NOT NULL,
    pickup_to           TIMESTAMPTZ    NOT NULL,
    storage_conditions  VARCHAR(500),
    allergens           VARCHAR(500),
    status              VARCHAR(20)    NOT NULL,
    expiry_warning_sent BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_donation_qty_total CHECK (quantity_total > 0),
    CONSTRAINT chk_donation_qty_avail CHECK (quantity_available >= 0 AND quantity_available <= quantity_total),
    CONSTRAINT chk_donation_status CHECK (status IN ('AVAILABLE', 'RESERVED', 'COMPLETED', 'EXPIRED', 'CANCELLED'))
);
CREATE INDEX idx_donations_status_expiry ON donations (status, expiry_at);
CREATE INDEX idx_donations_donor ON donations (donor_id);


CREATE TABLE donation_requests (
    id                 BIGSERIAL PRIMARY KEY,
    donation_id        BIGINT         NOT NULL REFERENCES donations (id),
    recipient_id       BIGINT         NOT NULL REFERENCES users (id),
    quantity_requested NUMERIC(12, 2) NOT NULL,
    status             VARCHAR(20)    NOT NULL,
    note               VARCHAR(500),
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_request_qty CHECK (quantity_requested > 0),
    CONSTRAINT chk_request_status CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED', 'COMPLETED'))
);
CREATE INDEX idx_requests_donation ON donation_requests (donation_id, status);
CREATE INDEX idx_requests_recipient ON donation_requests (recipient_id);

CREATE TABLE pickup_tasks (
    id           BIGSERIAL PRIMARY KEY,
    request_id   BIGINT      NOT NULL UNIQUE REFERENCES donation_requests (id),
    volunteer_id BIGINT REFERENCES users (id),
    status       VARCHAR(20) NOT NULL,
    collected_at TIMESTAMPTZ,
    delivered_at TIMESTAMPTZ,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_pickup_status CHECK (status IN ('OPEN', 'ASSIGNED', 'PICKED_UP', 'DELIVERED', 'CANCELLED'))
);
CREATE INDEX idx_pickups_status ON pickup_tasks (status);
CREATE INDEX idx_pickups_volunteer ON pickup_tasks (volunteer_id);

CREATE TABLE notifications (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES users (id),
    type       VARCHAR(40)  NOT NULL,
    message    VARCHAR(500) NOT NULL,
    read_flag  BOOLEAN      NOT NULL DEFAULT FALSE,
    ref_type   VARCHAR(40),
    ref_id     BIGINT,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_notifications_user ON notifications (user_id, read_flag, created_at DESC);

