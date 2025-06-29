"use client";

import { useState } from 'react';
import Image from "next/image";
import Link from "next/link";
import styles from '@/app/components/navbar/NavBarStyle.module.scss';

interface NavBarTitle {
  title: string;
  subtitle: string;
  titleUrl: string;
  subtitleUrl: string;
}

type Props = NavBarTitle & {
  dict: {
    dashboard: string;
    devices: string;
    media: string;
    settings: string;
    logout: string;
    version: string;
  };
};

const NavBar: React.FC<Props> = ({ title, titleUrl, subtitle, subtitleUrl, dict }) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const openMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  const appVersion = process.env.NEXT_PUBLIC_APP_VERSION;

  return (
    <header className={styles.navbar}>
      <div className={styles.navBarLeft}>
        <div className={styles.hamburger} onClick={openMenu}>
          <i className="fa-solid fa-bars fa-xl"></i>
        </div>
        <Link href="/">
          <Image
            className={styles.logo}
            src="/logo.png"
            alt="SmartSecurity logo"
            width={32}
            height={32}
            priority
          />
        </Link>
        {!isMenuOpen && (
          <>
            {title && (
              <Link href={titleUrl}>
                <span>
                  {title}
                </span>
              </Link>
            )}
            {title && subtitle && (
              <span> / </span>
            )}
            {subtitle && (
              <Link href={subtitleUrl}>
                <span>
                  {subtitle}
                </span>
              </Link>
            )}
          </>
        )}
      </div>
      <div className={`${styles.menu} ${isMenuOpen ? styles.menuOpen : ''}`}>
        <div className={styles.openMenuMain}>
          <div className={styles.openedMenuTop}>
            <div className={styles.logoAndHeader}>
              <Link href="/dashboard">
                <Image
                  className={styles.logo}
                  src="/logo.png"
                  alt="SmartSecurity logo"
                  width={32}
                  height={32}
                  priority
                />

                <h3>Smart Security</h3>
              </Link>
            </div>
            <div className={styles.closeMenu} onClick={openMenu}>
              <i className="fa-solid fa-xmark fa-xl"></i>
            </div>
          </div>
          <ul className={styles.menuList}>
            <li>
              <Link href="/dashboard">
                <i className="fa-regular fa-bookmark"></i>
                <span>{dict.dashboard}</span>
              </Link>
            </li>
            <li>
              <Link href="/devices">
                <i className="fa-regular fa-compass"></i>
                <span>{dict.devices}</span>
              </Link>
            </li>
            <li>
              <Link href="/media">
                <i className="fa-regular fa-folder-open"></i>
                <span>{dict.media}</span>
              </Link>
            </li>
            <li>
              <Link href="/settings">
                <i className="fa-solid fa-gear"></i>
                <span>{dict.settings}</span>
              </Link>
            </li>
          </ul>
        </div>
        <div className={styles.openMenuFooter}>
          <ul>
            <li>
              <Link href="/logout">
                <i className="fa-solid fa-arrow-right-from-bracket"></i>
                <span>{dict.logout}</span>
              </Link>
            </li>
          </ul>
          <span>{dict.version}: {appVersion}</span>
        </div>
      </div>
    </header>
  );
};

export default NavBar;
