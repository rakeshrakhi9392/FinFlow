import { AxiosError } from 'axios';

export function getErrorMessage(error: unknown, fallback = 'Something went wrong'): string {
  if (typeof error === 'string') {
    return error;
  }

  if (error instanceof AxiosError) {
    const data = error.response?.data;
    if (typeof data === 'string' && data.trim()) {
      return data;
    }
    if (data && typeof data === 'object' && 'message' in data) {
      const message = (data as { message?: unknown }).message;
      if (typeof message === 'string' && message.trim()) {
        return message;
      }
    }
    if (error.message) {
      return error.message;
    }
  }

  if (error instanceof Error && error.message) {
    return error.message;
  }

  return fallback;
}
