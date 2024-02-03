CREATE TABLE Match (
                      matchId INT AUTO_INCREMENT PRIMARY KEY,
                      player1_id INT NOT NULL,
                      player2_id INT NOT NULL,
                      startTimestamp DATETIME NOT NULL,
                      endTimestamp DATETIME,
                      FOREIGN KEY (player1_id) REFERENCES UserEntity(id),
                      FOREIGN KEY (player2_id) REFERENCES UserEntity(id)
    );

CREATE TABLE Move (
                    moveId INT AUTO_INCREMENT PRIMARY KEY,
                    matchId INT NOT NULL,
                    playerId INT NOT NULL,
                    fenString VARCHAR(255) NOT NULL,
                    move VARCHAR(10) NOT NULL,
                    FOREIGN KEY (matchId) REFERENCES Matches(matchId),
                    FOREIGN KEY (playerId) REFERENCES UserEntity(id)
    );

CREATE TABLE MatchHistory (
                           historyId INT AUTO_INCREMENT PRIMARY KEY,
                           userId INT NOT NULL,
                           matchId INT NOT NULL,
                           FOREIGN KEY (userId) REFERENCES UserEntity(id),
                           FOREIGN KEY (matchId) REFERENCES Matches(matchId)
    );