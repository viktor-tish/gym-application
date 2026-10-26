INSERT INTO users (id, first_name, last_name, username, password, is_active, created_at, updated_at) VALUES
  ('b1b2c3d4-0002-0000-0000-000000000001', 'John', 'Doe', 'john.doe', '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8=', true, NOW(), NOW()),
  ('b1b2c3d4-0002-0000-0000-000000000002', 'Alice', 'Wonderland', 'alice.wonderland', '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8=', true, NOW(), NOW()),
  ('b1b2c3d4-0002-0000-0000-000000000003', 'Bob', 'Marley', 'bob.marley', '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8=', true, NOW(), NOW()),
  ('b1b2c3d4-0002-0000-0000-000000000004', 'Inactive', 'Trainee', 'inactive.trainee', '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8=', false, NOW(), NOW());

INSERT INTO users (id, first_name, last_name, username, password, is_active, created_at, updated_at) VALUES
  ('b1b2c3d4-0002-0000-0000-000000000005', 'Jane', 'Smith', 'jane.smith', '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8=', true, NOW(), NOW()),
  ('b1b2c3d4-0002-0000-0000-000000000006', 'Michael', 'Scott', 'michael.scott', '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8=', true, NOW(), NOW()),
  ('b1b2c3d4-0002-0000-0000-000000000007', 'Serena', 'Williams', 'serena.williams', '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8=', true, NOW(), NOW()),
  ('b1b2c3d4-0002-0000-0000-000000000008', 'Inactive', 'Trainer', 'inactive.trainer', '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8=', false, NOW(), NOW());

INSERT INTO trainees (id, user_id, date_of_birth, address, created_at, updated_at) VALUES
  ('c1b2c3d4-0003-0000-0000-000000000001', 'b1b2c3d4-0002-0000-0000-000000000001', '1995-05-15', '123 Main St, New York', NOW(), NOW()),
  ('c1b2c3d4-0003-0000-0000-000000000002', 'b1b2c3d4-0002-0000-0000-000000000002', '1998-11-20', '456 Elm St, Boston', NOW(), NOW()),
  ('c1b2c3d4-0003-0000-0000-000000000003', 'b1b2c3d4-0002-0000-0000-000000000003', '1990-02-06', '789 Oak Ave, Austin', NOW(), NOW()),
  ('c1b2c3d4-0003-0000-0000-000000000004', 'b1b2c3d4-0002-0000-0000-000000000004', '2000-01-01', '321 Pine Rd, Seattle', NOW(), NOW());

INSERT INTO trainers (id, user_id, specialization_id, created_at, updated_at) VALUES
  ('d1b2c3d4-0004-0000-0000-000000000001', 'b1b2c3d4-0002-0000-0000-000000000005', 'a1b2c3d4-0001-0000-0000-000000000001', NOW(), NOW()),
  ('d1b2c3d4-0004-0000-0000-000000000002', 'b1b2c3d4-0002-0000-0000-000000000006', 'a1b2c3d4-0001-0000-0000-000000000002', NOW(), NOW()),
  ('d1b2c3d4-0004-0000-0000-000000000003', 'b1b2c3d4-0002-0000-0000-000000000007', 'a1b2c3d4-0001-0000-0000-000000000005', NOW(), NOW()),
  ('d1b2c3d4-0004-0000-0000-000000000004', 'b1b2c3d4-0002-0000-0000-000000000008', 'a1b2c3d4-0001-0000-0000-000000000003', NOW(), NOW());

INSERT INTO trainee_trainers (trainee_id, trainer_id) VALUES
  ('c1b2c3d4-0003-0000-0000-000000000001', 'd1b2c3d4-0004-0000-0000-000000000001'), -- John Doe -> Jane Smith (Fitness)
  ('c1b2c3d4-0003-0000-0000-000000000001', 'd1b2c3d4-0004-0000-0000-000000000002'), -- John Doe -> Michael Scott (Yoga)
  ('c1b2c3d4-0003-0000-0000-000000000002', 'd1b2c3d4-0004-0000-0000-000000000001'), -- Alice -> Jane Smith (Fitness)
  ('c1b2c3d4-0003-0000-0000-000000000002', 'd1b2c3d4-0004-0000-0000-000000000003'); -- Alice -> Serena Williams (Resistance)

INSERT INTO trainings (id, trainee_id, trainer_id, type_id, name, date, duration, created_at, updated_at) VALUES
  ('e1b2c3d4-0005-0000-0000-000000000001', 'c1b2c3d4-0003-0000-0000-000000000001', 'd1b2c3d4-0004-0000-0000-000000000001', 'a1b2c3d4-0001-0000-0000-000000000001', 'Morning Fitness Cardio', '2026-10-01', 60, NOW(), NOW()),
  ('e1b2c3d4-0005-0000-0000-000000000002', 'c1b2c3d4-0003-0000-0000-000000000001', 'd1b2c3d4-0004-0000-0000-000000000002', 'a1b2c3d4-0001-0000-0000-000000000002', 'Evening Relaxation Yoga', '2026-10-15', 45, NOW(), NOW()),
  ('e1b2c3d4-0005-0000-0000-000000000003', 'c1b2c3d4-0003-0000-0000-000000000002', 'd1b2c3d4-0004-0000-0000-000000000001', 'a1b2c3d4-0001-0000-0000-000000000001', 'HIIT Circuit Training', '2026-10-10', 90, NOW(), NOW()),
  ('e1b2c3d4-0005-0000-0000-000000000004', 'c1b2c3d4-0003-0000-0000-000000000002', 'd1b2c3d4-0004-0000-0000-000000000003', 'a1b2c3d4-0001-0000-0000-000000000005', 'Powerlifting Resistance Session', '2026-10-20', 75, NOW(), NOW());
