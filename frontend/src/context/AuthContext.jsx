import { createContext, useContext, useEffect, useState } from 'react';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('jwt_token'));
  const [email, setEmail] = useState(() => localStorage.getItem('jwt_email'));

  useEffect(() => {
    if (token) {
      localStorage.setItem('jwt_token', token);
    } else {
      localStorage.removeItem('jwt_token');
    }
  }, [token]);

  useEffect(() => {
    if (email) {
      localStorage.setItem('jwt_email', email);
    } else {
      localStorage.removeItem('jwt_email');
    }
  }, [email]);

  const value = {
    token,
    email,
    isAuthenticated: Boolean(token),
    login: (authResponse) => {
      setToken(authResponse.token);
      setEmail(authResponse.email);
    },
    logout: () => {
      setToken(null);
      setEmail(null);
    }
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
