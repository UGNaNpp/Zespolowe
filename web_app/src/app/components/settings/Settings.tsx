'use client';

import { useEffect, useState } from "react";
import { useRouter, usePathname } from 'next/navigation';
import styles from '@/app/components/settings/settingsStyle.module.scss';
import Cookies from 'js-cookie';
import { ErrorToast } from "@/app/components/errorToast/ErrorToast";
import { apiRequest } from "@/app/api/axios/apiRequest";
import { User } from "@/types/user";
import { UserAddForm } from "@/app/components/addUserForm/UserAddForm";
import Image from 'next/image';

type Props = {
  dict: {
    pageTitle: string;
    language: string;
    pickLanguage: string;
    userDeleted: string;
    users: string;
    noUsers: string;
    delete: string;
    deleteConfirm: string;
    yes: string;
    no: string;
    addUser: string;
  },
  ApiErrorsDict: {
    "unknown": string;
    "400": string;
    "401": string;
    "403": string;
    "404": string;
    "500": string;
  },
  addUserForm: {
    "addUser": string;
    "login": string;
    "add": string;
    "cancel": string;
    "addedUser": string;
    "notFound": string;
  }
};

const localeNames: Record<string, string> = {
  en: 'English',
  pl: 'Polski',
};

const locales = Object.keys(localeNames);

export default function Settings({ dict, ApiErrorsDict, addUserForm }: Props) {
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState<string>("");
  const [refreshFlag, setRefreshFlag] = useState(false);
  const [users, setUsers] = useState<User[]>([]);
  const [selectedLogin, setSelectedLogin] = useState<string | null>(null);
  const [showConfirmDelete, setShowConfirmDelete] = useState(false);
  const [showAddUserForm, setShowAddUserForm] = useState(false);

  const router = useRouter();
  const pathname = usePathname();
  const pathWithoutLang = pathname.replace(/^\/[^\/]+/, '');

  const setLanguage = (locale: string) => {
    Cookies.set('NEXT_LOCALE', locale);
    router.push(`/${locale}${pathWithoutLang}`);
  };

  const currentLocale = pathname.split('/')[1] || 'system';

  useEffect(() => {
      apiRequest<User[]>({
        endpoint: "/users/",
        method: "GET",
        onSuccess: (data) => {
          setUsers(Object.values(data));
        },
        onError: (errMsg) => {
          setToastMessage(errMsg);
          setShowToast(true);
        },
        dict: ApiErrorsDict,
      });

    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [refreshFlag]);

  const handleDeleteClick = (login: string) => {
    setSelectedLogin(login);
    setShowConfirmDelete(true);
  };

  const cancelDelete = () => {
    setShowConfirmDelete(false);
    setSelectedLogin(null);
  };

  const confirmDelete = () => {
    if (!selectedLogin) return;
    setShowConfirmDelete(false);

    apiRequest<void>({
      endpoint: `/users/${selectedLogin}`,
      method: "DELETE",
      onSuccess: () => {
        setToastMessage(`${selectedLogin} ${dict.userDeleted}`);
        setShowToast(true);
        setRefreshFlag(prev => !prev);
        setSelectedLogin(null);
      },
      onError: (errMsg) => {
        setToastMessage(errMsg);
        setShowToast(true);
        setSelectedLogin(null);
      },
      dict: ApiErrorsDict,
    });
  };

  const handleAddUserClick = () => {
    setShowAddUserForm(true);
  };

  const cancelAddUser = () => {
    setShowAddUserForm(false);
  };

  return (
    <>
      <main className={styles.main}>
        <div className={styles.menu}>
          <div className={styles.language}>
            <h2>{dict.language}</h2>
            <div className={styles.languageSelection}>
              <label htmlFor="language-select">{dict.pickLanguage}:</label>
              <select
                id="language-select"
                value={currentLocale === 'en' || currentLocale === 'pl' ? currentLocale : 'system'}
                onChange={(e) => {
                  const val = e.target.value;
                  setLanguage(val);
                }}
              >
                {locales.map((locale) => (
                  <option key={locale} value={locale}>
                    {localeNames[locale]}
                  </option>
                ))}
              </select>
            </div>
          </div>
          <div className={styles.users}>
            <h2>{dict.users}:</h2>
            <button className={styles.addUserButton} onClick={handleAddUserClick}>
              {dict.addUser}
            </button>
            {users.length === 0 ? (
              <p>{dict.noUsers}</p>
            ) : (
              <ul>
                {users.map(user => (
                  <li key={user.login} className={styles.userItem}>
                    <div className={styles.userInfo}>
                      <Image
                        className={styles.avatar}
                        src={user.avatarUrl ?? '/default-avatar.png'}
                        alt="User logo"
                        width={32}
                        height={32}
                        priority
                      />
                      <span>{user.login} - {user.name}</span>
                    </div>
                    <button className={styles.deleteButton} onClick={() => handleDeleteClick(user.login)}>
                      {dict.delete}
                    </button>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>
      </main>
      {showConfirmDelete && (
        <div className={styles.modalOverlay}>
          <div className={styles.confirmModal}>
            <p>{dict.deleteConfirm}</p>
            <div className={styles.modalButtons}>
              <button onClick={confirmDelete}>{dict.yes}</button>
              <button onClick={cancelDelete}>{dict.no}</button>
            </div>
          </div>
        </div>
      )}
      <ErrorToast
        message={toastMessage}
        show={showToast}
        onHide={() => setShowToast(false)}
      />
      {showAddUserForm && (
        <div className={styles.modalOverlay}>
          <div className={styles.formModal}>
            <UserAddForm
              dict={addUserForm}
              ApiErrorsDict={ApiErrorsDict}
                onClose={() => {
                cancelAddUser();
                setRefreshFlag(prev => !prev);
              }}
            />
          </div>
        </div>
      )}
    </>
  );
}
