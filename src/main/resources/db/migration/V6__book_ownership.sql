ALTER TABLE books
    ADD COLUMN owner_username VARCHAR(60);

UPDATE books
SET owner_username = 'admin';

UPDATE books
SET owner_username = 'user'
WHERE id = 1;

ALTER TABLE books
    ALTER COLUMN owner_username SET NOT NULL;

ALTER TABLE books
    ADD CONSTRAINT fk_books_owner
        FOREIGN KEY (owner_username)
            REFERENCES app_user(username);