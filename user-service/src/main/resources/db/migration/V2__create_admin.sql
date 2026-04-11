INSERT INTO users (username, email, password_hash, enabled, created_at)
VALUES (
    'admin',
    'admin@flight.com',
    '$2a$10$9OrfVb6K8P1zOjI1ZNjDeu7qUVf7YKkKZl4vA3nEJrVijUHVzPfNS', -- password = admin123
    TRUE,
    CURRENT_TIMESTAMP
);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@flight.com'
  AND r.name = 'ROLE_ADMIN';
