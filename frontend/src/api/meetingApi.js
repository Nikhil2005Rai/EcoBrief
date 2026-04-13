import axiosClient from './axiosClient';

export const getMeetings = async () => {
  const { data } = await axiosClient.get('/meeting/all');
  return data;
};

export const getMeetingById = async (id) => {
  const { data } = await axiosClient.get(`/meeting/${id}`);
  return data;
};

export const uploadMeeting = async ({ file, title }) => {
  const formData = new FormData();
  formData.append('file', file);
  if (title) {
    formData.append('title', title);
  }

  const { data } = await axiosClient.post('/meeting/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });

  return data;
};
