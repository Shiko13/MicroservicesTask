INSERT INTO users (first_name, last_name, username, password, is_active, postfix)
VALUES
    ('John', 'Doe', 'john.doe', '$2a$12$cKUhxdXKWJ09uITaB2PstOZeEyos.Upf1K3FxiJTY3bT3Pp9Nz2s2', true, 0),
    ('Jane', 'Smith', 'jane.smith', '$2a$12$jpaI5rnz8esEDwkAZaBFeuv2A7Kaajs6zt5GpSvdvHpXtVPSZvMjS', true, 0),
    ('Alice', 'Johnson', 'alice.johnson', '$2a$12$48fdzJn/w2VHKQzW7VDH4O6/MzCIJZPp4Lxrk/hv9EAiom47F49bq', true, 0),
    ('Bob', 'Brown', 'bob.brown', '$2a$12$SwSZOB.jQlp.c1U2170UT.ooaLzNqIomJeIaZP1klpNWMPMp3sRFu', true, 0),
    ('Eva', 'Davis', 'eva.davis', '$2a$12$sMh7LDKye.uDySsoeldvF.y7RfLamVOM.UehRX.XXr6baTj8d9qyq', true, 0),
    ('Michael', 'Wilson', 'michael.wilson', '$2a$12$2hTbuJFy1QBlYF27k/qIZOzOi9Wd.2hsu6/BtLysYMqn/hIGWRFSm', true, 0),
    ('Olivia', 'Lee', 'olivia.lee', '$2a$12$ObSwImMkHy5tMsrTcEY67.su.QxND8X6fDtvkonRTgWj.aqIol2nG', true, 0),
    ('Daniel', 'Taylor', 'daniel.taylor', '$2a$12$drkRdL16uD6Ic/XzIdhqFuG4/womevNb0ZhYEzC2g.msOy7Ocy9xe', true, 0),
    ('Sophia', 'Moore', 'sophia.moore', '$2a$12$6QKdgAMXsYrFECQWFsIqPukGTPgBInkZZ4MU9Peo58aDd26bzZ5O6', true, 0),
    ('James', 'Johnson', 'james.johnson', '$2a$12$/cvtBYXqNFJgABmF2y5uB.Fn0B0xx08Xq2AeC6DYNZi.5M6VFxkde', true, 0),
    ('Emily', 'White', 'emily.white', '$2a$12$Ipy.FJ2LfXOqEyc09x8O/e.AZHguMARD9rntcHzT9wfRZRS7nBlC2', true, 0),
    ('William', 'Anderson', 'william.anderson', '$2a$12$4pG1au/CbjQdZWwUerceqe5YOsrkW6GLR4xY355G7OYiuSczwJJ6K', true, 0),
    ('Charlotte', 'Martin', 'charlotte.martin', '$2a$12$fX3KkB/JqCG2jFjJH9tc2ugnh..gzD2nJeL/wocHZvjeC7vN0Y2xm', true, 0),
    ('Benjamin', 'Thompson', 'benjamin.thompson', '$2a$12$8M5HvvIwKfS5kbSxYT7pp.YpyPWOKZIphei3ZnsI.mXGj9QDXdm/O', true, 0),
    ('Amelia', 'Harris', 'amelia.harris', '$2a$12$eLFj1Hor3RTk7oI8p5nlvOuTZ6kp8Qs7/DPTBL5vJqDVxUQTSE8ru', true, 0),
    ('Henry', 'Nelson', 'henry.nelson', '$2a$12$VIoo.s8vmsc28qDlFkaAWu0gYOIhb0Mu4a6SPWjjA6MAl2hAuKyi6', true, 0),
    ('Sofia', 'King', 'sofia.king', '$2a$12$FxS4GJc1r9vUa2SgnbPT5OmPt8470sc6B7YgOMmeMHiEG8VkI6MDG', true, 0),
    ('Alexander', 'Hall', 'alexander.hall', '$2a$12$d64KojQgLPWykhUDy4D0u.zyKv4WP/6OwaEZ2aMnxbXcq.sTEWl7a', true, 0),
    ('Mia', 'Wright', 'mia.wright', '$2a$12$7s3k3ouIuikzfeK0zZ7jQOf2Oa5/CHDEn6rjvKdizNTjmRduUrSo2', true, 0),
    ('Joseph', 'Adams', 'joseph.adams', '$2a$12$Ck4YrmqCNvPf42PBlv9/VeV.00hAC.sqiHelaMwsSLlYaXQ67.9wa', true, 0);

INSERT INTO training_types (name) VALUES
                                      ('Strength Training'),
                                      ('Cardiovascular Exercise'),
                                      ('Yoga'),
                                      ('CrossFit'),
                                      ('Pilates'),
                                      ('Martial Arts'),
                                      ('Zumba'),
                                      ('Spinning'),
                                      ('Swimming'),
                                      ('Plyometrics');

INSERT INTO trainees (date_of_birth, address, id) VALUES
    ('1995-03-15', '123 Main St', 1),
    ('1992-08-20', '456 Elm St', 2),
    ('1990-04-10', '789 Oak St', 3),
    ('1998-12-05', '555 Pine St', 4),
    ('1994-07-28', '777 Birch St', 5),
    ('1997-09-14', '888 Cedar St', 6),
    ('1999-06-09', '999 Redwood St', 7),
    ('1993-02-25', '111 Maple St', 8),
    ('1996-11-03', '222 Willow St', 9),
    ('1991-01-01', '333 Birch St', 10);

INSERT INTO trainers (specialization, id) VALUES
        (1, 11),
        (2, 12),
        (3, 13),
        (4, 14),
        (5, 15),
        (6, 16),
        (7, 17),
        (8, 18),
        (9, 19),
        (10, 20);

INSERT INTO trainings (trainee_id, trainer_id, name, type_id, date, duration)
VALUES
    (1, 11, 'Training 1', 1, '2023-11-01', 60),
    (2, 12, 'Training 2', 2, '2023-11-02', 45),
    (3, 13, 'Training 3', 3, '2023-11-03', 90),
    (4, 14, 'Training 4', 4, '2023-11-04', 75),
    (5, 15, 'Training 5', 5, '2023-11-05', 60),
    (6, 16, 'Training 6', 6, '2023-11-06', 45),
    (7, 17, 'Training 7', 7, '2023-11-07', 90),
    (8, 18, 'Training 8', 8, '2023-11-08', 75),
    (9, 19, 'Training 9', 9, '2023-11-09', 60),
    (10, 20, 'Training 10', 10, '2023-11-10', 45);

INSERT INTO trainee_trainer (trainee_id, trainer_id) VALUES
      (1, 11),
      (1, 12),
      (2, 11),
      (3, 12),
      (4, 20),
      (5, 19),
      (4, 17),
      (6, 18),
      (7, 16),
      (8, 15),
      (9, 13),
      (10, 14),
      (1, 15),
      (2, 17),
      (5, 18),
      (3, 15),
      (3, 18)
;