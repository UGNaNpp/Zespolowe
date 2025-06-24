'use client';

import { useRouter, usePathname } from 'next/navigation';
import styles from '@/app/components/settings/settingsStyle.module.scss';
import Cookies from 'js-cookie';

const localeNames: Record<string, string> = {
  en: 'English',
  pl: 'Polski',
};

const locales = Object.keys(localeNames);

export default function Settings() {
  const router = useRouter();
  const pathname = usePathname();
  const pathWithoutLang = pathname.replace(/^\/[^\/]+/, '');

  const setLanguage = (locale: string) => {
    console.log(pathname);
    Cookies.set('NEXT_LOCALE', locale);
    router.push(`/${locale}${pathWithoutLang}`);
  };

  const currentLocale = pathname.split('/')[1] || 'system';

  return (
    <div className={styles.menu}>
      <label htmlFor="language-select">Wybierz język:</label>
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
  );
}
