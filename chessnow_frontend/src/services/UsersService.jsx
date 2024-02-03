import axios from "axios";
import LOCALHOST from "./URL";
import TokenService from "./TokenService";

function getAllUsers() {
  const token = TokenService.getAccessToken();

  const config = {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };

  return axios
    .get(LOCALHOST + "/users", config)
    .then((response) => response.data);
}

function sendRegisterUser(body) {
  return axios
    .post(`${LOCALHOST}/users`, body)
    .then((response) => response.data);
}

function loginUser(loginRequest) {
  return axios
    .post(`${LOCALHOST}/users/login`, loginRequest)
    .then((response) => response.data);
}

function updateUser(id, updatedUserData) {
  const token = TokenService.getAccessToken();
  console.log("called update" + id + " " + JSON.stringify(updatedUserData));
  const config = {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };

  return axios
    .put(`${LOCALHOST}/users/${id}`, updatedUserData, config)
    .then((response) => response.data);
}

function getSortedUsers(ascending) {
  const token = TokenService.getAccessToken();

  const config = {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };
  return axios
    .get(`${LOCALHOST}/users/sortRating/${ascending}`, config)
    .then((response) => response.data);
}

function getAllMatches(username) {
  const token = TokenService.getAccessToken();

  const config = {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };

  return axios
    .get(LOCALHOST + "/users/totalMatches/" + username, config)
    .then((response) => response.data);
}

function getAllMatchesWon(username) {
  const token = TokenService.getAccessToken();

  const config = {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };

  return axios
    .get(LOCALHOST + "/users/totalMatchesWon/" + username, config)
    .then((response) => response.data);
}

function getUsersSocial() {
  const token = TokenService.getAccessToken();

  const config = {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };

  return axios
    .get(LOCALHOST + "/users/social", config)
    .then((response) => response.data);
}

export default {
  getAllUsers,
  sendRegisterUser,
  loginUser,
  updateUser,
  getSortedUsers,
  getAllMatches,
  getAllMatchesWon,
  getUsersSocial,
};
