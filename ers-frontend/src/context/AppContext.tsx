import React, {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  ReactNode,
} from 'react';
import { AuthUser } from '../types';
import { API_BASE_URL, SESSION_USER_KEY } from '../shared/utils/constants';

interface AppContextValue {
  user: AuthUser | null;
  baseUrl: string;
  setUser: (user: AuthUser | null) => void;
  clearUser: () => void;
}

const AppContext = createContext<AppContextValue | undefined>(undefined);

function readStoredUser(): AuthUser | null {
  try {
    const raw = sessionStorage.getItem(SESSION_USER_KEY);
    if (!raw) {
      return null;
    }
    return JSON.parse(raw) as AuthUser;
  } catch {
    return null;
  }
}

export const AppProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUserState] = useState<AuthUser | null>(() => readStoredUser());

  const setUser = useCallback((next: AuthUser | null) => {
    setUserState(next);
    if (next) {
      sessionStorage.setItem(SESSION_USER_KEY, JSON.stringify(next));
    } else {
      sessionStorage.removeItem(SESSION_USER_KEY);
    }
  }, []);

  const clearUser = useCallback(() => {
    setUserState(null);
    sessionStorage.clear();
  }, []);

  const value = useMemo(
    () => ({
      user,
      baseUrl: API_BASE_URL,
      setUser,
      clearUser,
    }),
    [user, setUser, clearUser]
  );

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
};

export const useAppContext = (): AppContextValue => {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useAppContext must be used within an AppProvider');
  }
  return context;
};
