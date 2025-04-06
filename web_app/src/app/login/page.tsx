"use client";

// import { signIn, signOut, useSession } from "next-auth/react";
import { signIn } from "next-auth/react";

import styles from './loginStyle.module.scss';

export default function Login() {
  const loginTitle = process.env.NEXT_PUBLIC_LOGIN_TITLE;

  return (
    <div className={styles.background}>
      <div className={styles.mainForm}>
        <h2>{loginTitle}</h2>
        <div className={styles.buttonDiv}>
          <button onClick={() => signIn("github", { callbackUrl: "/", method: "POST" })}>
            <i className="fa-brands fa-github"></i>
            <span>Login with github</span>
          </button>
        </div>
      </div>
    </div>
  );
}
