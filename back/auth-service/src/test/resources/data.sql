INSERT INTO user_credentials(username, password, authority)
VALUES ('testAdmin', '$2a$10$hQm6XNZi7YNzgSzgvxzofOtkh3j4Rwn8sW/35YSwSSZga/FsgrwSy', 1)
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    authority = VALUES(authority);