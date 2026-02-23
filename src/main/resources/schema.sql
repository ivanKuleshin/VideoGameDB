CREATE TABLE VIDEOGAME
(
    ID           INT          PRIMARY KEY,
    NAME         VARCHAR(100) DEFAULT '',
    RELEASED_ON  DATE,
    REVIEW_SCORE INT,
    CATEGORY     VARCHAR(100),
    RATING       VARCHAR(100)
);

INSERT INTO VIDEOGAME VALUES (1,  'Resident Evil 4',                    '2005-10-01', 85, 'Shooter',  'Universal');
INSERT INTO VIDEOGAME VALUES (2,  'Gran Turismo 3',                     '2001-03-10', 91, 'Driving',  'Universal');
INSERT INTO VIDEOGAME VALUES (3,  'Tetris',                             '1984-06-25', 88, 'Puzzle',   'Universal');
INSERT INTO VIDEOGAME VALUES (4,  'Super Mario 64',                     '1996-10-20', 90, 'Platform', 'Universal');
INSERT INTO VIDEOGAME VALUES (5,  'The Legend of Zelda: Ocarina of Time','1998-12-10', 93, 'Adventure','PG-13');
INSERT INTO VIDEOGAME VALUES (6,  'Doom',                               '1993-02-18', 81, 'Shooter',  'Mature');
INSERT INTO VIDEOGAME VALUES (7,  'Minecraft',                          '2011-12-05', 77, 'Puzzle',   'Universal');
INSERT INTO VIDEOGAME VALUES (8,  'SimCity 2000',                       '1994-09-11', 88, 'Strategy', 'Universal');
INSERT INTO VIDEOGAME VALUES (9,  'Final Fantasy VII',                  '1997-08-20', 97, 'RPG',      'PG-13');
INSERT INTO VIDEOGAME VALUES (10, 'Grand Theft Auto III',               '2001-04-23', 90, 'Driving',  'Mature');
