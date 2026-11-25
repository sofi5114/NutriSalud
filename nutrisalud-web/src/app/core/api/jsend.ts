export interface JSend<T> {
  status: 'success' | 'fail' | 'error';
  data: T;
  message?: string;
}
