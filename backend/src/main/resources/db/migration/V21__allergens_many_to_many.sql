-- =====================================================================
-- V21 (owner: Member 2): allergen catalogue + many-to-many link to donations.
-- The free-text column becomes "allergen_notes" for anything not in the list.
-- =====================================================================
ALTER TABLE donations RENAME COLUMN allergens TO allergen_notes;

CREATE TABLE allergens (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(60) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- M:M join table: one donation can contain many allergens, one allergen appears in many donations
CREATE TABLE donation_allergens (
    donation_id BIGINT NOT NULL REFERENCES donations (id) ON DELETE CASCADE,
    allergen_id BIGINT NOT NULL REFERENCES allergens (id),
    PRIMARY KEY (donation_id, allergen_id)
);
CREATE INDEX idx_donation_allergens_allergen ON donation_allergens (allergen_id);

-- the 14 major food allergens (EU Regulation 1169/2011, Annex II)
INSERT INTO allergens (name) VALUES
  ('Gluten'), ('Crustaceans'), ('Eggs'), ('Fish'), ('Peanuts'), ('Soy'), ('Milk'),
  ('Tree nuts'), ('Celery'), ('Mustard'), ('Sesame'), ('Sulphites'), ('Lupin'), ('Molluscs');
