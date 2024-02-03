import React, { useEffect, useState } from "react";
import axios from "axios";
import LOCALHOST from "./URL";
import TokenService from "./TokenService";

export function sendChessMove(moveData) {
  const token = TokenService.getAccessToken();
  const config = {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };
  return axios
    .post(`${LOCALHOST}/chess/move`, moveData, config)
    .then((response) => response.data);
}

export function startmatch(matchData) {
  const token = TokenService.getAccessToken();
  const config = {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };
  return axios
    .post(`${LOCALHOST}/chess/startmatch`, matchData, config)
    .then((response) => response.data);
}
export function getMatchHistory(userId) {
  const token = TokenService.getAccessToken();
  const config = {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };
  return axios
    .get(`${LOCALHOST}/chess/matchHistory/${userId}`, config)
    .then((response) => response.data);
}

export function getLastPosition(matchId) {
  const token = TokenService.getAccessToken();
  const config = {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };
  return axios
    .get(`${LOCALHOST}/chess/lastMatchPosition/${matchId}`, config)
    .then((response) => response.data);
}

export function endMatch(endData) {
  const token = TokenService.getAccessToken();
  const config = {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };
  return axios
    .post(`${LOCALHOST}/chess/endMatch`, endData, config)
    .then((response) => response.data);
}
