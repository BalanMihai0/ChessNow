## ChessNow Application

This is a project that was heavily inspired by the website <link>chess.com</link>. It was built using a Java SpringBoot backend and a ReactJs frontend.

### Application Features

- **Account Control and Security:** Managed via JWT tokens and authorization. Social interactions such as inviting people to a match and chatting are facilitated through Websockets.
  
- **Chess Game Validation:** Ensures the validity of all interactions (moves, checks, castling, promotions, pinned pieces, etc.). Prediction arrows are drawn by using Right-Click. Offers playable chess against StockFish AI, board controls (e.g., resign), match history, user match statistics, profile and details change, and a rating system. It includes an admin interface used for adding accounts or for banning accounts.

### Technical and Architectural Choices

- **Backend Architecture:** The backend runs on Java SpringBoot following a layered architecture, abiding by the principles of OOP as well as the SOLID principles. It is modeled following the dependency inversion design pattern, making use of interfaces for cross-layer communication. Data persistence is achieved by making use of the ORM JPA Repository and using EntityFramework for the MySQL database. Optimizations have been made by using custom queries for aggregated data.

- **Frontend:** The frontend runs on ReactJs and uses Axios for making backend connections. API endpoints are secured by the CORS configuration as well as the role-based backend authorization. 

- More details about this can be found in the Software Architecture Document (SAD) located in the Documentation folder.

### Software Quality

- **Testing:** The logic layer is covered in unit tests, and the controller layer in integration tests. The coverage is checked via Sonarqube, which also checks for bugs and bad coding practices. The frontend is also covered by Cypress end-to-end tests.

- **Performance Testing:** The speed of the software was tested with Apache JMeter for the backend and Google Lighthouse for the frontend. A report on all of this can be found in the Documentation folder, as well as screenshots of the Sonarqube and the end-to-end test results.

### CI/CD

- **Continuous Integration/Continuous Deployment (CI/CD):** Continuous integration was done via a Gitlab Pipeline. The gitlab-ci.yml file with all the used commands can be found in the folder ChessNow_Source. The pipeline was testing the application, running a Sonarqube quality gate, and then using a docker-compose file to deploy the backend and the database as Docker images. The quality gate was set at 0 bugs, 0 code smells, 0 security issues, and over 80% code coverage.

- **Front-End Deployment:** Since the pipeline was running on the backend, the frontend would require manual deployment and port reconfiguration, as well as using a different compiler.

### Documentation

- **Project Plan:** The project plan was the initial idea of development, which has been changed over time. 

- **AGILE Workflow:** Jira was used for monitoring self-made User Stories and Sprints, simulating an AGILE work environment. User stories were built with acceptance criteria and story point estimates
  
- **Performance Report and SAD:** The performance reports and the SAD have already been introduced in previous paragraphs.

- **OWASP Security Report:** The OWASP Security Report showcases how the application handles the 10 security risks, discusses future ideas for improving security and, where is the case, discusses why a certain security risk was not addressed

- **Diagrams:** C1-C4 diagrams for relevant flows can be found in the Diagrams folder, as well as inside the SAD. Diagram of the pipeline can be found in the SAD as well.
