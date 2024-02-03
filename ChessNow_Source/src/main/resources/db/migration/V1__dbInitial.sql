CREATE TABLE UserEntity (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            email VARCHAR(255) NOT NULL UNIQUE,
                            username VARCHAR(255) NOT NULL UNIQUE,
                            password VARCHAR(255) NOT NULL,
                            rating INT NOT NULL,
                            isAdmin BOOLEAN NOT NULL
);

CREATE TABLE UserFriendship (
                                friendshipId INT AUTO_INCREMENT PRIMARY KEY,
                                user1_id INT NOT NULL,
                                user2_id INT NOT NULL,
                                FOREIGN KEY (user1_id) REFERENCES UserEntity(id),
                                FOREIGN KEY (user2_id) REFERENCES UserEntity(id)
);
