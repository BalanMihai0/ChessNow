describe("The main page", () => {
  beforeEach(() => {
    cy.visit("http://localhost:5173");
  });

  describe("Login", () => {
    beforeEach(() => {
      cy.visit("http://localhost:5173");
      cy.get("#navbar").contains("Login").click();
    });

    it("should navigate to Login page from the NavBar", () => {
      cy.url().should("include", "/loginregister");
    });

    it("should display an error message for invalid login", () => {
      cy.get("#loginUsername").type("invalid@email.com");
      cy.get("#loginPassword").type("invalidpassword");

      cy.get("#loginForm").submit();

      cy.get(".Toastify__toast--error").should("exist");
    });
  });

  describe("Register", () => {
    beforeEach(() => {
      cy.visit("http://localhost:5173/loginregister");
    });

    it("should display error message if not all fields have been filled in", () => {
      cy.get("#registerButton").click();
      cy.get(".Toastify__toast--error").should("exist");
    });

    it("should display warning message if passwords do not match"),
      () => {
        cy.get("#registerUsername").type("don_cy");
        cy.get("#registerEmail").type("don@email.com");
        cy.get("#registerPassword").type("Testpass1234");
        cy.get("#registerConfirmPassword").type("DifferentPass1234");

        cy.get("#registerButton").click();
        cy.get("#passwordMatchText").should("include", "Passwords must match!");
      };

    it("should register user successfully", () => {
      cy.get("#registerUsername").type("don_cy");
      cy.get("#registerEmail").type("don@email.com");
      cy.get("#registerPassword").type("Testpass1234");
      cy.get("#registerConfirmPassword").type("Testpass1234");

      cy.get("#registerButton").click();
      cy.get(".Toastify__toast--success").should("exist");
    });
  });

  describe("Social Interactions", () => {
    beforeEach(() => {
      cy.visit("http://localhost:5173/loginRegister");
      cy.get("#loginUsername").type("ProudInShroud");
      cy.get("#loginPassword").type("password");

      cy.get("#loginForm").submit();

      cy.get("#navbar").contains("Social").click();
    });
    it("should navigate to social page", () => {
      cy.url().should("include", "userpage");
    });

    it("should invite player", () => {
      cy.get("#inviteButton").click();
      cy.get(".Toastify__toast--success").should("exist");
    });

    it("should search for a user", () => {
      cy.get("#searchBar").type("exam");
      cy.get("#searchedUsername").should("contain", "exam");
    });
  });

  describe("Profile Interactions", () => {
    beforeEach(() => {
      cy.visit("http://localhost:5173/loginRegister");
      cy.get("#loginUsername").type("ProudInShroud");
      cy.get("#loginPassword").type("password");

      cy.get("#loginForm").submit();

      cy.get("#navbar").contains("Profile").click();
    });

    it("should see the correct username", () => {
      cy.get("#profileUsername").should("have.value", "ProudInShroud");
    });
  });

  describe("Admin interactions", () => {
    beforeEach(() => {
      cy.visit("http://localhost:5173/loginRegister");
    });

    it("should login as admin and see the admin page", () => {
      cy.get("#loginUsername").type("admin");
      cy.get("#loginPassword").type("password");

      cy.get("#loginForm").submit();
    });
  });
});
