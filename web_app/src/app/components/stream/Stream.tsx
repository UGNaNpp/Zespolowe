"use client";

import { useState } from "react";
import styles from '@/app/components/stream/StreamStyle.module.scss';

type Props = {
  deviceId: string;
  dict: {
    pageTitle: string;
    offline: string;
  };
};

export default function Stream({ deviceId, dict }: Props) {
  const streamUrl = `/api/stream/${deviceId}`;
  const [isOffline, setIsOffline] = useState(false);

  const handleError = () => {
    setIsOffline(true);
  };

  return (
    <div className={styles.streamBox}>
      {isOffline ? (
        <span>{dict.offline}</span>
      ) : (
        <img
          className={styles.stream}
          src={streamUrl}
          alt="Stream"
          onError={handleError}
        />
      )}
    </div>
  );
}
