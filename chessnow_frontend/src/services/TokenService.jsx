import { jwtDecode } from "jwt-decode";

class TokenService {
  static setAccessToken(accessToken) {
    localStorage.setItem("accessToken", accessToken);
  }

  static clearAccessToken() {
    localStorage.removeItem("accessToken");
  }

  static getAccessToken() {
    return localStorage.getItem("accessToken");
  }

  static handleLoginSuccess(accessToken) {
    //god giveth
    this.setAccessToken(accessToken);
  }

  static handleLogOut() {
    //god taketh
    this.clearAccessToken();
    localStorage.removeItem("matchId");
  }

  static getDecoded(accessToken) {
    try {
      const decodedToken = jwtDecode(accessToken);
      const subject = decodedToken.sub;
      const role = decodedToken.role;
      const username = decodedToken.username;
      const rating = decodedToken.rating;
      const email = decodedToken.email;

      return {
        subject,
        role,
        username,
        rating,
        email,
      };
    } catch (error) {
      return null;
    }
  }

  static updateAccessToken(updatedTokenData) {
    const currentAccessToken = this.getAccessToken();

    if (currentAccessToken) {
      try {
        const decodedToken = jwtDecode(currentAccessToken);
        const updatedToken = { ...decodedToken, ...updatedTokenData };

        const updatedAccessToken = jwtEncode(updatedToken);

        this.setAccessToken(updatedAccessToken);
      } catch (error) {
        console.error("Error updating access token:", error);
      }
    }
  }
}

export default TokenService;
