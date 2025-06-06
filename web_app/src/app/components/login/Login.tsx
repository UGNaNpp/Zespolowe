"use client";

// import { signIn, signOut, useSession } from "next-auth/react";
import { signIn } from "next-auth/react";
import Image from 'next/image';

import styles from './loginStyle.module.scss';

type Props = {
  dict: {
    title: string;
    button: string;
  };
};

export default function Login({ dict }: Props) {
  const loginTitle = process.env.NEXT_PUBLIC_LOGIN_TITLE || dict.title;

  return (
    <div className={styles.background}>
      <div className={styles.mainForm}>
        <h2>
        <Image
            className={styles.logo}
            src="/logo.png"
            alt="SmartSecurity logo"
            width={38}
            height={38}
            priority
          />
          {loginTitle}
        </h2>
        <div className={styles.buttonDiv}>
          <button onClick={() => signIn("github", { callbackUrl: "/", method: "POST" })}>
            <i className="fa-brands fa-github"></i>
            <span>{dict.button}</span>
          </button>
        </div>
      </div>
    </div>
  );
}
