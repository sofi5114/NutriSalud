import { inject } from '@angular/core';
import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthApi } from './auth-api';

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(AuthApi).token();

  if (!token || !req.url.startsWith(environment.apiUrl)) {
    return next(req);
  }

  const conToken = req.clone({
    setHeaders: { Authorization: `Bearer ${token}` }
  });
  return next(conToken);
};
