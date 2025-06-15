/* ---------- USERS ---------- */
INSERT INTO users (id, username, email, password, role, join_date)
VALUES
    (nextval('users_id_seq'),            -- ↓   видно будет в UI
     'tester1',
     'tester1@example.com',
     '$2a$10$K/5n5kIziHmnA7x6Wj.MyuYwsSgZJZ4USkRxmqhvpmNcrqZiau3G6', -- bcrypt("password")
     'USER',
     now()),

    (nextval('users_id_seq'),
     'tester2',
     'tester2@example.com',
     '$2a$10$NnU7FqZjwVzKV1/Fh.5EjeruRjNDDm07MGWMPHUpnBV.JM9cHjTfC', -- bcrypt("password2")
     'USER',
     now());

/* ---------- EVENT (голосование) ---------- */
INSERT INTO events (id,
                    title,
                    description,
                    start_date,
                    end_date,
                    status)
VALUES
    (nextval('events_id_seq'),
     'Выбор председателя совета',
     'Тестовое голосование для проверки блокчейн-цепочки',
     now(),                        -- старт = сейчас
     now() + interval '7 days',    -- закончится через неделю
     'active');                    -- или другой статус, который ждёт сервис


-- 📄 resources/db/migration/V2__seed_test_voting_with_options.sql
-- 1. само событие
INSERT INTO events(id, title, description,
                   start_date, end_date, status)
VALUES (100, 'Тестовое голосование для проверки блокчейн-цепочки',
        'Выберите одного из трёх кандидатов.',
        NOW(),                       -- начало сейчас
        NOW() + INTERVAL '7 days',   -- закончится через неделю
        'active');                   -- важно!

-- 2. кандидаты (простейшая таблица options)
INSERT INTO option(id, event_id, text, votes)
VALUES (1001, 100, 'Алиса', 10),
       (1002, 100, 'Боб', 0),
       (1003, 100, 'Чарли', 1);

SELECT id,event_id,text FROM option WHERE event_id = 421;

------------------


-- создаём событие
INSERT INTO events(id, title, description, start_date, end_date, status)
VALUES (421, 'Тестовое голосование', 'Выберите лучшего кандидата',
        now(), now() + interval '7 day', 'active');

UPDATE events
SET    status = 'active'
WHERE  id = 421;


-- получаем id вставленной строки (допустим, это 1)
-- добавляем три варианта
INSERT INTO option(id, event_id, text, votes) VALUES
                                               (4, 421, 'Иван Петров', 2),
                                              (5, 421, 'Мария Сидорова', 5),
                                              (6, 421, 'Алексей Иванов', 0);


SELECT id, title, end_date, status FROM events;

SELECT * FROM users;

INSERT INTO users (username, email, password, role, join_date)
VALUES (
           'admin',
           'admin@example.com',
           '123456', -- пароль: admin123
           'ADMIN',
           now()
       );

UPDATE users
SET role = 'ADMIN'
WHERE email = 'user1@gmail.com';

select * from votes;






INSERT INTO events(id, title, description, start_date, end_date, status)
VALUES (9123, 'Выбор нового слогана для компании OpenVoice
', 'Выберите лучший слоган',
        now(), now() + interval '7 day', 'active');

INSERT INTO option(id, event_id, text, votes) VALUES
                                                  (123, 9123, 'Каждый голос имеет значение', 0),
                                                  (513, 9123, 'Твое мнение — наша основа', 0),
                                                  (312, 9123, 'Прозрачные решения от настоящих', 1);

update option set votes = 1 where id = 513