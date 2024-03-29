import axios, { AxiosResponse, AxiosError } from 'axios';
import { ImageType } from '@/image';
import { Ref } from 'vue';
import { AlgoTypes } from './algorithms';
import { request } from 'http';

const instance = axios.create({
  baseURL: "/",
  timeout: 40000,
});

const responseBody = (response: AxiosResponse) => response.data;

const requests = {
  get: (url: string, param: {}) => instance.get(url, param).then(responseBody),
  post: (url: string, body: {}) => instance.post(url, body, { headers: { "Content-Type": "multipart/form-data" }, }).then(responseBody),
  put: (url: string, body: {}) => instance.put(url, body).then(responseBody),
  delete: (url: string) => instance.delete(url).then(responseBody)
};

export const api = {
  getImageList: (): Promise<ImageType[]> => requests.get('images', {}),
  getImage: (id: number): Promise<Blob> => requests.get(`images/${id}`, { responseType: "blob" }),
  createImage: (form: FormData): Promise<ImageType> => requests.post('images', form),
  deleteImage: (id: number): Promise<void> => requests.delete(`images/${id}`),
  getAlgoList: (): Promise<AlgoTypes[]> => requests.get('images/algorithms', {}),
  getAlgo: (id: number,name: String,param: String): Promise<Blob> => requests.get(`images/${id}?algorithm=${name}${param}`, {responseType: "blob"})
};