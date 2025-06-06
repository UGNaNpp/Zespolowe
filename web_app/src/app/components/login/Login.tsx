"use client";

import { signIn, useSession } from "next-auth/react";
import { useEffect } from "react";
import { useRouter } from "next/navigation";
import Image from 'next/image';
import styles from '@/app/components/login/loginStyle.module.scss';

type Props = {
  dict: {
    pageTitle: string;
    title: string;
    button: string;
  };
};

export default function Login({ dict }: Props) {
  const loginTitle = process.env.NEXT_PUBLIC_LOGIN_TITLE || dict.title;
  const { data: session, status } = useSession();
  const router = useRouter();

  useEffect(() => {
    if (status === "authenticated") {
      router.push("/");
    }
  }, [status, router]);

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
