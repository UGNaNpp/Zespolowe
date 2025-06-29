"use client";

import { useState, useEffect } from "react";
import styles from '@/app/components/stream/StreamStyle.module.scss';
import { apiRequest } from "@/app/api/axios/apiRequest";
import { Device } from "@/types/device";
import { ErrorToast } from "@/app/components/errorToast/ErrorToast";

type Props = {
  deviceId: string;
  dict: {
    pageTitle: string;
    offline: string;
    seconds: string;
    minute: string;
    minutes: string;
    startRecording: string;
    ip: string;
  },
  ApiErrorsDict: {
    "unknown": string;
    "400": string;
    "401": string;
    "403": string;
    "404": string;
    "500": string;
  }
};

export default function Stream({ deviceId, dict, ApiErrorsDict }: Props) {
  const streamUrl = `/api/stream/${deviceId}`;
  const [isOffline, setIsOffline] = useState(false);
  const [device, setDevice] = useState<Device | undefined>();
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState<string>("");

  const handleError = () => {
    setIsOffline(true);
  };

  useEffect(() => {
    apiRequest<Device>({
      endpoint: `/devices/id/${deviceId}`,
      method: "GET",
      onSuccess: (data) => {
        setDevice(data);
      },
      onError: (errMsg) => {
        setToastMessage(errMsg);
        setShowToast(true);
      },
      dict: ApiErrorsDict,
    })
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <>
    <main className={styles.main}>
      <div className={styles.streamBox}>
        {isOffline ? (
          <span>{dict.offline}</span>
        ) : (
          // eslint-disable-next-line @next/next/no-img-element
          <img
            className={styles.stream}
            src={streamUrl}
            alt="Stream"
            onError={handleError}
          />
        )}
      </div>
      { device && (
        <>
        <h2>{device.name}</h2>
        <div className={styles.info}>
          {dict.ip}: {device.AssociatedIP}
        </div>
        </>
      )
      }
    </main>
    <ErrorToast
      message={toastMessage}
      show={showToast}
      onHide={() => setShowToast(false)}
    />
    </>
  );
}
