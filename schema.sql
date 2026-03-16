-- ==========================================================
-- Investment Tracker — Initial Database Schema (MySQL 8.0)
-- ==========================================================

-- 1. Create the Database
CREATE DATABASE IF NOT EXISTS investment_tracker_db
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE investment_tracker_db;

-- ==========================================================
-- 2. Core Security & Identity Core
-- ==========================================================

-- Tenants (Organizations)
CREATE TABLE IF NOT EXISTS tenants (
    id VARCHAR(36) PRIMARY KEY, -- Uses UUIDs
    name VARCHAR(100) NOT NULL UNIQUE,
    subscription_plan VARCHAR(20) NOT NULL DEFAULT 'FREE', -- FREE, PRO, ENTERPRISE
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Users (Employees/Clients scoped to a Tenant)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER', -- ROLE_ADMIN, ROLE_USER
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT uk_users_email_tenant UNIQUE (email, tenant_id) -- Unique email per organization
);
-- Optimize user lookups by tenant
CREATE INDEX idx_users_tenant ON users(tenant_id);

-- ==========================================================
-- 3. Business Domain (Investments)
-- ==========================================================

-- Investments (Stocks, Crypto, Mutual Funds)
CREATE TABLE IF NOT EXISTS investments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    user_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    type VARCHAR(20) NOT NULL, -- STOCK, MUTUAL_FUND, CRYPTO, FIXED_DEPOSIT
    symbol VARCHAR(20),
    quantity DECIMAL(18,8) NOT NULL, -- Precision for fractional Crypto
    purchase_price DECIMAL(18,4) NOT NULL, -- Precision for Fiat
    current_price DECIMAL(18,4) NOT NULL,
    purchase_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL, -- Soft Deletion Timestamp
    CONSTRAINT fk_investments_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_investments_user FOREIGN KEY (user_id) REFERENCES users(id)
);
-- Multi-Tenant Indexing Strategy
CREATE INDEX idx_inv_tenant ON investments(tenant_id);
CREATE INDEX idx_inv_user ON investments(user_id, tenant_id);
CREATE INDEX idx_inv_type ON investments(type, tenant_id);
CREATE INDEX idx_inv_deleted ON investments(deleted_at); -- heavily queried by Hibernate @Where clause

-- Transactions (Ledger for buys/sells)
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    investment_id BIGINT NOT NULL,
    transaction_type VARCHAR(10) NOT NULL, -- BUY, SELL, DIVIDEND
    quantity DECIMAL(18,8) NOT NULL,
    price_per_unit DECIMAL(18,4) NOT NULL,
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_transactions_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_transactions_investment FOREIGN KEY (investment_id) REFERENCES investments(id)
);
CREATE INDEX idx_txn_inv ON transactions(investment_id, tenant_id);

-- ==========================================================
-- 4. Audit & Compliance
-- ==========================================================

-- Audit Logs (Append-Only system tracking)
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    user_id BIGINT,
    action VARCHAR(50) NOT NULL, -- e.g., 'CREATE_INVESTMENT', 'LOGIN'
    entity_name VARCHAR(50),
    entity_id VARCHAR(50),
    details TEXT,
    ip_address VARCHAR(45), -- Supports IPv6 lengths
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);
CREATE INDEX idx_audit_tenant_action ON audit_logs(tenant_id, action);
