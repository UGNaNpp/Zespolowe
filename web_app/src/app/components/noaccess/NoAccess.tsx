'use client';

import { useRouter } from 'next/navigation';
import styles from '@/app/components/noaccess/noaccessStyle.module.scss';

type Props = {
  dict: {
    pageTitle: string;
    title: string;
    message: string;
    button: string;
  };
};

export default function NoAccess({ dict }: Props) {
  const router = useRouter();

  const handleClick = () => {
    router.push('/');
  };

  return (
    <>
      <div className={styles.messageBox}>
        <h2 className={styles.title}>{dict.title}</h2>
        <div className={styles.message}>{dict.message}</div>
        <button className={styles.button} onClick={handleClick}>
          {dict.button}
        </button>
      </div>
      <div className={styles.footer}>Smarty Security 2025</div>
    </>
  );
}