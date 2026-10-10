CREATE TABLE category (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL UNIQUE,
    slug    VARCHAR(120) NOT NULL UNIQUE,
    active  BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE product (
    id          BIGSERIAL PRIMARY KEY,
    category_id BIGINT       NOT NULL REFERENCES category(id),
    name        VARCHAR(200) NOT NULL,
    slug        VARCHAR(220) NOT NULL UNIQUE,
    description TEXT,
    fabric      VARCHAR(100),
    gsm         INTEGER CHECK (gsm > 0),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE product_variant (
    id         BIGSERIAL PRIMARY KEY,
    product_id BIGINT        NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    size       VARCHAR(10)   NOT NULL,
    color      VARCHAR(40)   NOT NULL,
    sku        VARCHAR(60)   NOT NULL UNIQUE,
    price      NUMERIC(10,2) NOT NULL CHECK (price >= 0),
    stock      INTEGER       NOT NULL DEFAULT 0 CHECK (stock >= 0),
    UNIQUE (product_id, size, color)
);

CREATE INDEX idx_product_category ON product(category_id);