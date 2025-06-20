CREATE TABLE saga_instance (
                               id CHAR(36) NOT NULL PRIMARY KEY,
                               saga_type VARCHAR(100) NOT NULL,
                               status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED', 'COMPENSATED') NOT NULL,
                               context JSON,
                               created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                               updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE saga_step (
                           id CHAR(36) NOT NULL PRIMARY KEY,
                           saga_id CHAR(36) NOT NULL,
                           step_name VARCHAR(100) NOT NULL,
                           step_type ENUM('FORWARD', 'COMPENSATION') NOT NULL DEFAULT 'FORWARD',
                           status ENUM('PENDING', 'IN_PROGRESS', 'DONE', 'FAILED', 'COMPENSATED') NOT NULL,
                           execution_order INT NOT NULL,
                           command JSON,
                           event_response JSON,
                           compensates_step_id CHAR(36),
                           started_at DATETIME,
                           ended_at DATETIME,
                           FOREIGN KEY (saga_id) REFERENCES saga_instance(id) ON DELETE CASCADE,
                           FOREIGN KEY (compensates_step_id) REFERENCES saga_step(id) ON DELETE SET NULL,
                           INDEX idx_saga_id (saga_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE outbox_event (
                              id CHAR(36) NOT NULL PRIMARY KEY,
                              aggregate_type VARCHAR(100) NOT NULL,
                              aggregate_id VARCHAR(100) NOT NULL,
                              event_type VARCHAR(100) NOT NULL,
                              payload JSON NOT NULL,
                              status ENUM('PENDING', 'PUBLISHED', 'FAILED') NOT NULL DEFAULT 'PENDING',
                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                              published_at DATETIME,
                              INDEX idx_outbox_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;