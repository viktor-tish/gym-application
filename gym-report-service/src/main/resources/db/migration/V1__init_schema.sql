CREATE TABLE trainer_profiles
(
    id         UUID PRIMARY KEY,
    username   VARCHAR(255)             NOT NULL UNIQUE,
    first_name VARCHAR(255)             NOT NULL,
    last_name  VARCHAR(255)             NOT NULL,
    is_active  BOOLEAN                  NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE monthly_workloads
(
    id                        UUID PRIMARY KEY,
    trainer_profile_id        UUID                     NOT NULL,
    training_year             INT                      NOT NULL,
    training_month            INT                      NOT NULL,
    training_summary_duration INT                      NOT NULL,
    created_at                TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at                TIMESTAMP WITH TIME ZONE NOT NULL,

    FOREIGN KEY (trainer_profile_id) REFERENCES trainer_profiles (id) ON DELETE CASCADE,

    CONSTRAINT uk_monthly_workload_trainer_profile_year_month UNIQUE (trainer_profile_id, training_year, training_month),
    CONSTRAINT ck_monthly_workload_month CHECK (training_month BETWEEN 1 AND 12),
    CONSTRAINT ck_monthly_workload_summary_duration CHECK (training_summary_duration >= 0)
);
