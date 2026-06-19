import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import type { UserRole } from '@/types';
import { RoleEnum } from '@/constants';

interface UserContextType {
  userRole: UserRole;
  userName: string;
  userId: string;
  setUserRole: (role: UserRole) => void;
  setUserName: (name: string) => void;
  setUserId: (id: string) => void;
}

const UserContext = createContext<UserContextType | undefined>(undefined);

export const UserProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [userRole, setUserRole] = useState<UserRole>(() => {
    const saved = localStorage.getItem('userRole');
    return (saved as UserRole) || RoleEnum.FIELD_INSPECTOR;
  });

  const [userName, setUserName] = useState(() => {
    return localStorage.getItem('userName') || '巡查员';
  });

  const [userId, setUserId] = useState(() => {
    return localStorage.getItem('userId') || 'user001';
  });

  useEffect(() => {
    localStorage.setItem('userRole', userRole);
  }, [userRole]);

  useEffect(() => {
    localStorage.setItem('userName', userName);
  }, [userName]);

  useEffect(() => {
    localStorage.setItem('userId', userId);
  }, [userId]);

  return (
    <UserContext.Provider
      value={{ userRole, userName, userId, setUserRole, setUserName, setUserId }}
    >
      {children}
    </UserContext.Provider>
  );
};

export const useUser = () => {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error('useUser must be used within a UserProvider');
  }
  return context;
};
