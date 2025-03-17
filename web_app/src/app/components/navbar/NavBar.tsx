"use client";

import React, { useState } from 'react';
import Image from "next/image";
import Link from "next/link";
import styles from './NavBarStyle.module.scss';

interface NavBarTitle {
  title: string;
  subtitle: string;
  titleUrl: string;
  subtitleUrl: string;
}

const NavBar: React.FC<NavBarTitle> = ({ title, titleUrl, subtitle, subtitleUrl }) => {
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
            width={38}
            height={38}
            priority
          />
        </Link>
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
      </div>
      <div className={`${styles.menu} ${isMenuOpen ? styles.menuOpen : ''}`}>
        <div className={styles.openMenuMain}>
          <div className={styles.openedMenuTop}>
            <div className={styles.logoAndHeader}>
              <Link href="/">
                <Image
                  className={styles.logo}
                  src="/logo.png"
                  alt="SmartSecurity logo"
                  width={38}
                  height={38}
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
              <Link href="/home">
                <i className="fa-regular fa-bookmark"></i>
                <span>Home</span>
              </Link>
            </li>
            <li>
              <Link href="/devices">
                <i className="fa-regular fa-compass"></i>
                <span>Devices</span>
              </Link>
            </li>
            <li>
              <Link href="/media">
                <i className="fa-regular fa-folder-open"></i>
                <span>Media</span>
              </Link>
            </li>
            <li>
              <Link href="/notifications">
                <i className="fa-regular fa-bell"></i>
                <span>Notifications</span>
              </Link>
            </li>
            <li>
              <Link href="/account">
                <i className="fa-regular fa-circle-user"></i>
                <span>Account</span>
              </Link>
            </li>
          </ul>
        </div>
        <div className={styles.openMenuFooter}>
          <ul>
            <li>
              <Link href="/logout">
                <i className="fa-regular fa-rectangle-xmark"></i>
                <span>Logout</span>
              </Link>
            </li>
          </ul>
          <span>App version: {appVersion}</span>
        </div>
      </div>
    </header>
  );
};

export default NavBar;
