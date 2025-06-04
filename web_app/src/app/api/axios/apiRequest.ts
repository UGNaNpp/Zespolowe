import axios, { AxiosRequestConfig, Method } from "axios";
import api from "@/app/api/axios/axios";

type ErrorDict = {
  "unknown": string;
  "400": string;
  "401": string;
  "403": string;
  "404": string;
  "500": string;
};

type ApiRequestParams<T> = {
  endpoint: string;
  method: Method;
  data?: any;
  headers?: Record<string, string>;
  params?: Record<string, any>;
  onSuccess?: (data: T) => void;
  onError?: (error: string) => void;
  dict: ErrorDict;
};

export async function apiRequest<T>({
  endpoint,
  method,
  data,
  headers = { "Content-Type": "application/json" },
  params,
  onSuccess,
  onError,
  dict,
}: ApiRequestParams<T>): Promise<void> {
  try {
    const config: AxiosRequestConfig = {
      url: endpoint,
      method,
      data,
      headers,
      params,
    };

    const response = await api(config);

    if (onSuccess) {
      onSuccess(response.data);
    }
  } catch (err: any) {
    let errorMessage = dict["unknown"];

    if (axios.isAxiosError(err)) {
      const status = err.response?.status;
      const serverMsg = err.response?.data?.message;

      if (status) {
        const statusKey = String(status) as keyof ErrorDict;

        if (dict[statusKey]) {
          errorMessage = dict[statusKey];
        } else if (serverMsg) {
          errorMessage = serverMsg;
        } else {
          errorMessage = `Błąd ${status}`;
        }
      }
    }

    if (onError) {
      onError(errorMessage);
    }
  }
}
