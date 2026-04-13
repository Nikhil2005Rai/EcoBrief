import axiosClient from './axiosClient';

export const signup = async (payload) => {
  const { data } = await axiosClient.post('/auth/signup', payload);
  return data;
};

export const login = async (payload) => {
  const { data } = await axiosClient.post('/auth/login', payload);
  return data;
};
