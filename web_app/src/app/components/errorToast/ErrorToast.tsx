'use client';

import { useEffect, useState } from 'react';
import styles from "@/app/components/errorToast/errorToastStyle.module.scss";

type ErrorToastProps = {
  message: string;
  show: boolean;
  onHide: () => void;
};

export const ErrorToast: React.FC<ErrorToastProps> = ({ message, show, onHide }) => {
  const [visible, setVisible] = useState(show);

  useEffect(() => {
    if (show) {
      setVisible(true);
      const timer = setTimeout(() => {
        setVisible(false);
        onHide();
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [show, onHide]);

  if (!visible) return null;

  return (
    <div className={styles.error_toast}>
      {message}
    </div>
  );
};
