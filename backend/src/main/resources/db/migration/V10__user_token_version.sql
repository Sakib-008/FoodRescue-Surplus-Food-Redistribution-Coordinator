-- =====================================================================
-- V10 (owner: Member 1): server-side logout.
-- Every JWT carries the user's token_version ("ver" claim). Logging out increments it,
-- so all tokens issued before the logout are rejected by JwtAuthFilter immediately.
-- =====================================================================
ALTER TABLE users ADD COLUMN token_version INTEGER NOT NULL DEFAULT 0;
