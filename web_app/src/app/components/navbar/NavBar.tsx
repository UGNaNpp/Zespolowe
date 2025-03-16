"use client";

import React, { useState } from 'react';
import Image from "next/image";
import Link from "next/link";
import styles from './NavBarStyle.module.scss';

const NavBar: React.FC = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const openMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

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
              </Link>

              <h3>Smart Security</h3>

            </div>
            <div className={styles.closeMenu} onClick={openMenu}>
              <i className="fa-solid fa-xmark fa-xl"></i>
            </div>
          </div>
          <ul className={styles.menuList}>
            <li><a href="#">Home</a></li>
            <li><a href="#">Devices</a></li>
            <li><a href="#">Media</a></li>
            <li><a href="#">Notifications</a></li>
            <li><a href="#">Account</a></li>
          </ul>
        </div>
        <div className={styles.openMenuFooter}>
          <ul>
            <li><a href="#">Logout</a></li>
          </ul>
          <span>App version: 0.0.1</span>
        </div>
      </div>
    </header>
  );
};

export default NavBar;
