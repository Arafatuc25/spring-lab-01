CREATE TABLE app_user (
                          id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(60) NOT NULL UNIQUE,
                          password_hash VARCHAR(100) NOT NULL,
                          enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE user_role (
                           user_id BIGINT NOT NULL REFERENCES app_user(id),
                           role VARCHAR(20) NOT NULL,
                           PRIMARY KEY (user_id, role)
);

INSERT INTO app_user (username, password_hash)
VALUES
    (
        'user',
        '$2a$10$F51MZum5PZ8zPSA6aYUfKeTQgFfebdju3YWpFCpy.7UG8rBJZb.wm'
    ),
    (
        'admin',
        '$2a$10$OeEyRBatFzj3gZRn0/R/W.4gydBCn0fYDDjYC4/eXzc.jGIZaoKl2'
    );

INSERT INTO user_role (user_id, role)
SELECT id, 'USER'
FROM app_user;

INSERT INTO user_role (user_id, role)
SELECT id, 'ADMIN'
FROM app_user
WHERE username = 'admin';